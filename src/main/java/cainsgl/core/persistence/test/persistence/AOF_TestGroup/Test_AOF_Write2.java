package cainsgl.core.persistence.test.persistence.AOF_TestGroup;

import cainsgl.core.persistence.AOF.impl.AOFListener2;
import cainsgl.core.persistence.AOF.WriteFactor;
import cainsgl.core.persistence.AOF.impl.AOFListener3;

public class Test_AOF_Write2 {

    static byte[][] arg = new byte[2][10];
    static byte[][] arg2 = new byte[2][10];

    static {
        arg[1] = "jack".getBytes();
        arg[0] = "name".getBytes();
        arg2[1] = "mary".getBytes();
        arg2[0] = "name10".getBytes();
    }

    public static void main(String[] args) throws InterruptedException {
        testAOF_Write_Instance();
    }

    public static void testAOF_Write_Static() throws InterruptedException {
        AOFListener2.addCommand("set".getBytes(), arg, AOFListener2.SET_BUFFER_POSITION, WriteFactor.getFactor("set"));

        Thread thread = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    AOFListener2.addCommand("set".getBytes(), arg, AOFListener2.SET_BUFFER_POSITION, WriteFactor.getFactor("set"));
                }
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    AOFListener2.addCommand("set".getBytes(), arg, AOFListener2.SET_BUFFER_POSITION, WriteFactor.getFactor("set"));
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    AOFListener2.addCommand("set".getBytes(), arg, AOFListener2.SET_BUFFER_POSITION, WriteFactor.getFactor("set"));
                }
            }
        });

        thread.start();
        thread1.start();
        thread2.start();

        Thread.sleep(3000);

        AOFListener2.addCommand("set".getBytes(), arg2, AOFListener2.SET_BUFFER_POSITION, WriteFactor.getFactor("set"));


    }

    public static void testAOF_Write_Instance() throws InterruptedException {
        /*
        * 每一个线程各自实例化一个监听器；独享自己的缓冲区以及监听器线程，原子类等
        * */
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // 创建该线程的AOF监听器实例
                AOFListener3 aofListener3 = new AOFListener3("set".getBytes(), 10001L);
                for (int i = 0; i < 100000; i++) {
                    aofListener3.addCommand(arg2, aofListener3.writeFactor().getWriteFactor(), System.currentTimeMillis());
                }
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                // 创建该线程的AOF监听器实例
                AOFListener3 aofListener3 = new AOFListener3("set".getBytes(), 10002L);
                for (int i = 0; i < 100000; i++) {
                    aofListener3.addCommand(arg2, aofListener3.writeFactor().getWriteFactor(), System.currentTimeMillis());
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                // 创建该线程的AOF监听器实例
                AOFListener3 aofListener3 = new AOFListener3("set".getBytes(), 10003L);
                for (int i = 0; i < 100000; i++) {
                    aofListener3.addCommand(arg2, aofListener3.writeFactor().getWriteFactor(), System.currentTimeMillis());
                }
            }
        });

        thread.start();
        thread1.start();
        thread2.start();
    }
}
