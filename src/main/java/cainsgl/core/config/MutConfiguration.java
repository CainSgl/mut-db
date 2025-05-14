package cainsgl.core.config;

import cainsgl.core.network.server.MutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutConfiguration
{
    public static final Logger log = LoggerFactory.getLogger(MutServer.class);
    public static final Integer port=6379;
    public static final Integer backlog=128;
    public static final Integer workThreads=2;
    public static final Integer gcThreads=1;
    //哨兵模式
    public static final Boolean SentinelActivate=true;
    //假线程数量
    public static final Boolean autoScalingThread=true;
    //初始化hashtable的链表长度
    public static final int initial_capacity=2;
    //设置hashtable的负载因子最大
    public static final float MAX_Load_Factor=1.6f;
    //设置hashtable的负载因子最小
    public static final float MIN_Load_Factor=0.5f;
    //rehash的次数
    public static final int rehashNum=6;
}
