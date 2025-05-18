package cainsgl.core.command.config;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.persistence.MutSerializer;

import java.util.HashMap;
import java.util.Map;

public class CommandConfiguration {

    private static Map<Integer, ByteValue> data = new HashMap<>();

    private static final Integer set = 1;
    private static final Integer list = 2;

    public CommandConfiguration()
    {
        new SetGetManager();
        new SimpleCommandManager();
    }

    public static Map<Integer, MutSerializer> getData()
    {
        return null;
    }

}

