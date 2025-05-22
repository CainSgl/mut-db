package cainsgl.core.storge.aof;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AofFileExecutor
{
    public void execute()
    {
        try
        {
            List<AofCommand> aofCommands = readAndSortCommands(MutConfiguration.AOF.FILE_NAME);
            for (AofCommand aofCommand : aofCommands)
            {
                CommandProcessor<?> cmd = NetWorkConfig.getCmd(aofCommand.methodName);
                cmd.submit(aofCommand.args, (response) -> {
                    MutConfiguration.log.info("execute command by aof {}", new String(aofCommand.methodName));
                }, null);
            }
        } catch (IOException e)
        {
            MutConfiguration.log.error("failed to read AOF file", e);
        }
    }

    public static class AofCommand
    {
        final long timestamp;
        final byte[] methodName;
        final byte[][] args;

        public AofCommand(long timestamp, byte[] methodName, byte[][] args)
        {
            this.timestamp = timestamp;
            this.methodName = methodName;
            this.args = args;
        }

        public long getTimestamp() {return timestamp;}
    }

    public AofFileExecutor()
    {

    }

    public List<AofCommand> readAndSortCommands(String aofDirPath) throws IOException
    {
        List<AofCommand> allCommands = new ArrayList<>();

        // 遍历目录下所有 .aof 文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                Paths.get(aofDirPath), "*.aof"))
        {
            for (Path filePath : stream)
            {
                // 1. 提取命令名称（如 "set"）
                String commandName = parseCommandName(filePath);
                // 2. 解析文件内容，获取所有命令的时间戳和参数
                List<AofCommand> commands = parseAofFile(filePath, commandName);
                allCommands.addAll(commands);
            }
        }
        // 按时间戳升序排序
        allCommands.sort(Comparator.comparingLong(AofCommand::getTimestamp));
        return allCommands;
    }


    /**
     * 解析单个AOF文件，生成命令列表
     */
    private List<AofCommand> parseAofFile(Path filePath, String fileName) throws IOException
    {
        List<AofCommand> commands = new ArrayList<>();
        byte[] commandName = fileName.getBytes(StandardCharsets.UTF_8);
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ))
        {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);  // 1MB 缓冲区

            while (channel.read(buffer) != -1)
            {
                buffer.flip();  // 切换为读模式

                while (buffer.hasRemaining())
                {
                    try
                    {
                        // 解析时间戳（8字节）
                        long timestamp = buffer.getLong();

                        // 解析参数个数（4字节）
                        int argCount = buffer.getInt();

                        // 解析每个参数（长度+内容）
                        byte[][] args = new byte[argCount][];
                        for (int i = 0; i < argCount; i++)
                        {
                            int argLength = buffer.getInt();
                            byte[] arg = new byte[argLength];
                            buffer.get(arg);
                            args[i] = arg;
                        }
                        if (CommandConfiguration.rdbTimeStamp > 0 && timestamp < CommandConfiguration.rdbTimeStamp)
                        {
                            //不加入，后续删除
                            continue;
                        }
                        commands.add(new AofCommand(timestamp, commandName, args));
                    } catch (java.nio.BufferUnderflowException e)
                    {
                        // 剩余数据不足，需要继续读取文件
                        break;
                    }
                }

                buffer.compact();  // 清空已读数据，保留未读数据
            }
            return commands;
        }
    }


    public String parseCommandName(Path filePath)
    {
        String fileName = filePath.getFileName().toString();
        String nameWithoutExt = fileName.replaceAll("\\.aof$", "");
        // 去除末尾的数字
        return nameWithoutExt.replaceAll("\\d+$", "");
    }




    private List<AofCommand> readValidCommands(Path filePath) throws IOException
    {
        String commandName = parseCommandName(filePath);
        List<AofCommand> validCommands = new ArrayList<>();

        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ))
        {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            while (channel.read(buffer) != -1)
            {
                buffer.flip();
                while (buffer.hasRemaining())
                {
                    try
                    {
                        long timestamp = buffer.getLong();
                        int argCount = buffer.getInt();
                        byte[][] args = new byte[argCount][];

                        for (int i = 0; i < argCount; i++)
                        {
                            int argLength = buffer.getInt();
                            byte[] arg = new byte[argLength];
                            buffer.get(arg);
                            args[i] = arg;
                        }

                        // 仅保留时间戳 >= RDB时间戳的命令
                        if (timestamp >= CommandConfiguration.rdbTimeStamp)
                        {
                            validCommands.add(new AofCommand(timestamp,
                                    commandName.getBytes(StandardCharsets.UTF_8), args));
                        }
                    } catch (BufferUnderflowException e)
                    {
                        break;
                    }
                }
                buffer.compact();
            }
        }
        return validCommands;
    }







    /**
     * 压缩指定目录下的所有AOF文件（支持动态写入不中断）
     */
    public void compressAofFiles( ) throws IOException {
        // 遍历目录下所有.aof文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(MutConfiguration.AOF.FILE_NAME), "*.aof")) {
            for (Path filePath : stream) {
                processSingleAofFile(filePath);
            }
        }
    }

    /**
     * 处理单个AOF文件（核心逻辑）
     * @param originalPath 原始AOF文件路径
     * @throws IOException 文件操作异常
     */
    private void processSingleAofFile(Path originalPath) throws IOException {
        // 1. 生成带时间戳的备份路径（避免同名冲突）
        String fileName = originalPath.getFileName().toString();
        Path backupPath = originalPath.resolveSibling(
                fileName.replace(".aof", "") + "_bak_.aof"
        );

        // 2. 原子移动原文件到备份路径（关键：确保写入重定向）
        Files.move(originalPath, backupPath,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        );
        MutConfiguration.log.info("Moved [{}] to backup: [{}]", originalPath, backupPath);

        // 3. 创建同名新文件（后续新命令写入此文件）
        Path newAofPath = Files.createFile(originalPath);
        MutConfiguration.log.info("Created new AOF file for writing: [{}]", newAofPath);

        // 4. 提交后台压缩任务
        Thread.ofVirtual().factory().newThread(() -> {
            try {
                // 压缩备份文件（筛选有效命令）
                List<AofCommand> validCommands = readValidCommands(backupPath);
                // 追加有效命令到新文件
                if (!validCommands.isEmpty()) {
                    appendCommandsToFile(newAofPath, validCommands);
                }
                Files.deleteIfExists(backupPath);
                MutConfiguration.log.info("Compressed [{}] (kept {} commands)",
                        originalPath, validCommands.size());
            } catch (IOException e) {
                handleCompressionError(backupPath, e);
            }
        }).start();
    }


    /**
     * 将有效命令追加到新AOF文件
     */
    private void appendCommandsToFile(Path newAofPath, List<AofCommand> commands) throws IOException {
        try (FileChannel channel = FileChannel.open(
                newAofPath,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND))
        {
            for (AofCommand cmd : commands) {
                writeCommandToChannel(channel, cmd);
            }
        }
    }

    /**
     * 处理压缩异常（保留备份文件）
     */
    private void handleCompressionError(Path backupPath, IOException e) {
        MutConfiguration.log.error("Compression failed for [{}]: {}", backupPath, e.getMessage());
        try {
            // 将失败的备份文件重命名为error_前缀
            Path errorPath = backupPath.resolveSibling("error_" + backupPath.getFileName());
            Files.move(backupPath, errorPath, StandardCopyOption.REPLACE_EXISTING);
            MutConfiguration.log.info("Renamed failed backup to: [{}]", errorPath);
        } catch (IOException ex) {
            MutConfiguration.log.error("Failed to rename error backup [{}]: {}", backupPath, ex.getMessage());
        }
    }



    private void writeCommandToChannel(FileChannel channel, AofCommand cmd) throws IOException
    {
        int totalSize = 8 + 4;
        for (byte[] arg : cmd.args)
        {
            totalSize += 4 + arg.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.putLong(cmd.timestamp);
        buffer.putInt(cmd.args.length);
        for (byte[] arg : cmd.args)
        {
            buffer.putInt(arg.length);
            buffer.put(arg);
        }
        buffer.flip();  // 切换为写模式
        channel.write(buffer);
    }
}
