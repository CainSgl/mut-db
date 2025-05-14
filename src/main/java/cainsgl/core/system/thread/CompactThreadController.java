package cainsgl.core.system.thread;

import io.netty.channel.EventLoop;

import static cainsgl.core.system.thread.ThreadManager.SERVER_WORKER_GROUP;

public class CompactThreadController implements ThreadController
{
    public  final EventLoop[] eventWorkerGroup;
    public CompactThreadController(int workThreads)
    {
        eventWorkerGroup=new EventLoop[workThreads];
        for(int i = 0;i < workThreads;i++)
        {
            eventWorkerGroup[i] = SERVER_WORKER_GROUP.next();
        }

    }
    @Override
    public EventLoop getEventLoop(int id)
    {
      return eventWorkerGroup[id%eventWorkerGroup.length];
    }

    @Override
    public void backEventLoop(int id)
    {
        return;
    }
}
