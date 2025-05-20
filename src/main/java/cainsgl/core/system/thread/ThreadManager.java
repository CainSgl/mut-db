package cainsgl.core.system.thread;

import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.config.MutConfiguration;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.HashMap;
import java.util.Map;


public class ThreadManager
{
    public static final EventLoopGroup SERVER_BOSS_GROUP;
    public static final EventLoopGroup SERVER_WORKER_GROUP;
    public static final EventLoop[] GC_WORKER_GROUP;

    public static final int gcThreads;
    private final static int fakeThreads;
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
        GC_WORKER_GROUP=new EventLoop[gcThreads2];
        for(int i=0;i<gcThreads2;i++)
        {
          GC_WORKER_GROUP[i]=new DefaultEventLoop(new DefaultThreadFactory("gcWorker"+i, Thread.NORM_PRIORITY));
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


    public static EventLoop getEventLoop()
    {
        return threadController.getEventLoop();
    }

    public static void backEventLoop(EventLoop eventLoop)
    {
        threadController.backEventLoop(eventLoop);
    }

    public static EventLoopGroup getEventLoopGroup(int threadsNum)
    {
        return threadController.getEventLoopGroup(threadsNum);
    }

    public static void backLoopGroup(EventLoopGroup eventLoopGroup)
    {
        threadController.backLoopGroup(eventLoopGroup);
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

    private static final Map<CommandManager,EventLoop> MANAGER_EVENT_LOOP_MAP = new HashMap<CommandManager,EventLoop>();
    public static void register(CommandManager manager,EventLoop workLoop)
    {
        MANAGER_EVENT_LOOP_MAP.put(manager,workLoop);
    }
    public static void unRegister(CommandManager manager)
    {
        EventLoop remove = MANAGER_EVENT_LOOP_MAP.remove(manager);
        backEventLoop(remove);
    }
    public static EventLoop getLoopByManager(CommandManager manager)
    {
        return MANAGER_EVENT_LOOP_MAP.get(manager);
    }

    public static boolean hasFakeThread()
    {
        return fakeThreads > 0;
    }
}
