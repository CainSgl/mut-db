package cainsgl.core.persistence.test.persistence.AOF_TestGroup;

import cainsgl.core.persistence.AOF.WriteFactor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test_WriteFactor {

    public static void main(String[] args) {

        // 写入线程，从原子类中读取factor属性
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    int factor = WriteFactor.getFactor("set");
                    System.out.println("当前时刻原子类中的factor属性为: " + factor);
                }
            }
        });

        // 异步线程；定时任务，定时修改原子类中的factor属性
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                WriteFactor.setFactor("set", 3);
            }
        }, 3, TimeUnit.SECONDS);

        thread.start();

        executor.schedule(new Runnable() {
            @Override
            public void run() {
                WriteFactor.setFactor("set", 10);
            }
        }, 7, TimeUnit.SECONDS);

    }
}
