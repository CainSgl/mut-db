package cainsgl.core.command;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MUT
{
    public static boolean execute(byte[] cmd, byte[][] args, Consumer<RESP2Response> consumer)
    {
        CommandProcessor<?> cmd1 = NetWorkConfig.getCmd(cmd);
        if (cmd1 == null)
        {
            MutConfiguration.log.error("执行命令：无法找到该命令");
            return false;
        }
        if (args.length < cmd1.minCount() || args.length > cmd1.maxCount())
        {
            MutConfiguration.log.error("执行命令：参数错误");
            return false;
        }
        cmd1.submit(args, consumer, null);
        return true;
    }

    public static boolean execute(Consumer<RESP2Response> consumer, String... cmd)
    {
        // 校验输入：至少需要1个元素（命令）
        if (cmd == null || cmd.length == 0)
        {
            MutConfiguration.log.error("命令参数为空");
            return false;
        }
        // 解析命令（第一个元素）
        String commandStr = cmd[0];
        if (commandStr == null || commandStr.isEmpty())
        {
            MutConfiguration.log.error("命令字符串为空");
            return false;
        }
        byte[] commandBytes = commandStr.getBytes(StandardCharsets.UTF_8); // 转换为字节数组
        // 解析参数（剩余元素）
        byte[][] argsBytes;
        if (cmd.length > 1)
        {
            argsBytes = new byte[cmd.length - 1][];
            for (int i = 1; i < cmd.length; i++)
            {
                String arg = cmd[i];
                argsBytes[i - 1] = (arg != null ? arg : "").getBytes(StandardCharsets.UTF_8); // 处理null参数
            }
        } else
        {
            argsBytes = new byte[0][]; // 无参数时返回空数组
        }

        return execute(commandBytes, argsBytes, consumer);
    }
    public static boolean execute(String cmd,Consumer<RESP2Response> consumer)
    {
        return execute(consumer,cmd.split(" "));
    }




}
