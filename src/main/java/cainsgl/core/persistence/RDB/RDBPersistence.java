package cainsgl.core.persistence.RDB;


import cainsgl.core.config.MutConfiguration;
import cainsgl.废案.serializer_abandon.impl.RedisObjSerializer;
import cainsgl.废案.serializer_abandon.valueObj.DeserializeVO;
import cainsgl.core.persistence.test.mainMemory.Data;
import cainsgl.core.persistence.test.mainMemory.RedisObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RDBPersistence {
    private static final Logger log = LoggerFactory.getLogger(RDBPersistence.class);

    private static final String fileName = MutConfiguration.RDB.FILE_NAME;

    private static final RedisObjSerializer redisObjSerializer = new RedisObjSerializer();

    public static void storage() {
        log.debug("Starting task!");
        // 模拟从主内存中拿取数据
        HashMap<String, RedisObj<?>> data = Data.getData();
        List<String> keyList = new ArrayList<>(data.keySet());
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName))) {
            for (String key : keyList) {
                RedisObj<?> redisObj = data.get(key);
                byte[] serialized = redisObjSerializer.serialize(redisObj, key);
                // 使用长度前缀法；写入真是数据之前先写入这个数组的字节数组长度
                out.writeInt(serialized.length);
                out.write(serialized);
            }
        } catch (IOException e) {
            log.error("Error while writing to file", e);
            throw new RuntimeException(e);
        }
    }

    public static void readOnBytes(){
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))) {
            while (dis.available() > 0) {
                // 先读取数据的长度
                int length = dis.readInt();

                // 按照数据的长度读取字节数组
                byte[] data = new byte[length];
                dis.readFully(data);

                // 反序列化数据
                DeserializeVO deserializeVO = redisObjSerializer.deserialize(data);
                log.debug("Now Value: {}", deserializeVO);
                RedisObj<?> redisObj = deserializeVO.redisObj();
                String key = deserializeVO.key();
                Data.put(key, redisObj);
            }
        } catch (IOException e) {
            log.error("Error while reading from file!", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found While Deserialize This Data!", e);
            throw new RuntimeException(e);
        }
    }
}
