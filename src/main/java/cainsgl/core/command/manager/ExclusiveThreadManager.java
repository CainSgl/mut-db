package cainsgl.core.command.manager;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.command.processor.nonblock.NonBlockCommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.persistence.MutSerializer;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;

public  class ExclusiveThreadManager extends CommandManagerProxy
{
    public ExclusiveThreadManager(CommandProcessor<?>... processors)
    {
        NonBlockCommandProcessor<CommandManager>[] proxyArray = new NonBlockCommandProcessor[processors.length];
        EventLoop myEventLoop = ThreadManager.getEventLoop();
        for (int i = 0; i < proxyArray.length; i++)
        {
            proxyArray[i] = new ExclusiveThreadCommandProcessor(processors[i],myEventLoop);
        }
        super(proxyArray);
        ThreadManager.register(this,myEventLoop);
        if(this instanceof MutSerializer mutSerializer)
        {
            CommandConfiguration.register(mutSerializer,myEventLoop);
        }
    }

    private static class ExclusiveThreadCommandProcessor<T extends CommandManager> extends NonBlockCommandProcessor<T>
    {
        private final CommandProcessor<T> proxy;

        public ExclusiveThreadCommandProcessor(CommandProcessor<T> commandProcessor,EventLoop eventLoop)
        {
            super(eventLoop, commandProcessor.minCount(), commandProcessor.maxCount(), commandProcessor.commandName(), commandProcessor.parameters());
            proxy = commandProcessor;
        }

        @Override
        public RESP2Response execute(byte[][] args, T manager)
        {
            return proxy.execute(args, manager);
        }
    }

}
