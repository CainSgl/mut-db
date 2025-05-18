package cainsgl.core.network.config;

import cainsgl.core.command.manager.AbstractCommandManager;
import cainsgl.core.data.ByteKey;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NetWorkConfig
{
    private static final Map<ByteKey, AbstractCommandManager.CommandAdaptor> commandMap=new HashMap<ByteKey, AbstractCommandManager.CommandAdaptor>();
    public static  void register(byte[] key, AbstractCommandManager.CommandAdaptor manager)
    {
        commandMap.put(new ByteKey(key),manager);
    }
    public static  void register(String command, AbstractCommandManager.CommandAdaptor manager)
    {
        commandMap.put(new ByteKey(command.getBytes(StandardCharsets.UTF_8)),manager);
    }

    public static AbstractCommandManager.CommandAdaptor getCmd(byte[] key)
    {
        return commandMap.get(new ByteKey(key));
    }
}
