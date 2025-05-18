package cainsgl.core.command.config;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.manager.shunt.CommandShunt;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.persistence.MutSerializer;
import io.netty.channel.EventLoop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandConfiguration
{
    public CommandConfiguration()
    {
        new SetGetManager();
        new SimpleCommandManager();
    }
    private static final Map<String, MutSerializer> COMMAND_MANAGERS = new HashMap<>();
    private static final Map<String, byte[]> DESERIALIZER_MAP = new HashMap<>();
    private static final Map<MutSerializer, EventLoop> SERIALIZER_EVENT_LOOP_MAP = new HashMap<>();
    public static void register(MutSerializer manager, EventLoop eventLoop)
    {
        String className = manager.getClass().getName();
        COMMAND_MANAGERS.put(className, manager);
        SERIALIZER_EVENT_LOOP_MAP.put(manager, eventLoop);
        byte[] byteValue = DESERIALIZER_MAP.get(className);
        if (byteValue == null)
        {
            MutConfiguration.log.info("{} 该manager无序列化数据跳过", className);
            return;
        }
        eventLoop.submit(()->{
            manager.deSerializer(byteValue);
        });
    }

    public static void register(CommandShunt shunt, ShuntCommandManager manager)
    {
        String className = manager.getClass().getName();
        COMMAND_MANAGERS.put(className, shunt);
        byte[] byteValue = DESERIALIZER_MAP.get(className);
        if (byteValue == null)
        {
            MutConfiguration.log.info("{} ，该shunt无序列化数据，直接跳过", className);
            return;
        }
        shunt.deSerializer(byteValue);
    }



    public static Map<String, byte[]> getData()
    {
        Map<String, byte[]> data = new HashMap<>();
        for (Map.Entry<String, MutSerializer> entry : COMMAND_MANAGERS.entrySet())
        {
            data.put(entry.getKey(), entry.getValue().serialization());
        }
        return data;
    }

}
