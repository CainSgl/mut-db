package cainsgl.core.network.config;

import cainsgl.core.command.processor.CommandProcessor;

import cainsgl.core.data.key.ByteFastKey;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NetWorkConfig
{
    private static final Map<ByteFastKey, CommandProcessor> commandMap=new HashMap<>();
    public static  void register(byte[] key, CommandProcessor executor)
    {
        commandMap.put(new ByteFastKey(key),executor);
    }
    public static  void register(String command, CommandProcessor executor)
    {
        commandMap.put(new ByteFastKey(command.getBytes(StandardCharsets.UTF_8)),executor);
    }

    public static CommandProcessor getCmd(byte[] key)
    {
        return commandMap.get(new ByteFastKey(key));
    }
}
