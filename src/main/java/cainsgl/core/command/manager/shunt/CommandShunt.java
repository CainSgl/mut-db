package cainsgl.core.command.manager.shunt;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.persistence.MutSerializer;
import cainsgl.core.system.thread.ThreadManager;
import cainsgl.core.utils.SerialiUtil;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class CommandShunt implements MutSerializer
{
    private final List<CommandShuntComponent>[] processors;
    private final EventLoopGroup WORK_GROUP;
    private final EventLoop volumeThread;
    private AtomicReference<Thread> serializerThread = null;

    //长度前缀法
    @Override
    public byte[] serialization()
    {
        List<CommandShuntComponent> processor = processors[0];
        int allLength = 0;
        List<byte[]> allData = new ArrayList<>();
        try
        {
            for (CommandShuntComponent c : processor)
            {
                byte[] serialization = c.manager.serializationByFuture().get();
                allData.add(serialization);
                allLength += serialization.length;
            }
        } catch (Exception e)
        {
            MutConfiguration.log.error("获取序列化数据的时候获取失败，出现异常", e);
        }
        //组合，并且由于我有多个serialization,后面反序列化也要扩张
        //记录下长度
        allLength += processor.size() * 4;
        byte[] serialization = new byte[allLength];
        int i = 0;
        for (byte[] bytes : allData)
        {
            i = SerialiUtil.writeDataToInt(serialization, i, bytes.length);
            //写入数据
            System.arraycopy(bytes, 0, serialization, i, bytes.length);
            i += bytes.length;
        }
        return serialization;
    }

    @Override
    public void deSerializer(byte[] data)
    {
        EventLoop eventLoop = ThreadManager.getEventLoop();
        eventLoop.submit(() -> {
            serializerThread = new AtomicReference<>(Thread.currentThread());
            List<byte[]> result = new ArrayList<>();
            int offset = 0;
            while (offset < data.length)
            {
                // 读取长度（4字节，大端序）
                int length = SerialiUtil.readIntFromBytes(data, offset); // 需实现反向解析
                offset += 4;
                // 读取对应长度的数据
                byte[] subData = new byte[length];
                System.arraycopy(data, offset, subData, 0, length);
                offset += length;
                result.add(subData);
            }

            ShuntManagerProxy<?> manager = processors[0].getFirst().manager;
            manager.deSerializer(result.getFirst());
            int count = 1;//计数器，代表第几个被序列化了
            while (count < result.size())
            {
                //说明需要去创建数据
                if (count >= processors[0].size())
                {
                    //说明数据不足了，必须等待
                    manager.create();
                    serializerThread.compareAndSet(null, Thread.currentThread());
                    LockSupport.park();
                }
                //这里是被唤醒和数据充足的时候，说明肯定都行
                while (count >= processors[0].size())//再次检验
                {
                    //防止虚假唤醒
                    MutConfiguration.log.info("CommandShunt里在没有添加的数据情况下被唤醒");
                    serializerThread.compareAndSet(null, Thread.currentThread());
                    LockSupport.park();
                }
                processors[0].get(count).manager.deSerializer(result.get(count));
                count++;
            }
            serializerThread = null;
            ThreadManager.backEventLoop(eventLoop);
        });
    }


    private static class CommandShuntComponent extends CommandProcessor<ShuntCommandManager<?>>
    {
        final CommandProcessor<ShuntCommandManager<?>> proxy;
        public final ShuntManagerProxy<?> manager;
        public final EventLoop eventLoop;

        public CommandShuntComponent(ShuntManagerProxy<?> manager, CommandProcessor<ShuntCommandManager<?>> proxy, EventLoop e)
        {
            super(proxy.minCount(), proxy.maxCount(), proxy.commandName(), proxy.parameters());
            this.proxy = proxy;
            this.manager = manager;
            this.eventLoop = e;
        }

        public void shuntExecute(byte[][] args, Consumer<RESP2Response> consumer)
        {
            eventLoop.execute(() ->
            {
                try{
                    RESP2Response execute = proxy.execute(args, manager.proxy);
                    consumer.accept(execute);
                }catch (Exception e)
                {
                    manager.exceptionCaught(e);
                }
            });
        }


        @Override
        public RESP2Response execute(byte[][] args, ShuntCommandManager<?> manager)
        {
            throw new UnsupportedOperationException("不支持直接调用分流器的执行方法");
        }
    }

    private static class ShuntManagerProxy<D> implements ShuntManager<D>, MutSerializer
    {
        public final ShuntCommandManager<D> proxy;
        final EventLoop eventLoop;

        public ShuntManagerProxy(ShuntCommandManager<D> shuntCommandManager, EventLoop eventLoop)
        {
            this.proxy = shuntCommandManager;
            ThreadManager.register(proxy, eventLoop);
            this.eventLoop = eventLoop;
        }
        @Override
        public final void exceptionCaught(Exception e)
        {
            eventLoop.execute(() ->
            {
                proxy.exceptionCaught(e);
            });
        }
        @SafeVarargs
        @Override
        public final void create(D... data)
        {
            List<D> array = new ArrayList<>(data.length);
            for (int i = 0; i < data.length; i++)
            {
                if (data[i] != null)
                {
                    array.add(data[i]);
                }
            }
            proxy.createImpl(array);
        }

        @Override
        public Future<D> separate()
        {
            Promise<D> promise = eventLoop.newPromise();
            eventLoop.submit(() -> {
                D d = proxy.separateImpl();
                promise.setSuccess(d);
            });
            return promise;
        }

        @Override
        public void setTester(Tester tester)
        {
            eventLoop.submit(() -> {
                proxy.setTester(tester);
            });
        }

        @Override
        public Future<Integer> overLoad()
        {
            Promise<Integer> promise = eventLoop.newPromise();
            eventLoop.submit(() -> {
                promise.setSuccess(proxy.overLoadImpl());
            });
            return promise;
        }

        @Override
        public Future<D> destory()
        {
            Promise<D> promise = eventLoop.newPromise();
            eventLoop.submit(() -> {
                promise.setSuccess(proxy.destoryImpl());
                ThreadManager.unRegister(proxy);
            });
            return promise;
        }

        public void addData(Object data)
        {
            eventLoop.submit(() -> {
                proxy.addData((D) data);
            });
        }

        public Future<byte[]> serializationByFuture()
        {
            Promise<byte[]> promise = eventLoop.newPromise();
            eventLoop.submit(() -> {
                byte[] serialization = proxy.serialization();
                promise.setSuccess(serialization);
            });
            return promise;
        }

        @Override
        public byte[] serialization()
        {
            return proxy.serialization();
        }

        @Override
        public void deSerializer(byte[] data)
        {
            eventLoop.submit(() -> {
                proxy.deSerializer(data);
            });
        }
    }

    public CommandShunt(ShuntCommandManager shuntCommandManager, CommandProcessor... proxy)
    {
        WORK_GROUP = ThreadManager.getEventLoopGroup(MutConfiguration.shuntThreads);
        volumeThread = ThreadManager.getEventLoop();
        processors = new List[proxy.length];
        EventLoop eventLoop = ThreadManager.getEventLoop();
        for (int i = 0; i < proxy.length; i++)
        {
            processors[i] = new ArrayList<>();
            processors[i].add(new CommandShuntComponent(new ShuntManagerProxy(shuntCommandManager, eventLoop), proxy[i], eventLoop));
            MutConfiguration.log.info("create the proxy for shunt,command: {}", proxy[i].commandName());
        }

    }

    public void shunt(ShuntCaller caller, byte[][] args, Consumer<RESP2Response> consumer)
    {
        WORK_GROUP.submit(() -> {
            int record = caller.getRecord();
            List<CommandShuntComponent> commandShuntComponents = processors[record];
            autoVolume(commandShuntComponents);
            CommandShuntComponent executor = commandShuntComponents.get(new ByteFastKey(args[0]).hashCode() % commandShuntComponents.size());
            executor.shuntExecute(args, consumer);
        });
    }

    boolean isVolume = false;

    private void autoVolume(List<CommandShuntComponent> executors)
    {
        volumeThread.submit(() -> {
            if (isVolume)
            {
                return;
            }
            isVolume = true;

            int size = executors.size();
            List<Future<Integer>> futures = new ArrayList<>(size);
            for (CommandShuntComponent executor : executors)
            {
                Future<Integer> future = executor.manager.overLoad();
                futures.add(future);
            }
            try
            {
                int AlloverLoad = 0;
                for (Future<Integer> future : futures)
                {
                    AlloverLoad += future.get();
                }
                if (AlloverLoad / size > MutConfiguration.MAX_OVER_LOAD)
                {
                    //需要扩容
                    //1.先去设置tester，告诉分流组件，哪些key会被移除
                    for (int i = 0; i < size; i++)
                    {
                        final int finalI = i;
                        executors.get(i).manager.setTester(key -> new ByteFastKey(key).hashCode() % (size + 1) == finalI);
                    }
                    //      Future[] separates = new Future[size];
//                    for (int i = 0; i < size; i++)
//                    {
//                        separates[i] = ;
//                    }
                    //防止全量阻塞，这里本来是不阻塞的，但是为了防止别人开发命令不规范
                    Object[] datas = new Object[size];
                    for (int i = 0; i < size; i++)
                    {
                        datas[i] = executors.get(i).manager.separate().get();
                    }
                    ShuntManagerProxy manager = executors.getFirst().manager;
                    manager.create(datas);
                } else if (size>3&&AlloverLoad / size < MutConfiguration.MIN_OVER_LOAD)
                {
                    //销毁最后一个
                    for (int i = 0; i < size; i++)
                    {
                        //设置tester，跳过最后一个
                        final int finalI = i;
                        executors.get(i).manager.setTester(key -> new ByteFastKey(key).hashCode() % (size - 1) == finalI);
                    }
                    //提醒最后一个，给他把数据删除
                    for (List<CommandShuntComponent> processor : processors)
                    {
                        processor.removeLast();
                    }
                    Object o = processors[0].getLast().manager.destory().get();
                    //这里如果在分流处理，由于阻塞，不会导致不一致，只有
                    for (int i = 0; i < size; i++)
                    {
                        executors.get(i).manager.addData(o);
                    }
                }
                return;
            } catch (Exception e)
            {
                MutConfiguration.log.error("====>不期望的错误", e);
            } finally
            {
                isVolume = false;
            }
        });
    }


    public void addProcessors(ShuntCommandManager shuntCommandManager, CommandProcessor... proxys)
    {
        volumeThread.submit(() -> {
            EventLoop eventLoop = ThreadManager.getEventLoop();
            try
            {
                for (int i = 0; i < processors.length; i++)
                {
                    if (proxys[i].minCount() < 1)
                    {
                        // throw new UnsupportedOperationException("不支持没有key的CommandProcessor使用分流器");
                        while (true)
                        {
                            MutConfiguration.log.error("不支持没有key的CommandProcessor使用分流器,command: {}", proxys[i].commandName());
                        }
                    }
                    //检验一下，是否他们的名称相同
                    if (!proxys[i].commandName().equals(processors[i].getFirst().commandName()))
                    {
                        throw new RuntimeException("在再次构建manager的时候，你的processor的顺序与第一次不符合");
                    }
                    processors[i].add(new CommandShuntComponent(new ShuntManagerProxy(shuntCommandManager, eventLoop), proxys[i], eventLoop));
                }
            } catch (ArrayIndexOutOfBoundsException e)
            {
                MutConfiguration.log.error("错误，在再次构建manager的时候，你的processor少于第一次构建的数量");
            } catch (Exception e)
            {
                MutConfiguration.log.error("发生错误", e);
            }
            if (serializerThread != null)
            {
                //唤醒他
                if (serializerThread.get() != null)
                {
                    LockSupport.unpark(serializerThread.getAndSet(null));
                }
            }
        });
    }


//        WORK_GROUP.submit(()->{
//            int index = caller.inManagerIndex;
//            List<CommandProcessor<? extends  CommandManager>> executor = processors[index];
//            if(caller.isWillOverLoad())
//            {
//                autoVolume(executor,caller);
//            }
//            ShuntExecutor<Object> shuntExecutor = executor.get(new ByteFastKey(args[0]).hashCode() % executor.size());
//            shuntExecutor.submit(args, consumer);
//        });
//    }
//
//
//
//    boolean isJudge=false;
//    private void autoVolume(List<CommandProcessor<?>> executors, ShuntProcessorProxy caller)
//    {
//        if(isJudge)
//        {
//            return;
//        }
//        isJudge = true;
//        volumeThread.submit(() -> {
//            int overLoadCount = 0;
//            for (ShuntExecutor<Object> executor : executors)
//            {
//                if (executor.overLoad())
//                {
//                    overLoadCount++;
//                }
//            }
//            int size = executors.size();
//            if (overLoadCount > size / 2)
//            {
//                //扩容
//                for (int i = 0; i <size; i++)
//                {
//                    int finalI = i;
//                    executors.get(i).setTester((byte[] key) -> finalI == new ByteFastKey(key).hashCode() % size);
//                }
//                Future<Object>[] futures = new Future[size];
//                for(int i=0;i<size;i++)
//                {
//                    Future<Object> data = executors.get(i).splitByFuture();
//                    futures[i]=data;
//                }
//                try{
//                    Object[] datas=new Object[size];
//                    for(int i=0;i<size;i++)
//                    {
//                        Object o = futures[i].get();
//                        datas[i]=o;
//                    }
//                    //创建对象
//                    ShareMemory shareMemory1 = shareMemory.newMemory();
//
//                }catch (Exception e)
//                {
//                    MutConfiguration.log.error("Failed to auto volume", e);
//                }finally
//                {
//                    isJudge = false;
//                }
//
//            }
//        });
//    }


    int idRecord = -1;

    public int getRecord()
    {
        idRecord++;
        return idRecord;
    }
}
