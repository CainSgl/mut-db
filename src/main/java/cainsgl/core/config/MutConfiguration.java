package cainsgl.core.config;

import cainsgl.core.network.server.MutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutConfiguration {

    public static final Logger log = LoggerFactory.getLogger(MutServer.class);
    public static final Integer port = 6379;
    public static final Integer backlog = 128;
    public static final Integer workThreads = 2;
    public static final Integer gcThreads = 1;

    //哨兵模式
    public static final Boolean SentinelActivate = true;
    //假线程数量
    public static final Boolean autoScalingThread = true;
    //初始化hashtable的链表长度
    public static final int initial_capacity = 1;
    //设置hashtable的负载因子最大
    public static final float MAX_Load_Factor = 0.75f;
    //设置hashtable的负载因子最小
    public static final float MIN_Load_Factor = 0.1f;
    //一次rehash的次数
    public static final int rehashNum = 6;

    // RDB相关配置
    public static class RDB{

        // 执行RDB间隔单位时间 单位毫秒
        public static long INTERVAL_TIME = 2000L;

        // 单位时间内插入数据的次数
        public static long INSERT_COUNT = 10L;

        // 业务类型 - LOW; HIGH
        public static String BUSINESS_TYPE = "HIGH";

        // 最大延迟次数
        public static final int MAX_DELAY_COUNT = 5;

        // 指示 RDB文件名称 TODO - 暂时使用绝对路径
        public static final String FILE_NAME = "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\dump.txt";

    }

    public static class AOF{

        // 每一个独立缓冲区的内存大小
        public static int BUFFER_SIZE = 1024 * 512; // 500KB(约为50万字节)

        // 独立缓冲区的个数
        public static int BUFFER_COUNT = 2;

        // AOF执行的间隔时间
        public static long INTERVAL_TIME = 5000L;

        // AOF文件路径；暂时写死
        public static String FILE_NAME = "D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\persistence\\test\\aof.text";

    }
}
