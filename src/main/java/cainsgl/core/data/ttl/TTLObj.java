package cainsgl.core.data.ttl;

import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.system.GcSystem;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;
import jdk.jfr.Event;

import java.util.function.Consumer;

public class TTLObj<T>
{
    private  long expireTime;
    private Consumer<TTLObj<T>> consumer;
    private CommandManager manager;
    private final T wrapper;
    boolean alawayNoDeCall;
    public T getWrapper()
    {
        if(expireTime<0)
        {
            //永不过期的
            return wrapper;
        }
        if(consumer==null)
        {
            //被执行了
            return null;
        }
        if(expireTime<GcSystem.updateTime)
        {
            consumer.accept(this);
            return null;
        }
        return wrapper;
    }
    public void setManager(CommandManager manager)
    {
        this.manager = manager;
        alawayNoDeCall=true;
    }
    public TTLObj(long expireTime, T wrapper, CommandManager manager, Consumer<TTLObj<T>> delCall)
    {
        this.wrapper = wrapper;
        if (expireTime < 0)
        {
            //永不过期
            this.expireTime = -1;
            return;
        }
        if (manager == null)
        {
            throw new NullPointerException("manager is null");
        }
        this.manager = manager;
        this.consumer = delCall;
        this.expireTime = expireTime + GcSystem.updateTime;
        GcSystem.register(this);
    }
    public TTLObj(T wrapper){
        this.expireTime = -1;
        this.wrapper = wrapper;
    }
    public void setExpireTime(long expireTime)
    {
        if (expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        long originTime=this.expireTime;
        this.expireTime = expireTime+GcSystem.updateTime;
        GcSystem.fixRegister(this,originTime);
    }
    public void increTime(long increTime)
    {
        if (expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        long originTime=this.expireTime;
        this.expireTime = expireTime+increTime;
        GcSystem.fixRegister(this,originTime);
    }
    int index=-1;
    public void setIndex(int index)
    {
        if(index<0)
        {
            this.index = index;
            return;
        }
        if(this.index>0)
        {
            throw new UnsupportedOperationException("修改了不允许修改的值");
        }
        this.index = index;
    }
    public int getIndex()
    {
        return index;
    }
    public long getExpire()
    {
        return expireTime;
    }
    public long getExpireTime()
    {
        long time=expireTime-GcSystem.updateTime;
        if(time<0)
        {
            return -1;
        }
        return time;
    }
    public void del()
    {
        if(consumer == null)
        {
            return;
        }
        EventLoop execute = ThreadManager.getLoopByManager(manager);
        if (execute == null)
        {
            //manager没自己的线程，有可能是分流器的线程被销毁
            MutConfiguration.log.warn("manager no self thread,but it new a TTLObj");
            consumer.accept(this);
            return;
        }
        execute.submit(() -> {
            consumer.accept(this);
        });
    }
    public void clear()
    {
        consumer.accept(this);
        consumer=null;
    }
}
