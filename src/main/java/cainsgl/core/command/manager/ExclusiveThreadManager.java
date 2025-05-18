package cainsgl.core.command.manager;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.command.processor.nonblock.NonBlockCommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;

public class ExclusiveThreadManager extends CommandManagerProxy {

    private static class ExclusiveThreadCommandProcessor<T> extends NonBlockCommandProcessor<T> {
        private final CommandProcessor<T> proxy;

        public ExclusiveThreadCommandProcessor(CommandProcessor<T> commandProcessor) {
            super(ThreadManager.getEventLoop(), commandProcessor.minCount(), commandProcessor.maxCount(), commandProcessor.commandName(), commandProcessor.parameters());
            proxy = commandProcessor;
        }

        @Override
        public RESP2Response execute(byte[][] args, T manager)
        {
            return proxy.execute(args, manager);
        }

    }

    public ExclusiveThreadManager(CommandProcessor<CommandManager>... processors) {
        NonBlockCommandProcessor<CommandManager>[] proxyArray = new NonBlockCommandProcessor[processors.length];
        for (int i = 0; i < proxyArray.length; i++) {
            proxyArray[i] = new ExclusiveThreadCommandProcessor<>(processors[i]);
        }
        super(proxyArray);
    }
}
