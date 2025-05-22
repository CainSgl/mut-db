package cainsgl.core.data.ttl;

import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.system.GcSystem;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;

import java.io.*;
import java.util.function.Consumer;

public class TTL2Obj
{

    public static class TTLObjConverter extends Converter<TTL2Obj>
    {
        private static final Converter<ByteValue> INSTANCE = ConverterRegister.getConverter(ByteValue.class);

        @Override
        public byte[] toBytes(TTL2Obj obj)
        {
            try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                 DataOutputStream dataOut = new DataOutputStream(byteOut))
            {
                dataOut.writeLong(obj.expireTime-GcSystem.updateTime);
                byte[] bytes = obj.byteValue.getBytes();
                dataOut.writeInt(bytes.length);
                dataOut.write(bytes);
                return byteOut.toByteArray();
            } catch (IOException e)
            {
                throw new RuntimeException("TTLObj serialization failed", e);
            }
        }

        @Override
        public TTL2Obj fromBytes(byte[] bytes)
        {
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
                 DataInputStream dataIn = new DataInputStream(byteIn))
            {
                // 1. 读取过期时间
                long expireTime = dataIn.readLong();
                int i = dataIn.readInt();
                byte[] bytes1 = new byte[i];
                dataIn.readFully(bytes1);
                return new TTL2Obj(expireTime, new ByteValue(bytes1));
            } catch (IOException e)
            {
                throw new RuntimeException("TTLObj deserialization failed", e);
            }
        }
    }


    ByteValue byteValue;
    private CommandManager manager;
    // boolean alawayNoDeCall;

    public ByteValue getWrapper()
    {
        if (expireTime < 0)
        {
            return byteValue;
        }
        if (byteValue == null)
        {
            return null;
        }
        if (expireTime < GcSystem.updateTime)
        {
            //过期了
            consumer.accept(this);
            this.byteValue = null;
            return null;
        }
        return byteValue;
    }

    public void setManager(CommandManager manager, Consumer<TTL2Obj> consumer)
    {
        this.manager = manager;
        this.consumer = consumer;
    }

    long expireTime;

    private TTL2Obj(long expireTime, ByteValue byteValue)
    {
        this.expireTime = expireTime;
        this.byteValue = byteValue;
    }

    private Consumer<TTL2Obj> consumer;

    public TTL2Obj(long expireTime, ByteValue byteValue, CommandManager manager, Consumer<TTL2Obj> delCall)
    {
        //    this.wrapper = wrapper;
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
        this.byteValue = byteValue;
        this.manager = manager;
        this.consumer = delCall;
        this.expireTime = expireTime + GcSystem.updateTime;
        GcSystem.register(this);
    }

    public TTL2Obj(ByteValue byteValue)
    {
        this.expireTime = -1;
        this.byteValue = byteValue;
    }

    public void setExpireTime(long e)
    {
        if (this.expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        if (e < 0)
        {
            //改为永不过期对象
            consumer=null;
            return;
        }
        long originTime = this.expireTime;
        this.expireTime = e + GcSystem.updateTime;
        GcSystem.fixRegister(this, originTime);
    }

    public void increTime(long increTime)
    {
        if (expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        long originTime = this.expireTime;
        this.expireTime = expireTime + increTime;
        GcSystem.fixRegister(this, originTime);
    }

    int index = -1;

    public void setIndex(int index)
    {
        if (index < 0)
        {
            this.index = index;
            return;
        }
        if (this.index > 0)
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
        long time = expireTime - GcSystem.updateTime;
        if (time < 0)
        {
            return -1;
        }
        return time;
    }


    public void del()
    {
        if (consumer == null)
        {
            return;
        }
        EventLoop execute = ThreadManager.getLoopByManager(manager);
        if (execute == null)
        {
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
        if (consumer == null)
        {
            //说明已经被清理了
            return;
        }
        consumer.accept(this);
        consumer = null;
    }

    public void activate(CommandManager manager, Consumer<TTL2Obj> delCall)
    {
        if (this.expireTime < 0)
        {
            return;
        }
        this.expireTime=this.expireTime+ GcSystem.updateTime;
        this.manager = manager;
        this.consumer = delCall;
        GcSystem.register(this);
    }
    long stopTime;
    public void await()
    {
        if(this.expireTime<0)
        {
            return;
        }

        stopTime=GcSystem.updateTime;
        //调用他，直接变成永久的，后面再恢复
        consumer=null;
    }
    public void recover(CommandManager manager,Consumer<TTL2Obj> delCall)
    {
        if(this.expireTime<0)
        {
            return;
        }
        //增加其中等待的时间
        this.manager = manager;
        this.consumer = delCall;
        long originTime=this.expireTime;
        this.expireTime=originTime+GcSystem.updateTime-stopTime;
        GcSystem.fixRegister(this,originTime);
    }
}
