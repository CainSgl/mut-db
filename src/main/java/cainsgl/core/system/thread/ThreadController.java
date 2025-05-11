package cainsgl.core.system.thread;

import io.netty.channel.EventLoop;

public interface ThreadController
{
    EventLoop getEventLoop(int id);
    void backEventLoop(int id);

}
