package cainsgl.core.command.processor;

import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;

import java.util.function.Consumer;

public class CommandProcessorProxy<T> extends CommandProcessor<T>
{
    final T commandManager;
    final CommandProcessor<T> proxy;

    public CommandProcessorProxy(T commandManager, CommandProcessor<T> commandProcessor)
    {
        super(commandProcessor.minCount, commandProcessor.maxCount, commandProcessor.commandName, commandProcessor.parameters);
        this.commandManager = commandManager;
        this.proxy = commandProcessor;
        NetWorkConfig.register(commandProcessor.commandName, this);
    }


    public RESP2Response execute(byte[][] args, T manager)
    {
        return proxy.execute(args, commandManager);
    }

    @Override
    public void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager)
    {
        proxy.submit(args, consumer, commandManager);
    }
}
