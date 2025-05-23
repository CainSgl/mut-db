package cainsgl.core.data.ttl;

import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.system.GcSystem;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;
import jdk.jfr.Event;

import java.io.*;
import java.util.function.Consumer;

@Deprecated
public class TTLObj
{
    //去序列化的时候只记录时间，wrapper通通记录为byteValue
    public static class TTLObjConverter extends Converter<TTLObj>
    {
        @Override
        public byte[] toBytes(TTLObj obj)
        {
            try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                 DataOutputStream dataOut = new DataOutputStream(byteOut))
            {
                // 1. 序列化过期时间
                dataOut.writeLong(obj.expireTime);
                // 2. 处理wrapper（统一转换为ByteValue）
                //              Object wrapper = obj.wrapper;
                //  if (wrapper == null)
                //    {
                // 写入空标记（0长度表示null）
                //       dataOut.writeInt(0);
                //      return byteOut.toByteArray();
                //     }
                // 3. 将wrapper转换为byte[]（通过其类型的转换器）
                //         Converter wrapperConverter = ConverterRegister.getConverter(wrapper.getClass());
                //      byte[] wrapperBytes = wrapperConverter.toBytes(wrapper);
                ///      dataOut.writeInt(wrapperBytes.length);
                //       dataOut.write(wrapperBytes);
                return byteOut.toByteArray();
            } catch (IOException e)
            {
                throw new RuntimeException("TTLObj serialization failed", e);
            }
        }

        @Override
        public TTLObj fromBytes(byte[] bytes)
        {
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
                 DataInputStream dataIn = new DataInputStream(byteIn))
            {
                // 1. 读取过期时间
                long expireTime = dataIn.readLong();
                // 2. 读取ByteValue字节数据
                //     int byteValueLen = dataIn.readInt();
                //    if (byteValueLen == 0)
                //   {
                // 空值处理
                //       return new TTLObj<>(null);
                //    }
                //     byte[] byteValueBytes = new byte[byteValueLen];
                //    dataIn.readFully(byteValueBytes);
                //     ByteValue byteValue = new ByteValue(byteValueBytes);
                return new TTLObj(expireTime); // 需根据实际构造函数参数调整
            } catch (IOException e)
            {
                throw new RuntimeException("TTLObj deserialization failed", e);
            }
        }
    }

    private long expireTime;
    private Consumer<TTLObj> consumer;
    private CommandManager manager;
    //  private final T wrapper;
    boolean alawayNoDeCall;

//    public T getWrapper()
//    {
//        if (expireTime < 0)
//        {
//            //永不过期的
//            return wrapper;
//        }
//        if (consumer == null)
//        {
//            //被执行了
//            return null;
//        }
//        if (expireTime < GcSystem.updateTime)
//        {
//            consumer.accept(this);
//            return null;
//        }
//        return wrapper;
//    }

    public void setManager(CommandManager manager)
    {
        this.manager = manager;
        alawayNoDeCall = true;
    }

    private TTLObj(long expireTime)
    {
        this.expireTime = expireTime;
  //      this.wrapper = wrapper;
    }

    public TTLObj(long expireTime, CommandManager manager, Consumer<TTLObj> delCall)
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
        this.manager = manager;
        this.consumer = delCall;
        this.expireTime = expireTime + GcSystem.updateTime;
    //    GcSystem.register(this);
    }

//    public TTLObj(T wrapper)
//    {
//        this.expireTime = -1;
//      //  this.wrapper = wrapper;
//    }

    public void setExpireTime(long expireTime)
    {
        if (expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        long originTime = this.expireTime;
        this.expireTime = expireTime + GcSystem.updateTime;
    //    GcSystem.fixRegister(this, originTime);
    }

    public void increTime(long increTime)
    {
        if (expireTime < 0)
        {
            throw new UnsupportedOperationException("这是一个未注册在内的ttlObj，不能修改");
        }
        long originTime = this.expireTime;
        this.expireTime = expireTime + increTime;
     //   GcSystem.fixRegister(this, originTime);
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
        consumer = null;
    }

    public void activate(CommandManager manager, Consumer<TTLObj> delCall)
    {
        this.manager = manager;
        this.consumer = delCall;
      //  GcSystem.register(this);
    }
}
