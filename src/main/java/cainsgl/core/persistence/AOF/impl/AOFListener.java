package cainsgl.core.persistence.AOF.impl;


import cainsgl.core.config.MutConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
* AOF_同步刷盘
* */
public class AOFListener {
    private static final Logger log = LogManager.getLogger(AOFListener.class);

    public static final int SET_BUFFER_POSITION = 0;
    public static final int LIST_BUFFER_POSITION = 1;

    // 堆内存的分配大小
    private static final int bufferSize = MutConfiguration.AOF.BUFFER_SIZE;
    // 独立缓冲区的个数
    private static final int bufferCount = MutConfiguration.AOF.BUFFER_COUNT;
    // 堆内存缓冲区；用于缓冲命令信息
    private static final ByteBuffer[] buffers = new ByteBuffer[bufferCount];

    // AOF定时任务线程池
    private static final ScheduledExecutorService scheduler;
    // AOF间隔时间 单位/毫秒
    private static final long intervalTime = MutConfiguration.AOF.INTERVAL_TIME;

    // AOF文件名称；暂时写死
    private static final String fileName = MutConfiguration.AOF.FILE_NAME;

    static {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        init();
        // 初始化堆内存缓冲区；由于每种命令存在一个单独线程，所以每种命令对应一个缓冲区
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = ByteBuffer.allocate(bufferSize);
        }
    }

    public static void init(){
        scheduler.scheduleAtFixedRate(()  ->{{
                // 刷新所有的缓冲区
                log.info("AOF is Started.");
                flushAll();
            }
        }, 0, intervalTime, TimeUnit.MILLISECONDS);
    }

    public static void addCommand(byte[] cmd, byte[][] args){
        // 向缓冲区写命令信息
    }

    public static boolean isOpenAOF(){
        return true;
    }

    public static void addPacket(String key, byte[] value, Long expire, int bufferPosition) {
        ByteBuffer buffer = buffers[bufferPosition];
        if((buffer.remaining() - 100) < (key.getBytes().length + value.length + expire.toString().getBytes().length)){
            // 缓冲区内存即将耗尽；强制触发刷盘；仅刷新该缓冲区的数据到磁盘
            flushWithBufferPosition(bufferPosition);
        }
        buffer.putInt(key.getBytes().length);
        buffer.put(key.getBytes());
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putLong(expire);
    }

    // 刷入指定缓冲区的数据到磁盘
    private static void flushWithBufferPosition(int bufferPosition) {
        ByteBuffer buffer = buffers[bufferPosition];
        buffer.flip();
        try (FileChannel fileChannel = new FileOutputStream(fileName, true).getChannel()) {
            int written = fileChannel.write(buffer);
            log.info("Wrote {} bytes to file", written);
            // 清空缓冲区
            buffer.clear();
        }catch (IOException ioException){
            log.error(ioException);
        }
    }

    // 将所有缓冲区的数据刷入磁盘
    private static void flushAll() {
        for (int i = 0; i < buffers.length; i++) {
            flushWithBufferPosition(i);
        }
    }

    public static List<String> getValue(){
        List<String> res = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))){
            while (dis.available() > 0) {
                // 读取key的长度
                int length = dis.readInt();
                byte[] keyBytes = new byte[length];
                dis.readFully(keyBytes);
                String key = new String(keyBytes);
                // 读取value的长度
                int valueLength = dis.readInt();
                byte[] value = new byte[valueLength];
                dis.readFully(value);
                // 读取expire
                long expire = dis.readLong();
                // 封装结果
                res.add("key: " + key + " value: " + value + " expire: " + expire);
            }
        }catch (IOException ioException){
            log.error(ioException);
        }
        return res;
    }
}
