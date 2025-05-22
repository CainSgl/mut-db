package cainsgl.core.command.processor.nonblock;

import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;

import java.util.List;
import java.util.function.Consumer;

public abstract class NonBlockCommandProcessor<T extends CommandManager> extends CommandProcessor<T>
{
    final EventLoop eventLoop;

    public NonBlockCommandProcessor(EventLoop eventLoop, int minCount, int maxCount, String commandName, List<String> parameters)
    {
        super(minCount, maxCount, commandName, parameters);
        this.eventLoop = eventLoop;
    }

    @Override
    public void submit(byte[][] args, Consumer<RESP2Response> consumer,T manager)
    {

        eventLoop.submit(() -> {
            try{
                consumer.accept(this.execute(args,manager));
            }catch(Exception t){
                manager.exceptionCaught(t);
            }
        });
    }
}
