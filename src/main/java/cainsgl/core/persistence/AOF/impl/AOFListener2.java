package cainsgl.core.persistence.AOF.impl;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.excepiton.MutPersistenceException;
import cainsgl.core.persistence.AOF.WriteFactor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
*  AOF_异步刷盘_静态方法
* */
public class AOFListener2 {
    private static final Logger log = LogManager.getLogger(AOFListener2.class);

    public static final int SET_BUFFER_POSITION = 0;
    public static final int LIST_BUFFER_POSITION = 1;

    // 堆内存的分配大小
    private static final int bufferSize = MutConfiguration.AOF.BUFFER_SIZE;
    // 独立缓冲区的个数
    private static final int bufferCount = MutConfiguration.AOF.BUFFER_COUNT;
    // 从缓冲区个数
    private static final int slaveCount = 1;
    // 堆内存缓冲区；用于缓冲命令信息；包括主从缓冲区结构
    private static final ByteBuffer[][] buffers = new ByteBuffer[bufferCount][1 + slaveCount];

    // AOF定时任务线程池；只负责向异步刷盘提交任务
    private static final ScheduledExecutorService scheduler;
    // AOF间隔时间 单位/毫秒
    private static final long intervalTime = MutConfiguration.AOF.INTERVAL_TIME;

    // 异步刷盘线程
    private static final ExecutorService diskExecutor;

    // AOF文件名称；暂时写死
    private static final String fileName = MutConfiguration.AOF.FILE_NAME;

    static {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        diskExecutor = Executors.newSingleThreadExecutor();
        init();
        // 初始化堆内存缓冲区；由于每种命令存在一个单独线程，所以每种命令对应一个缓冲区
        for (int i = 0; i < buffers.length; i++) {
            for (int j = 0; j < buffers[i].length; j++) {
               buffers[i][j] = ByteBuffer.allocate(bufferSize);
            }
        }
    }

    private static void init(){
        scheduler.scheduleAtFixedRate(()  ->{{
            // 刷新所有的缓冲区
            log.info("AOF Sync is Started.");
            flushAllAsync();
        }
        }, 0, intervalTime, TimeUnit.MILLISECONDS);
    }

    // 向指定缓冲区写命令信息
    public static void addCommand(byte[] cmd, byte[][] args, int bufferPosition, int writeFactor){
        if(bufferPosition >= buffers.length){
            throw new MutPersistenceException("BufferPosition out of bounds.");
        }
        // 获取当前状态对应的缓冲区
        ByteBuffer curBuffer = buffers[bufferPosition][writeFactor];
        // 向缓冲区写入数据之前检查缓冲区空间是否充足
        long valueLength = 0L;
        for (byte[] bytes : args) {
            valueLength += bytes.length;
        }
        if((curBuffer.remaining()) < (cmd.length + valueLength)){
            // 达到阈值则触发异步刷盘
            log.info("缓冲区即将写满，触发AOF");
            flushWithBufferPositionAsync(bufferPosition);
        }
        logForAOF(bufferPosition, writeFactor);
        curBuffer.putInt(cmd.length);
        curBuffer.put(cmd);
        // 依次写入参数args
        for (byte[] arg : args) {
            curBuffer.putInt(arg.length);
            curBuffer.put(arg);
        }
    }

    // 异步刷盘；刷所有
    private static void flushAllAsync(){
        // 提交一个异步刷盘任务就说明触发了AOF
        diskExecutor.submit(AOFListener2::flushAll);
    }

    // 异步刷盘；刷指定缓冲区
    private static void flushWithBufferPositionAsync(int bufferPosition){
        diskExecutor.submit(() -> {
            flushWithBufferPosition(bufferPosition);
        });
    }

    // 异步刷盘；刷所有
    private static void flushAll(){
        for (int i = 0; i < buffers.length; i++) {
            flushWithBufferPosition(i);
        }
    }

    // 刷新指定缓冲区的数据
    private static void flushWithBufferPosition(int bufferPosition){
        // 先修改原子类中的factor属性
        if(SET_BUFFER_POSITION == bufferPosition){
             WriteFactor.setFactor("set", 1);
        }
        else if(LIST_BUFFER_POSITION == bufferPosition){
            WriteFactor.setFactor("list", 1);
        }
        // 拿到主缓冲区引用
        ByteBuffer curBuffer = buffers[bufferPosition][0];
        curBuffer.flip();
        // 执行刷盘逻辑
        try (FileChannel fileChannel = new FileOutputStream(fileName, true).getChannel()){
            int written = fileChannel.write(curBuffer);
            log.info("Written {} bytes to {}", written, curBuffer);
            // 清空缓冲区
            curBuffer.clear();
        }catch (IOException e){
            throw new MutPersistenceException("ERR While do AOF.");
        }
        // 刷盘完成；切换主从缓冲区并重置factor
        exchangeSlaveAndMaster(bufferPosition);
        WriteFactor.setFactor("set", 0);
    }

    public static boolean isOpenAOF(){
        return true;
    }

    // 切换指定缓冲区类型的主从模式
    private static void exchangeSlaveAndMaster(int bufferPosition){
        // 第一个元素永远是主缓冲区
        ByteBuffer temp = buffers[bufferPosition][0];
        buffers[bufferPosition][0] = buffers[bufferPosition][1];
        buffers[bufferPosition][1] = temp;
    }

    // 日志方法
    private static void logForAOF(int bufferPosition, int writeFactor){
        if(writeFactor == 1){
            log.info("当前触发了AOF，写入线程正在向从缓冲区写入数据");
        }
    }

}
