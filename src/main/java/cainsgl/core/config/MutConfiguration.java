package cainsgl.core.config;

import cainsgl.core.network.server.MutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutConfiguration {
    public static Logger log = LoggerFactory.getLogger(MutServer.class);

    public static Integer PORT;

    public static Integer BACKLOG;
    public static Integer WORK_THREADS;
    public static Integer GC_THREADS;

    //哨兵模式
    public static Boolean SENTINEL_ACTIVATE;
    //假线程数量
    public static Boolean AUTO_SCALING_THREAD;
    //初始化hashtable的链表长度
    public static int INITIAL_CAPACITY;
    //设置hashtable的负载因子最大
    public static float MAX_LOAD_FACTOR;
    //设置hashtable的负载因子最小
    public static float MIN_LOAD_FACTOR;

    //分流器线程大小
    public static int SHUNT_THREADS;

    //5个key就分裂
    public static int MAX_OVER_LOAD;
    public static int MIN_OVER_LOAD;
    //一次rehash的次数
    public static int REHASH_NUM;

    // RDB相关配置
    public static class RDB
    {

        // 执行RDB间隔单位时间 单位毫秒
        public static long INTERVAL_TIME;

        // 单位时间内插入数据的次数
        public static long INSERT_COUNT;

        // 业务类型 - LOW; HIGH
        public static String BUSINESS_TYPE;

        // 最大延迟次数
        public static int MAX_DELAY_COUNT;

        // 指示 RDB文件名称 TODO - 暂时使用绝对路径
        public static String FILE_NAME;

    }

    public static class AOF
    {
        static {
            System.out.println("AOF属性被加载: " + "类加载器为：" + AOF.class.getClassLoader());
        }
        // 每一个独立缓冲区的内存大小
        public static int BUFFER_SIZE; // 500KB(约为50万字节)

        // 独立缓冲区的个数
        public static int BUFFER_COUNT;

        // AOF执行的间隔时间
        public static long INTERVAL_TIME;

        // AOF文件路径；暂时写死
        public static String FILE_NAME;

    }

    public static class GC
    {
        static {
            System.out.println("GC属性被加载: " + "类加载器为：" + AOF.class.getClassLoader());
        }
        //时间片轮数，必须是2的n次方
        public static int DEFAULT_SLOTS;
        //一个插槽的时间
        public static int TICK_DURATION;

        public static int UNIT_UPDATE_TIME;
    }

//    public static final Logger log = LoggerFactory.getLogger(MutServer.class);
//    public static final Integer PORT = 6379;
//    public static final Integer BACKLOG = 128;
//    public static final Integer WORK_THREADS = 2;
//    public static final Integer GC_THREADS = 2;
//    //哨兵模式
//    public static final Boolean SENTINEL_ACTIVATE = true;
//    //假线程数量
//    public static final Boolean AUTO_SCALING_THREAD = true;
//    //初始化hashtable的链表长度
//    public static final int INITIAL_CAPACITY = 2;
//    //设置hashtable的负载因子最大
//    public static final float MAX_LOAD_FACTOR = 2f;
//    //设置hashtable的负载因子最小
//    public static final float MIN_LOAD_FACTOR = 0.5f;
//
//    //分流器线程大小
//    public static final int SHUNT_THREADS = 2;
//
//    //5个key就分裂
//    public static final int MAX_OVER_LOAD = 3;
//    public static final int MIN_OVER_LOAD = 1;
//    //一次rehash的次数
//    public static final int REHASH_NUM = 6;
//
//    // RDB相关配置
//    public static class RDB
//    {
//
//        // 执行RDB间隔单位时间 单位毫秒
//        public static long INTERVAL_TIME = 2000L;
//
//        // 单位时间内插入数据的次数
//        public static long INSERT_COUNT = 10L;
//
//        // 业务类型 - LOW; HIGH
//        public static String BUSINESS_TYPE = "HIGH";
//
//        // 最大延迟次数
//        public static final int MAX_DELAY_COUNT = 5;
//
//        // 指示 RDB文件名称 TODO - 暂时使用绝对路径
//        public static final String FILE_NAME = "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\dump.txt";
//
//    }
//
//    public static class AOF
//    {
//
//        // 每一个独立缓冲区的内存大小
//        public static int BUFFER_SIZE = 1024 * 1024 * 4; // 500KB(约为50万字节)
//
//        // 独立缓冲区的个数
//        public static int BUFFER_COUNT = 2;
//
//        // AOF执行的间隔时间
//        public static long INTERVAL_TIME = 5000L;
//
//        // AOF文件路径；暂时写死
//        public static String FILE_NAME = "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\aof.txt";
//
//    }
//
//    public static class GC
//    {
//        //时间片轮数，必须是2的n次方
//        public static final int DEFAULT_SLOTS = 1024;
//        //一个插槽的时间
//        public static final int TICK_DURATION = 10;
//
//        public static final int UNIT_UPDATE_TIME = 5;
//    }
}
