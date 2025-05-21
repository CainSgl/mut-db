package cainsgl.core.system;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Promise;

import java.time.chrono.Chronology;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GcSystem
{
    public static final long SERVER_START_TIME = System.currentTimeMillis();
    public volatile static long updateTime;


    private static final Chronology[] chronologies;
    private static final EventLoop MAIN_THEAD;

    static
    {
        if (ThreadManager.gcThreads > 1)
        {
            chronologies = new Chronology[ThreadManager.gcThreads - 1];
            for (int i = 0; i < chronologies.length; i++)
            {
                chronologies[i] = new Chronology(ThreadManager.GC_WORKER_GROUP[i + 1]);
            }
            MAIN_THEAD = ThreadManager.getEventLoop();
            updateTime = System.currentTimeMillis();
            System.out.println("GC类加载器" + MutConfiguration.GC.class.getClassLoader());
            MAIN_THEAD.scheduleAtFixedRate(GcSystem::mainExecute, 0, MutConfiguration.GC.UNIT_UPDATE_TIME, TimeUnit.MILLISECONDS);
            for (Chronology chronology : chronologies)
            {
                chronology.start();
            }
        } else
        {
            throw new IllegalArgumentException("GcSystem requires at least two Thread");
        }

    }

    private static void mainExecute()
    {
        updateTime = System.currentTimeMillis();
    }

    private static int lastChronology = 0;

    public static void register(TTLObj ttlObj)
    {
        MAIN_THEAD.submit(() -> {
            chronologies[lastChronology].register(ttlObj);
            ttlObj.setIndex(lastChronology);
            lastChronology = (lastChronology + 1) % chronologies.length;
        });
    }

    //不建议使用
    public static void unRegister(TTLObj ttlObj)
    {
        MAIN_THEAD.submit(() -> {
            Chronology chronology = chronologies[ttlObj.getIndex()];
            if (chronology.unRegister(ttlObj))
            {
                chronologies[lastChronology].register(ttlObj);
                ttlObj.setIndex(lastChronology);
                lastChronology = (lastChronology + 1) % chronologies.length;
            }
        });
    }

    public static void fixRegister(TTLObj ttlObj,long originTime)
    {

        MAIN_THEAD.submit(() -> {
            Chronology chronology = chronologies[ttlObj.getIndex()];
            chronology.reRegister(ttlObj, originTime);
        });
    }


    //时间论，会根据gcThread创建对应的数量
    private static class Chronology
    {
        final EventLoop eventLoop;


        private final TreeSet<TTLObj>[] solts;
        private final int mask;

        public Chronology(EventLoop eventLoop)
        {
            this.eventLoop = eventLoop;
            this.mask = MutConfiguration.GC.DEFAULT_SLOTS - 1;
            if (MutConfiguration.GC.DEFAULT_SLOTS > 0 && (MutConfiguration.GC.DEFAULT_SLOTS & (MutConfiguration.GC.DEFAULT_SLOTS - 1)) != 0)
            {
                throw new IllegalArgumentException("MutConfiguration.GC.DEFAULT_SLOTS  capacity must be 2^n");
            }
            solts = new TreeSet[MutConfiguration.GC.DEFAULT_SLOTS];

            Comparator<TTLObj> comparator = Comparator.comparingLong(TTLObj::getExpire);
            for (int i = 0; i < solts.length; i++)
            {
                solts[i] = new TreeSet<>(comparator);
            }

        }

        int lastSlot = 0;

        public void start()
        {
            eventLoop.scheduleAtFixedRate(() -> {
                TreeSet<TTLObj> solt = solts[lastSlot];
                lastSlot = (lastSlot + 1) & mask;
                while (true)
                {
                    if (solt.isEmpty())
                    {
                        return;
                    }
                    TTLObj first = solt.getFirst();
                    if (first.getExpire() < updateTime)
                    {
                        //删除
                        solt.removeFirst();
                        MutConfiguration.log.info("remove ttlObj");
                        first.del();
                    } else
                    {
                        break;
                    }
                }
            }, 0, MutConfiguration.GC.TICK_DURATION, TimeUnit.MILLISECONDS);
        }

        public void reRegister(TTLObj ttlObj,long originTime)
        {
            eventLoop.submit(() -> {
                ///删除原来的
                int index = ((int) (originTime - SERVER_START_TIME) / MutConfiguration.GC.TICK_DURATION) & mask;
                solts[index].remove(ttlObj);
                ///根据ttlObj里的过期时间，来分配插槽
                int index2 = ((int) (ttlObj.getExpire() - SERVER_START_TIME) / MutConfiguration.GC.TICK_DURATION) & mask;
                //分配到第index个插槽
                solts[index2].add(ttlObj);
            });
        }

        public boolean unRegister(TTLObj ttlObj)
        {
            try
            {
                Promise<Boolean> await = eventLoop.newPromise();
                eventLoop.submit(() -> {
                    ///根据ttlObj里的过期时间，来分配插槽
                    int index = ((int) (ttlObj.getExpire() - SERVER_START_TIME) / MutConfiguration.GC.TICK_DURATION) & mask;
                    await.setSuccess(solts[index].remove(ttlObj));
                });
                return await.get();
            } catch (Exception e)
            {
                MutConfiguration.log.error("unRegister failed", e);
                return false;
            }

        }

        public void register(TTLObj ttlObj)
        {
            eventLoop.submit(() -> {
                ///根据ttlObj里的过期时间，来分配插槽
                int index = ((int) (ttlObj.getExpire() - SERVER_START_TIME) / MutConfiguration.GC.TICK_DURATION) & mask;
                //分配到第index个插槽
                solts[index].add(ttlObj);
            });
        }
    }
}
