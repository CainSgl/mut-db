package cainsgl.core.persistence.test.persistence.RDB_TestGroup;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.persistence.serializer.MutSerializable;

import java.util.HashMap;
import java.util.Map;

public class TestCommandConfiguration {

    public static Map<String, MutSerializable> test(){
        return COMMAND_MANAGERS;
    }

    private static final Map<String, MutSerializable> COMMAND_MANAGERS = new HashMap<>();

    //RDB
    public static Map<String, byte[]> getData()
    {

        Map<String, byte[]> data = new HashMap<>();
        for (Map.Entry<String, MutSerializable> entry : COMMAND_MANAGERS.entrySet())
        {
            MutSerializable value = entry.getValue();
            byte[] serialization = value.serialization();
            try
            {
                data.put(entry.getKey(), serialization);
            } catch (Exception e)
            {
                MutConfiguration.log.error("序列化过程出现异常", e);
            }
        }
        return data;
    }

}
