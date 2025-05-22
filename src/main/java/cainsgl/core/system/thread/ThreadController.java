package cainsgl.core.system.thread;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;

public interface ThreadController
{

    EventLoop getEventLoop();

    void backEventLoop(EventLoop eventLoop);


    EventLoopGroup getEventLoopGroup(int threadsNum);

    void backLoopGroup(EventLoopGroup eventLoopGroup);

    boolean hasMoreThreads();
}
