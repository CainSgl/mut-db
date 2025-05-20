package cainsgl.core.network.config;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteFastIgnoreCaseKey;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NetWorkConfig
{
    private static final Map<ByteFastIgnoreCaseKey, CommandProcessor<?>> commandMap=new HashMap<>();
    public static  void register(byte[] key, CommandProcessor<?> executor)
    {
        ByteFastIgnoreCaseKey byteFastIgnoreCaseKey = new ByteFastIgnoreCaseKey(key);
        if(commandMap.containsKey(byteFastIgnoreCaseKey))
        {
            throw new IllegalArgumentException("不能添加重复的命令:"+new String(key,StandardCharsets.UTF_8));
        }
        commandMap.put(byteFastIgnoreCaseKey,executor);
    }
    public static  void register(String command, CommandProcessor<?> executor)
    {
        register(command.getBytes(StandardCharsets.UTF_8),executor);
    }

    public static CommandProcessor<?> getCmd(byte[] key)
    {
        return commandMap.get(new ByteFastIgnoreCaseKey(key));
    }
    public static Map<ByteFastIgnoreCaseKey,CommandProcessor<?>> getAllCommand()
    {
        return commandMap;
    }
}
