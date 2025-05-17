package cainsgl.core.command.manager.shunt;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;

public class CommandShunt
{
    private final List<CommandShuntComponent>[] processors;
    private final EventLoopGroup WORK_GROUP;
    private final EventLoop volumeThread;

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
                consumer.accept(proxy.execute(args, manager.proxy));
            });
        }


        @Override
        public RESP2Response execute(byte[][] args, ShuntCommandManager<?> manager)
        {
            throw new UnsupportedOperationException("不支持直接调用分流器的执行方法");
        }
    }

    private static class ShuntManagerProxy<D> implements ShuntManager<D>
    {
        public final ShuntCommandManager<D> proxy;
        final EventLoop eventLoop;

        public ShuntManagerProxy(ShuntCommandManager<D> shuntCommandManager, EventLoop eventLoop)
        {
            this.proxy = shuntCommandManager;
            this.eventLoop = eventLoop;
        }

        @SafeVarargs
        @Override
        public final void create(D... data)
        {
            eventLoop.submit(() -> {
                proxy.createImpl(data);
            });
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
                ThreadManager.backEventLoop(eventLoop);
            });
            return promise;
        }
    }

    public CommandShunt(ShuntCommandManager shuntCommandManager, CommandProcessor... proxy)
    {
        WORK_GROUP = ThreadManager.getEventLoopGroup(MutConfiguration.shuntThreads);
        volumeThread = WORK_GROUP.next();
        processors = new List[proxy.length];
        EventLoop eventLoop = ThreadManager.getEventLoop();
        for (int i = 0; i < proxy.length; i++)
        {
            processors[i] = new ArrayList<>();
            processors[i].add(new CommandShuntComponent(new ShuntManagerProxy(shuntCommandManager, eventLoop), proxy[i], eventLoop));
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
            for (int i = 0; i < size; i++)
            {
                Future<Integer> future = executors.get(i).manager.overLoad();
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
                        executors.get(i).manager.setTester(key -> new ByteFastKey(key).hashCode() % size == finalI);
                    }
                    Future[] separates = new Future[size];
                    for (int i = 0; i < size; i++)
                    {
                        separates[i] = executors.get(i).manager.separate();
                    }
                    Object[] datas = new Object[size];
                    for (int i = 0; i < size; i++)
                    {
                        datas[i] = separates[i].get();
                    }
                    ShuntManagerProxy manager = executors.getFirst().manager;
                    manager.create(datas);
                } else if (AlloverLoad / size < MutConfiguration.MIN_OVER_LOAD)
                {
                    //TODO 需要减

                }
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
            for (int i = 0; i < processors.length; i++)
            {
                if (proxys[i].minCount() < 1)
                {
                    throw new UnsupportedOperationException("不支持没有key的CommandProcessor使用分流器");
                }
                processors[i].add(new CommandShuntComponent(new ShuntManagerProxy(shuntCommandManager, eventLoop), proxys[i], eventLoop));
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
