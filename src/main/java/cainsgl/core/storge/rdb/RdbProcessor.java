package cainsgl.core.storge.rdb;

import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.system.GcSystem;

import java.nio.charset.StandardCharsets;

public class RdbProcessor
{
    private static int frequency=0;
    private static long lastTime= GcSystem.updateTime;
    private static final CommandProcessor<?> RdbProcessor= NetWorkConfig.getCmd("save".getBytes(StandardCharsets.UTF_8));
    private static final byte[][] args=new byte[][]{new byte[]{1} };
    public static void add(){
        frequency++;
        long updateTime = GcSystem.updateTime;
        if(frequency> MutConfiguration.RDB.INSERT_COUNT&&updateTime >lastTime+MutConfiguration.RDB.INTERVAL_TIME)
        {
            frequency=0;
            lastTime= updateTime;
            MutConfiguration.log.info("rdb db");
            //执行rdb
            RdbProcessor.submit(args,res->{
            },null);
        }
    }
}
