package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ErrorResponse;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.storge.aof.AofFileExecutor;

import java.util.List;

public class ZipProcessor extends CommandProcessor<SimpleCommandManager>
{

    public ZipProcessor()
    {
        super(0, 1, "zip", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        try{
            new AofFileExecutor().compressAofFiles();
            return RESP2Response.OK;
        }catch (Exception e){
            return new ErrorResponse(e.getClass().getName(), e.getMessage());
        }

    }
}


