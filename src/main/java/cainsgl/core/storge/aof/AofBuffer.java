package cainsgl.core.storge.aof;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.system.GcSystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;


public class AofBuffer
{
    private final String filePath;
    // 内存缓冲区
    private final ByteBuffer buffer;
    private long lastFlushTime;
    // 刷盘策略
    private static final int flushPolicy = MutConfiguration.AOF.flushPolicy;
    private final FileChannel fileChannel;

    public AofBuffer(String filePath) throws IOException
    {
        this.filePath = MutConfiguration.AOF.FILE_NAME+filePath+".aof";
        this.buffer = ByteBuffer.allocateDirect(MutConfiguration.AOF.BUFFER_SIZE);
        this.lastFlushTime = GcSystem.updateTime;
        // 初始化FileChannel（追加模式，自动创建文件）
        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.APPEND);
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        Path path = Paths.get(this.filePath);
        this.fileChannel = FileChannel.open(path, options);
    }

    public  boolean write(byte[][] command)
    {
        byte[] data = serialize(command);

        if (buffer.remaining() < data.length)
        {
            return false;
        }

        buffer.put(data);
        triggerFlushByPolicy();
        return true;
    }

    /**
     * 根据策略触发刷盘
     */
    private void triggerFlushByPolicy()
    {
        switch (flushPolicy)
        {
            case 2:  // ALWAYS策略：立即刷盘
                flush();
                break;
            case 3:  // EVERY_SEC策略：每秒刷盘
                long currentTime = GcSystem.updateTime;
                if (currentTime - lastFlushTime > 1000)
                {
                    flush();
                }
                break;
            case 1:  // NO策略：由外部统一刷盘
            default:
                break;
        }
    }

    /**
     * 执行刷盘（使用FileChannel写入）
     */
    public void flush()
    {
        if (buffer.position() == 0) {return;}
        buffer.flip();
        try
        {
            // 使用FileChannel写入
            while (buffer.hasRemaining())
            {
                fileChannel.write(buffer);
            }
            fileChannel.force(true);  // 强制
        } catch (IOException e)
        {
            throw new RuntimeException("AOF刷盘失败: " + filePath, e);
        } finally
        {
            reset();  // 重置指针
        }
    }

    /**
     * 关闭资源（重要：避免文件句柄泄漏）
     */
    public void close()
    {
        try
        {
            if (fileChannel != null && fileChannel.isOpen())
            {
                flush();  // 关闭前强制刷盘剩余数据
                fileChannel.close();
            }
        } catch (IOException e)
        {
            MutConfiguration.log.error("关闭AOF通道失败:{} ", filePath, e);
        }
    }

    // 以下为原有核心方法（保持不变）
    private void reset()
    {
        buffer.clear();
        lastFlushTime = GcSystem.updateTime;
    }


    private byte[] serialize(byte[][] command)
    {
        try (var baos = new java.io.ByteArrayOutputStream();
             var dos = new java.io.DataOutputStream(baos))
        {
            dos.writeLong(GcSystem.updateTime);  // 时间戳
            dos.writeInt(command.length);               // 参数个数
            for (byte[] arg : command)
            {                // 参数内容
                dos.writeInt(arg.length);
                dos.write(arg);
            }
            return baos.toByteArray();
        } catch (java.io.IOException e)
        {
            throw new RuntimeException("序列化失败", e);
        }
    }

    // Getter方法
    public String getFilePath() {return filePath;}

    public long getLastFlushTime() {return lastFlushTime;}
}
