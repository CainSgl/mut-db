package cainsgl.core.command.manager;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.command.processor.nonblock.NonBlockCommandProcessor;
import cainsgl.core.network.response.RESP2Response;

import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultThreadFactory;

public  class ShareThreadManager extends CommandManagerProxy
{
    public ShareThreadManager(CommandProcessor<?>... processors)
    {
        NonBlockCommandProcessor<CommandManager>[] proxyArray = new NonBlockCommandProcessor[processors.length];
        for (int i = 0; i < proxyArray.length; i++)
        {
            proxyArray[i] = new ShareThreadCommandProcessor(processors[i]);
        }
        super(proxyArray);
        ThreadManager.register(this,SHARE_LOOP);
        if(this instanceof MutSerializable mutSerializer)
        {
            CommandConfiguration.register(mutSerializer,SHARE_LOOP);
        }
    }

    private static class ShareThreadCommandProcessor<T extends CommandManager> extends NonBlockCommandProcessor<T >
    {
        private final CommandProcessor<T> proxy;

        public ShareThreadCommandProcessor(CommandProcessor<T> commandProcessor)
        {
            super(SHARE_LOOP, commandProcessor.minCount(), commandProcessor.maxCount(), commandProcessor.commandName(), commandProcessor.parameters());
            proxy = commandProcessor;
        }

        @Override
        public RESP2Response execute(byte[][] args, T manager)
        {
            return proxy.execute(args, manager);
        }
    }

    public static final EventLoop SHARE_LOOP = new DefaultEventLoop(new DefaultThreadFactory("share-loop"));

}
