package cainsgl.core.persistence.RDB;


import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.persistence.serializer.util.SeriUtil;
import cainsgl.core.persistence.RDB.serializer_rdb.impl.RedisObjSerializer;
import cainsgl.core.persistence.RDB.serializer_rdb.valueObj.DeserializeVO;
import cainsgl.core.persistence.test.mainMemory.Data;
import cainsgl.core.persistence.test.mainMemory.RedisObj;
import cainsgl.core.persistence.test.persistence.RDB_TestGroup.TestCommandConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class RDBPersistence {
    private static final Logger log = LoggerFactory.getLogger(RDBPersistence.class);

    private static final String fileName = MutConfiguration.RDB.FILE_NAME;

    private static final RedisObjSerializer redisObjSerializer = new RedisObjSerializer();

    private static final int INT_LENGTH_PREFIX = 4;

    public static void storage() {
        log.debug("Starting task!");
        // 模拟从主内存中拿取数据
        Map<String, byte[]> dataGroup = TestCommandConfiguration.getData();
        Map<String, byte[]> map = CommandConfiguration.getData();
        dataGroup.forEach((managerClassName, value) -> {
            byte[] res = new byte[4 + managerClassName.getBytes().length + value.length + 4];
            int offset = 0;
            // 先写入key的长度；一个字节
            System.arraycopy(SeriUtil.intToBytes(managerClassName.length()), 0, res, offset, 4);
            offset += 4;
            // 再写入key的数据
            System.arraycopy(managerClassName.getBytes(), 0, res, offset, managerClassName.getBytes().length);
            offset += managerClassName.getBytes().length;
            // 再写入value的长度
            System.arraycopy(SeriUtil.intToBytes(value.length), 0, res, offset, 4);
            offset += 4;
            System.arraycopy(value, 0, res, offset, value.length);

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName, true))){
                out.write(res);
            }catch (IOException e) {
                log.error("Error while writing to file", e);
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<String, byte[]> readOnBytes(){
        Map<String, byte[]> dataGroup = TestCommandConfiguration.getData();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))) {
            while (dis.available() > 0) {
                int keyLength = dis.readInt();
                System.out.println("keyLength: " + keyLength);
                byte[] managerClassName = new byte[keyLength];
                dis.readFully(managerClassName);
                System.out.println("managerClassName: " + new String(managerClassName));

                int valueLength = dis.readInt();
                byte[] value = new byte[valueLength];
                dis.readFully(value);

                // 主动向各个工作内存填充数据；以及向commandConfig填充数据
                try {
                    Class<?> managerClass = Class.forName(new String(managerClassName));
                    Constructor<?> constructor = managerClass.getConstructor();
                    MutSerializable manager = (MutSerializable) constructor.newInstance();
                    manager.deSerializer(value);

                    TestCommandConfiguration.test().put(new String(managerClassName), manager);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                dataGroup.put(new String(managerClassName), value);
            }
            return dataGroup;
        } catch (IOException e) {
            log.error("Error while reading from file!", e);
            throw new RuntimeException(e);
        }
    }
}
