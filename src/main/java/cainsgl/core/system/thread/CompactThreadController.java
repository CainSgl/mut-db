package cainsgl.core.system.thread;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;

public class CompactThreadController implements ThreadController
{
    int fakeThreadsNum;
    EventLoop eventLoop=new DefaultEventLoop();
    EventLoopGroup group=new DefaultEventLoopGroup(2);
    public CompactThreadController(int fakeThreadsNum)
    {
        this.fakeThreadsNum = fakeThreadsNum;
    }

    @Override
    public EventLoop getEventLoop()
    {
        return eventLoop;
    }

    @Override
    public void backEventLoop(EventLoop eventLoop)
    {

    }

    @Override
    public EventLoopGroup getEventLoopGroup(int threadsNum)
    {
        return group;
    }

    @Override
    public void backLoopGroup(EventLoopGroup eventLoopGroup)
    {

    }

    @Override
    public boolean hasMoreThreads()
    {
        return false;
    }


}
