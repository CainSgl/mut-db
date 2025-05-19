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
        commandMap.put(new ByteFastIgnoreCaseKey(key),executor);
    }
    public static  void register(String command, CommandProcessor<?> executor)
    {
        commandMap.put(new ByteFastIgnoreCaseKey(command.getBytes(StandardCharsets.UTF_8)),executor);
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
