package cainsgl.core.system.thread;

import cainsgl.core.config.MutConfiguration;
import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;


public class ThreadManager
{
    public static final EventLoopGroup SERVER_BOSS_GROUP;
    public static final EventLoopGroup SERVER_WORKER_GROUP;
    public static final EventLoop[] GC_WORKER_GROUP;

    public static final int gcThreads;
    private static int fakeThreads;
    private static final ThreadController threadController;

    static
    {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int workThreads, gcThreads2;
        gcThreads2 = workThreads = cpuCores / 4;
        if (workThreads < 2)
        {
            workThreads = 2;
        }
        if (gcThreads2 < 1)
        {
            gcThreads2 = 1;
        }
        if (MutConfiguration.gcThreads != null)
        {
            gcThreads2 = MutConfiguration.gcThreads;
        }
        if (MutConfiguration.workThreads != null)
        {
            workThreads = MutConfiguration.workThreads;
        }
        SERVER_BOSS_GROUP = new NioEventLoopGroup(1, new DefaultThreadFactory("BossGroup", Thread.NORM_PRIORITY));
        SERVER_WORKER_GROUP = new NioEventLoopGroup(workThreads, new DefaultThreadFactory("HandlerGroup", Thread.NORM_PRIORITY));
        GC_WORKER_GROUP = new EventLoop[gcThreads2];
        for (int i = 0; i < gcThreads2; i++)
        {
            GC_WORKER_GROUP[i] = new DefaultEventLoop(new DefaultThreadFactory("GCWorkerGroup:" + i, Thread.NORM_PRIORITY));
        }
        gcThreads = gcThreads2;
        if (MutConfiguration.autoScalingThread)
        {
            fakeThreads = cpuCores - workThreads - gcThreads2 - 1;
        } else
        {
            fakeThreads = -1;
        }
        if (fakeThreads < 2)
        {
            threadController = new CompactThreadController(workThreads);
        } else
        {
            threadController = new DefaultThreadController(fakeThreads);
        }

    }


    public static EventLoop getEventLoop(int id)
    {
        return threadController.getEventLoop(id);
    }

    public static void backEventLoop(int id)
    {
        threadController.backEventLoop(id);
    }

    public static void stop() throws Exception
    {
        SERVER_BOSS_GROUP.shutdownGracefully();
        SERVER_WORKER_GROUP.shutdownGracefully();
        for (EventLoop eventExecutors : GC_WORKER_GROUP)
        {
            eventExecutors.shutdownGracefully();
        }
    }

    public static boolean hasFakeThread()
    {
        return fakeThreads > 0;
    }
}
