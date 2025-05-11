package cainsgl.core.system.thread;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultThreadFactory;



public class DefaultThreadController implements ThreadController
{
    public  final EventLoop[] eventWorkerGroup;
    public  final int[] eventWorkerCount;
    public DefaultThreadController(int eventWorkerGroupNum)
    {
        eventWorkerGroup=new EventLoop[eventWorkerGroupNum];
        eventWorkerCount=new int[eventWorkerGroupNum];
    }
    @Override
    public EventLoop getEventLoop(int id)
    {
        int index = id % eventWorkerGroup.length;
        if (eventWorkerGroup[index] != null)
        {
            eventWorkerCount[index]++;
            return eventWorkerGroup[index];
        } else
        {
            eventWorkerCount[index]=1;
            return eventWorkerGroup[index] = new DefaultEventLoop(new DefaultThreadFactory("WorkerGroup:" + id, Thread.NORM_PRIORITY));
        }
    }

    @Override
    public void backEventLoop(int id)
    {
        int index = id % eventWorkerGroup.length;
        eventWorkerCount[index]--;
        if(eventWorkerCount[index]==0)
        {
            eventWorkerGroup[index].close();
            eventWorkerGroup[index] = null;
        }
    }
}
