package cainsgl.core.persistence.test.persistence.RDB_TestGroup;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.ConfigLoader;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.persistence.RDB.RDBPersistence;
import cainsgl.core.persistence.serializer.MutSerializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Test_RDB_Read2 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        try {
//            // 加载配置
//            ConfigLoader.loadConfig("D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\config\\mut-config.xml");
//
//            // 打印配置验证
//            System.out.println("Port: " + MutConfiguration.PORT);
//            System.out.println("RDB File: " + MutConfiguration.RDB.FILE_NAME);
//            // 其他初始化...
//
//            new CommandConfiguration();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        RDBPersistence.storage();

//        Map<String, byte[]> stringMap = RDBPersistence.readOnBytes();
//
//        // 模拟从CommandConfig中拿到数据；并验证逻辑
//        Map<String, byte[]> map = TestCommandConfiguration.getData();
//        System.out.println(map);
//
//        TestManager.map.forEach((key, value) -> {
//            System.out.println("key: " + new String(key) + " value: " + new String(value));
//        });
//
//        TestManager2.map.forEach((key, value) -> {
//            System.out.println("key: " + new String(key) + " value: " + new String(value));
//        });
    }
}
