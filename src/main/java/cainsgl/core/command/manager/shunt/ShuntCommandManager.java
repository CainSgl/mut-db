package cainsgl.core.command.manager.shunt;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.manager.CommandManager;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.persistence.MutSerializer;
import io.netty.util.concurrent.Future;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;

public abstract class ShuntCommandManager<D> implements CommandManager, ShuntManager<D>, MutSerializer
{
    private static class ShuntCommandProcessor<M extends CommandManager> extends CommandProcessor<M> implements ShuntCaller
    {

        final CommandProcessor<?> proxy;
        final CommandShunt commandShunt;
        final int record;

        public ShuntCommandProcessor(CommandProcessor processor, CommandShunt commandShunt)
        {
            super(processor.minCount(), processor.maxCount(), processor.commandName(), processor.parameters());
            this.proxy = processor;
            this.commandShunt = commandShunt;
            record = commandShunt.getRecord();
            NetWorkConfig.register(processor.commandName(), this);
        }

        @Override
        public RESP2Response execute(byte[][] args, M manager)
        {
            //   return null;
            throw new UnsupportedOperationException("不支持直接调用分流器的execute");
        }
        //最终被执行的
        @Override
        public void submit(byte[][] args, Consumer<RESP2Response> consumer, M manager)
        {
            commandShunt.shunt(this, args, consumer);
        }

        @Override
        public int getRecord()
        {
            return record;
        }
    }

    private static final Map<Class<?>, CommandShunt> SHUNT_MAP = new HashMap<>();
    @SafeVarargs
    public ShuntCommandManager(CommandProcessor... processors)
    {
        Class<?> myClass = this.getClass();
        CommandShunt commandShunt = SHUNT_MAP.get(myClass);
        if (commandShunt == null)
        {
            commandShunt = new CommandShunt(this, processors);
            //第一次进来
            for (CommandProcessor processor : processors)
            {
                new ShuntCommandProcessor<>(processor, commandShunt);
            }
        //    commandShunt.addProcessors(this, processors);
            SHUNT_MAP.put(myClass, commandShunt);
            CommandConfiguration.register(commandShunt,this);
        } else
        {
            //第二次进来，直接往ShuntCommand里添加数据
            MutConfiguration.log.info("a command overload,expanding it.");
            commandShunt.addProcessors(this, processors);
        }
    }

    private Tester tester;

    @Override
    public Future<D> separate()
    {
        throw new UnsupportedOperationException("不支持直接使用ShuntCommandManager");
    }

    public abstract D separateImpl();

    @SafeVarargs
    @Override
    public final void create(D... data)
    {
        throw new UnsupportedOperationException("不支持直接使用ShuntCommandManager");
    }

    public abstract void createImpl(List<D> datas);

    @Override
    public Future<Integer> overLoad()
    {
        throw new UnsupportedOperationException("不支持直接使用ShuntCommandManager");
    }



    public abstract Integer overLoadImpl();

    @Override
    public Future<D> destory()
    {
        throw new UnsupportedOperationException("不支持直接使用ShuntCommandManager");
    }

    public abstract void addData(D data);


    public abstract D destoryImpl();

    public void setTester(Tester tester)
    {
        this.tester = tester;
    }

    protected boolean testKey(byte[] key)
    {
        return tester.testKey(key);
    }
}
