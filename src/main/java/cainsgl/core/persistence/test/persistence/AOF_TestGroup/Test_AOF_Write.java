package cainsgl.core.persistence.test.persistence.AOF_TestGroup;

import cainsgl.core.config.ConfigLoader;
import cainsgl.core.persistence.test.command.SetProcessor;
import cainsgl.core.persistence.test.mainMemory.Data;
import cainsgl.core.persistence.test.mainMemory.RedisObj;

import java.util.ArrayList;
import java.util.List;

public class Test_AOF_Write {
    public static void main(String[] args) {
        // 加载配置文件
        try {
            ConfigLoader.loadConfig("D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\config\\mut-config.xml");
        } catch (Exception e) {

        }


        SetProcessor setProcessor = new SetProcessor();
        setProcessor.execute("key", "value1".getBytes(), 3000L);
        setProcessor.execute("key1", "value2".getBytes(), 5000L);
        setProcessor.execute("key2", "value3".getBytes(), 2000L);
        setProcessor.execute("key3", "value4".getBytes(), 1000L);
        List<String> keyList = new ArrayList<>(Data.getData().keySet());
        for (String key : keyList) {
            RedisObj<?> redisObj = Data.getData().get(key);
            System.out.println("key:" + key + ", value:" + redisObj.getValue());
        }
    }
}
