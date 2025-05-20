package cainsgl.core.persistence.test.serialzie;

import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.persistence.serializer.MutSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test_DefaultSerializer {
    public static void main(String[] args) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                MString mString = new MString();
                mString.name = "name";
                MString mString2 = new MString();
                mString2.name = "mary";
                MString mString3 = new MString();
                mString3.name = "age";
                MString mString4 = new MString();
                mString4.name = "18";
                MString mString5 = new MString();
                mString5.name = "addr";
                MString mString6 = new MString();
                mString6.name = "beijing";
                Map<MString, MString> map = new HashMap<>();
                map.put(mString, mString2);
                map.put(mString3, mString4);
                map.put(mString5, mString6);
                // 序列化得到的字节数组
                byte[] bytes = MutSerializer.serialize(map);
                System.out.println(Arrays.toString(bytes));

                System.out.println(mString.getClass().getName());

                Map<MString, MString> map2 = new HashMap<>();
                MutSerializer.deserialize(bytes, map2);

                map.forEach((key, value) -> {
                    System.out.println("key: " + key + ", value: " + value);
                });
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                Integer agg = 12;
                Integer agg2 = 12;
                Integer agg3 = 12;
                Integer agg4 = 12;
                Integer agg5 = 12;
                Integer agg6 = 12;
                String key = "key";
                String key2 = "key2";
                String key3 = "key3";
                String key4 = "key4";
                String key5 = "key5";
                String key6 = "key6";
                Map<String, Integer> map = new HashMap<>();
                map.put(key, agg);
                map.put(key2, agg2);
                map.put(key3, agg3);
                map.put(key4, agg4);
                map.put(key5, agg5);
                map.put(key6, agg6);
                byte[] bytes = MutSerializer.serialize(map);
                System.out.println(Arrays.toString(bytes));

                HashMap<String, Integer> map2 = new HashMap<>();
                MutSerializer.deserialize(bytes, map2);
                map2.forEach((k, value) -> {
                    System.out.println("key: " + k + " value: " + value);
                });
            }
        });

        Thread t3 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Integer agg = 12;
                    Integer agg2 = 12;
                    Integer agg3 = 12;
                    Integer agg4 = 12;
                    Integer agg5 = 12;
                    Integer agg6 = 12;
                    String key = "key";
                    String key2 = "key2";
                    String key3 = "key3";
                    String key4 = "key4";
                    String key5 = "key5";
                    String key6 = "key6";
                    Map<String, Integer> map = new HashMap<>();
                    map.put(key, agg);
                    map.put(key2, agg2);
                    map.put(key3, agg3);
                    map.put(key4, agg4);
                    map.put(key5, agg5);
                    map.put(key6, agg6);
                    byte[] bytes = MutSerializer.serialize(map);
                    System.out.println(Arrays.toString(bytes));

                    HashMap<String, Integer> map2 = new HashMap<>();
                    MutSerializer.deserialize(bytes, map2);
                    map2.forEach((k, value) -> {
                        System.out.println("key: " + k + " value: " + value);
                    });
                }
            }
        });

        t.start();
        t2.start();
        t3.start();
    }
}
