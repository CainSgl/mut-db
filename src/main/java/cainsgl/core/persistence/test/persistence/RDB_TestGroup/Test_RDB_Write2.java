package cainsgl.core.persistence.test.persistence.RDB_TestGroup;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.persistence.RDB.RDBPersistence;
import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.persistence.serializer.MutSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Test_RDB_Write2 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // 模拟向工作内存填充数据
        Map<String, MutSerializable> test = TestCommandConfiguration.test();
        TestManager testManager = new TestManager();
        byte[] key = "key".getBytes();
        byte[] value = "value".getBytes();
        testManager.map.put(key, value);

        TestManager2 test2 = new TestManager2();
        byte[] key2 = "key2".getBytes();
        byte[] value2 = "value2".getBytes();
        test2.map.put(key2, value2);

        // 模拟向CommandConfig中填充该manager工作内存的信息
        test.put(testManager.getClass().getName(), testManager);
        test.put(test2.getClass().getName(), testManager);

        // 模拟从CommandConfig中拿到数据；并验证逻辑
//        Map<String, byte[]> map = TestCommandConfiguration.getData();
//        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
//            String managerClassName = entry.getKey();
//            Class<?> managerClass = Class.forName(managerClassName);
//            Constructor<?> constructor = managerClass.getConstructor();
//            MutSerializable manager = (MutSerializable)constructor.newInstance();
//
//            manager.deSerializer(entry.getValue());
//
//            System.out.println(manager.getClass());
//        }

        // NOTE 模拟RDB行为
        RDBPersistence.storage();


    }
}
