package cainsgl.core.config;

import cainsgl.core.network.server.MutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MutConfiguration
{
//    public static Logger log  ;
//
//    public static Integer PORT;
//
//    public static Integer BACKLOG;
//    public static Integer WORK_THREADS;
//    public static Integer GC_THREADS;
//
//    //哨兵模式
//    public static Boolean SENTINEL_ACTIVATE;
//    //假线程数量
//    public static Boolean AUTO_SCALING_THREAD;
//    //初始化hashtable的链表长度
//    public static int INITIAL_CAPACITY;
//    //设置hashtable的负载因子最大
//    public static float MAX_LOAD_FACTOR;
//    //设置hashtable的负载因子最小
//    public static float MIN_LOAD_FACTOR;
//
//    //分流器线程大小
//    public static int SHUNT_THREADS;
//
//    //5个key就分裂
//    public static int MAX_OVER_LOAD;
//    public static int MIN_OVER_LOAD;
//    //一次rehash的次数
//    public static int REHASH_NUM;
//
//    // RDB相关配置
//    public static class RDB
//    {
//
//        // 执行RDB间隔单位时间 单位毫秒
//        public static long INTERVAL_TIME;
//
//        // 单位时间内插入数据的次数
//        public static long INSERT_COUNT;
//
//        // 业务类型 - LOW; HIGH
//        public static String BUSINESS_TYPE;
//
//        // 最大延迟次数
//        public static int MAX_DELAY_COUNT;
//
//        // 指示 RDB文件名称 TODO - 暂时使用绝对路径
//        public static String FILE_NAME;
//
//    }
//
//    public static class AOF
//    {
//        static {
//            System.out.println("AOF属性被加载: " + "类加载器为：" + AOF.class.getClassLoader());
//        }
//        // 每一个独立缓冲区的内存大小
//        public static int BUFFER_SIZE; // 500KB(约为50万字节)
//
//        // 独立缓冲区的个数
//        public static int BUFFER_COUNT;
//
//        // AOF执行的间隔时间
//        public static long INTERVAL_TIME;
//
//        // AOF文件路径；暂时写死
//        public static String FILE_NAME;
//
//    }
//
//    public static class GC
//    {
//        static {
//            System.out.println("GC属性被加载: " + "类加载器为：" + AOF.class.getClassLoader());
//        }
//        //时间片轮数，必须是2的n次方
//        public static int DEFAULT_SLOTS;
//        //一个插槽的时间
//        public static int TICK_DURATION;
//
//        public static int UNIT_UPDATE_TIME;
//    }


    static
    {
        log = LoggerFactory.getLogger(MutServer.class);
        MutPropertiesSourceLoader sourceLoader = MutPropertiesSourceLoader.getInstance();
        try
        {
            sourceLoader.loadConfigByEnvrioment();
        } catch (Exception e)
        {
            LoggerFactory.getLogger(MutServer.class).error("加载配置时出现异常", e);
        }
        PORT = sourceLoader.getBaseInfoByDefault("port", 6379);

        // 初始化基础配置（对应base.*键）
        BACKLOG = sourceLoader.getBaseInfoByDefault("backlog", 128);
        WORK_THREADS = sourceLoader.getBaseInfoByDefault("workThreads", 2);
        GC_THREADS = sourceLoader.getBaseInfoByDefault("gcThreads", 2);
        SENTINEL_ACTIVATE = sourceLoader.getBaseInfoByDefault("sentinelActivate", true);
        AUTO_SCALING_THREAD = sourceLoader.getBaseInfoByDefault("autoScalingThread", true);
        INITIAL_CAPACITY = sourceLoader.getBaseInfoByDefault("initialCapacity", 2);
        MAX_LOAD_FACTOR = sourceLoader.getBaseInfoByDefault("maxLoadFactor", 2.0f);
        MIN_LOAD_FACTOR = sourceLoader.getBaseInfoByDefault("minLoadFactor", 0.5f);
        SHUNT_THREADS = sourceLoader.getBaseInfoByDefault("shuntThreads", 2);
        MAX_OVER_LOAD = sourceLoader.getBaseInfoByDefault("maxOverLoad", 3);
        MIN_OVER_LOAD = sourceLoader.getBaseInfoByDefault("minOverLoad", 1);
        REHASH_NUM = sourceLoader.getBaseInfoByDefault("rehashNum", 6);
        // 初始化Managers配置（解析逗号分隔的类名）
        String managerClassNames = sourceLoader.getBaseInfoByDefault("managers", "");
        MANAGERS = Arrays.stream(managerClassNames.split(","))
                         .map(String::trim)
                         .toList();
    }

    public static final List<String> MANAGERS;

    public static final Logger log;
    public static final Integer PORT;
    public static final Integer BACKLOG;
    public static final Integer WORK_THREADS;
    public static final Integer GC_THREADS;
    //哨兵模式
    public static final Boolean SENTINEL_ACTIVATE;
    //假线程数量
    public static final Boolean AUTO_SCALING_THREAD;
    //初始化hashtable的链表长度
    public static final int INITIAL_CAPACITY;
    //设置hashtable的负载因子最大
    public static final float MAX_LOAD_FACTOR;
    //设置hashtable的负载因子最小
    public static final float MIN_LOAD_FACTOR;

    //分流器线程大小
    public static final int SHUNT_THREADS;

    //5个key就分裂
    public static final int MAX_OVER_LOAD;
    public static final int MIN_OVER_LOAD;
    //一次rehash的次数
    public static final int REHASH_NUM;


    // RDB相关配置
    public static class RDB
    {

        static
        {
            // 初始化RDB配置（对应rdb.*键）
            MutPropertiesSourceLoader sourceLoader = MutPropertiesSourceLoader.getInstance();
            INTERVAL_TIME = sourceLoader.getRDBInfoByDefault("intervalTime", 2000);
            INSERT_COUNT = sourceLoader.getRDBInfoByDefault("insertCount", 10)-1;
            BUSINESS_TYPE = sourceLoader.getRDBInfoByDefault("businessType", "HIGH");
            MAX_DELAY_COUNT = sourceLoader.getRDBInfoByDefault("maxDelayCount", 5);
            FILE_NAME = sourceLoader.getRDBInfoByDefault("fileName",
                    "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\dump.txt");
        }

        // 执行RDB间隔单位时间 单位毫秒
        public final static long INTERVAL_TIME;

        // 单位时间内插入数据的次数
        public final static long INSERT_COUNT;

        // 业务类型 - LOW; HIGH
        public final static String BUSINESS_TYPE;

        // 最大延迟次数
        public static final int MAX_DELAY_COUNT;

        // 指示 RDB文件名称 TODO - 暂时使用绝对路径
        public static final String FILE_NAME;

    }

    public static class AOF
    {

        static
        {
            MutPropertiesSourceLoader sourceLoader = MutPropertiesSourceLoader.getInstance();
            // 初始化AOF配置（对应aof.*键）
            BUFFER_SIZE = sourceLoader.getAOFInfoByDefault("bufferSize", 1024 * 1024 * 4);
            BUFFER_COUNT = sourceLoader.getAOFInfoByDefault("bufferCount", 2);
            INTERVAL_TIME = sourceLoader.getAOFInfoByDefault("intervalTime", 5000);
            FILE_NAME = sourceLoader.getAOFInfoByDefault("fileName",
                    "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\aof.txt");
            String aofInfoByDefault = sourceLoader.getAOFInfoByDefault("flushPolicy", "EVERY_SEC");
            switch (aofInfoByDefault)
            {
                case "ALWAYS":
                    flushPolicy = 2; break;
                case "NO":
                    flushPolicy = 1; break;
                case "EVERY_SEC":
                default:
                    flushPolicy =3;
            }
        }

        // 每一个独立缓冲区的内存大小
        public final static int BUFFER_SIZE; // 500KB(约为50万字节)

        // 独立缓冲区的个数
        public final static int BUFFER_COUNT;

        // AOF执行的间隔时间
        public final static long INTERVAL_TIME;
        public final static int flushPolicy;
        // AOF文件路径；暂时写死
        public final static String FILE_NAME;

    }

    public static class GC
    {
        static
        {
            MutPropertiesSourceLoader sourceLoader = MutPropertiesSourceLoader.getInstance();
            // 初始化GC配置（对应gc.*键）
            DEFAULT_SLOTS = sourceLoader.getGCInfoByDefault("defaultSlots", 1024);
            TICK_DURATION = sourceLoader.getGCInfoByDefault("tickDuration", 10);
            UNIT_UPDATE_TIME = sourceLoader.getGCInfoByDefault("unitUpdateTime", 5);
        }

        //时间片轮数，必须是2的n次方
        public static final int DEFAULT_SLOTS;
        //一个插槽的时间
        public static final int TICK_DURATION;

        public static final int UNIT_UPDATE_TIME;
    }
}
