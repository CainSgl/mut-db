package cainsgl.core.command.processor;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.response.RESP2Response;

import java.util.List;
import java.util.function.Consumer;

public abstract class CommandProcessor<T>
{


    public CommandProcessor(int minCount, int maxCount, String commandName, List<String> parameters, boolean aof)
    {
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.commandName = commandName;
        this.parameters = parameters;
        this.aof=aof;
    }

    public CommandProcessor(int minCount, int maxCount, String commandName, List<String> parameters)
    {
        this(minCount, maxCount, commandName, parameters, false);
    }
    public final boolean aof;
    public boolean getAof()
    {
        return aof;
    }
    //public final AofExecutor aofExecutor;
    final int minCount;

    public int minCount()
    {
        return minCount;
    }

    final int maxCount;

    public int maxCount()
    {
        return maxCount;
    }

    //   final T manager;


    final List<String> parameters;

    public List<String> parameters()
    {
        return parameters;
    }

    final String commandName;

    public String commandName()
    {
        return commandName;
    }

    private T manager()
    {
        throw new NullPointerException("没有manager");
    }

    public abstract RESP2Response execute(byte[][] args, T manager);

    public void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager)
    {
        throw new UnsupportedOperationException("无法提交任务，因为没有单独的线程");
    }

    public  void aof(byte[][] args){
        throw new UnsupportedOperationException("无法执行aof，没有aofBuffer");
    }
}
