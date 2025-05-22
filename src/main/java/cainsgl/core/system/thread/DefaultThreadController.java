package cainsgl.core.system.thread;

import cainsgl.core.config.MutConfiguration;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class DefaultThreadController implements ThreadController
{
    private static int fakeThreadsNum;
    private final static List<ThreadControllerEventLoop> THREAD_ARRAY = new ArrayList<>();
    private final static List<ThreadControllerEventLoopGroup> THREAD_GROUPS = new ArrayList<>();
    //  private static Integer record = 0;
    private Integer singleIndex = 0;
    private Integer multiIndex = 0;

    public DefaultThreadController(int fakeThreadsNum)
    {
        DefaultThreadController.fakeThreadsNum = fakeThreadsNum;
    }

    private static class ThreadControllerEventLoop extends DefaultEventLoop
    {
        Integer id;
        AtomicInteger used = new AtomicInteger(1);

        public ThreadControllerEventLoop()
        {
            super(new DefaultThreadFactory("ThreadControllerEventLoop"));
            this.id = THREAD_ARRAY.size();
            fakeThreadsNum--;
        }
    }

    private static class ThreadControllerEventLoopGroup extends DefaultEventLoopGroup
    {
        Integer id;
        AtomicInteger used = new AtomicInteger(1);
        final int num;

        public ThreadControllerEventLoopGroup(int num)
        {
            super(num);
            this.num = num;
            this.id = DefaultThreadController.THREAD_ARRAY.size();
            fakeThreadsNum -= num;
        }
    }

    @Override
    public synchronized EventLoop getEventLoop()
    {

        if (fakeThreadsNum < 0)
        {
            singleIndex %= THREAD_ARRAY.size();
            ThreadControllerEventLoop eventExecutors = THREAD_ARRAY.get(singleIndex);
            eventExecutors.used.incrementAndGet();
            singleIndex++;
            MutConfiguration.log.info("无多余线程，给予重复的线程 {}",eventExecutors);
            return eventExecutors;
        }
        ThreadControllerEventLoop eventExecutors = new ThreadControllerEventLoop();
        MutConfiguration.log.info("申请的线程id为 {}",eventExecutors);
        THREAD_ARRAY.add(eventExecutors);
        fakeThreadsNum--;
        return eventExecutors;
    }

    @Override
    public synchronized void backEventLoop(EventLoop eventLoop)
    {
        ThreadControllerEventLoop eventLoop1 = (ThreadControllerEventLoop) eventLoop;
        MutConfiguration.log.info("归还线程id为{}的线程", eventLoop1);
        if (eventLoop1.used.decrementAndGet() == 0)
        {
            //销毁
            MutConfiguration.log.info("销毁id为{}的线程", eventLoop1);
            THREAD_ARRAY.remove(eventLoop1);
            fakeThreadsNum++;
            eventLoop1.shutdownGracefully();
        }
    }

    @Override
    public synchronized EventLoopGroup getEventLoopGroup(int threadsNum)
    {
        if (fakeThreadsNum < 0)
        {
            multiIndex %= THREAD_ARRAY.size();
            EventLoopGroup eventExecutors = THREAD_GROUPS.get(multiIndex);
            multiIndex++;
            return eventExecutors;
        }
        ThreadControllerEventLoopGroup eventExecutors = new ThreadControllerEventLoopGroup(threadsNum);
        THREAD_GROUPS.add(eventExecutors);
        fakeThreadsNum -= threadsNum;
        return eventExecutors;
    }

    @Override
    public synchronized void backLoopGroup(EventLoopGroup eventLoopGroup)
    {
        ThreadControllerEventLoopGroup group = (ThreadControllerEventLoopGroup) eventLoopGroup;
        if (group.used.decrementAndGet() == 0)
        {
            //销毁
            THREAD_GROUPS.remove(group);
            fakeThreadsNum += group.num;
            group.shutdownGracefully();
        }
    }


//    public  final EventLoop[] eventWorkerGroup;
//    public  final int[] eventWorkerCount;
//    private int fakeThreadCount;

//    @Override
//    public  EventLoop getEventLoop(int id)
//    {
//        int index = id % eventWorkerGroup.length;
//        if (eventWorkerGroup[index] != null)
//        {
//
//            eventWorkerCount[index]++;
//            return eventWorkerGroup[index];
//        } else
//        {
//            eventWorkerCount[index]=1;
//            fakeThreadCount--;
//            return eventWorkerGroup[index] = new DefaultEventLoop(new DefaultThreadFactory("WorkerGroup:" + id, Thread.NORM_PRIORITY));
//        }
//    }
//
//    @Override
//    public void backEventLoop(int id)
//    {
//        int index = id % eventWorkerGroup.length;
//        eventWorkerCount[index]--;
//        if(eventWorkerCount[index]==0)
//        {
//            fakeThreadCount++;
//            eventWorkerGroup[index].close();
//            eventWorkerGroup[index] = null;
//        }
//    }
//
//    @Override
//    public EventLoopGroup getEventLoopGroup(int id, int threadsNum)
//    {
//
//    }
//
//    @Override
//    public EventLoopGroup backLoopGroup(int id)
//    {
//        return null;
//    }
}
