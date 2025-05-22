package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.storge.aof.AofFileExecutor;
import cainsgl.core.storge.map.base.FileMapSerializer;
import cainsgl.core.system.GcSystem;
import cainsgl.core.utils.RespUtils;

import java.util.List;
import java.util.Map;

public class SaveProcessor extends CommandProcessor<SimpleCommandManager>
{
    public SaveProcessor()
    {
        super(0, 0, "save", List.of());
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        long updateTime= GcSystem.updateTime;
        Map<String, byte[]> data = CommandConfiguration.getData();
        FileMapSerializer<String, byte[]> serializer = new FileMapSerializer<>(String.class, byte[].class, MutConfiguration.RDB.FILE_NAME);
        serializer.setOtherInfo(RespUtils.longToAsciiBytes(updateTime));
        serializer.serialize(data);
        //去把aof文件里的之前的数据统统删除
        CommandConfiguration.rdbTimeStamp=updateTime;
//        AofFileExecutor aofFileExecutor = new AofFileExecutor();
//        try{
//            aofFileExecutor.compressAofFiles();
//        }catch(Exception e){
//            //压缩失败
//            MutConfiguration.log.error("Failed to compress AOF files", e);
//        }
        return RESP2Response.OK;
    }
}
