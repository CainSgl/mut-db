package cainsgl.core.persistence.test;

import java.util.HashMap;
import java.util.Map;

public class HashMapConcurrencyDemo {
    static int count = 0;

    static Map<Integer, Integer> map = new HashMap<>();
    public static void main(String[] args) throws InterruptedException {
        // 写线程：持续 put 数据
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("put了一个数据");
                map.put(i, i);
            }
        });

        // 读线程：持续 get 数据
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Integer value = map.get(i);
                System.out.println("get 了一个数据 - " + i + ":"  + value );
                if (value != null && value != i) {
                    count++;
                    System.out.println("数据不一致！key=" + i + ", value=" + value);
                }
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();
    }
}
