package cainsgl.core.persistence.RDB;


import cainsgl.core.config.MutConfiguration;
import cainsgl.core.persistence.serializer.impl.RedisObjSerializer;
import cainsgl.core.persistence.serializer.valueObj.DeserializeVO;
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
    private static final String SPLIT = "/";

    private static final RedisObjSerializer redisObjSerializer = new RedisObjSerializer();

    public static void storage() {
        log.debug("Starting task!");
        // 模拟从主内存中拿取数据
        HashMap<String, RedisObj<?>> data = Data.getData();
        List<String> keyList = new ArrayList<>(data.keySet());
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName))){
            for (String key : keyList) {
                RedisObj<?> redisObj = data.get(key);
                byte[] serialized = redisObjSerializer.serialize(redisObj, key);
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : serialized) {
                    stringBuilder.append(b);
                    stringBuilder.append(SPLIT);
                }
                fileWriter.write(stringBuilder.toString());
                fileWriter.newLine();
            }
        } catch (IOException e) {
            log.error("Error while writing to file", e);
            throw new RuntimeException(e);
        }
    }

    public static void readOnBytes(){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bytesInfo = line.split(SPLIT);
                byte[] value = new byte[bytesInfo.length];
                for (int i = 0; i < bytesInfo.length; i++) {
                    value[i] = Byte.parseByte(bytesInfo[i]);
                }
                try {
                    DeserializeVO deserialize = redisObjSerializer.deserialize(value);
                    System.out.println(deserialize.key() + "; " + deserialize.redisObj().getValue() + "; " + deserialize.redisObj().getType());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            log.error("Error while reading from file! RDB file ERR", e);
            throw new RuntimeException(e);
        }
    }
}
