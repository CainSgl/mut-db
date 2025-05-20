package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.ErrorResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class DbSizeProcessor extends CommandProcessor<SimpleCommandManager>
{

    public DbSizeProcessor()
    {
        super(0, 1, "dbsize", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        try{
            return  NumberResponse.valueOf( CommandConfiguration.dbSize());
        }catch (Exception e){
            return new ErrorResponse("ServerError", e.getMessage());
        }
    }
}
