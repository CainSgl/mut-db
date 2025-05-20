package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.ErrorResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScanProcessor extends CommandProcessor<SimpleCommandManager>
{
    public ScanProcessor()
    {
        super(0, 1000, "scan", List.of());
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        try{
            return new ArrayResponse( CommandConfiguration.scanData());
        }catch (Exception e){
            return new ErrorResponse("ServerError", e.getMessage());
        }
    }
}
