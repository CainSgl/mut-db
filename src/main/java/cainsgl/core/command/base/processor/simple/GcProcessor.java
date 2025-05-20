package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;

import java.util.List;

public class GcProcessor  extends CommandProcessor<SimpleCommandManager>
{
    public GcProcessor()
    {
        super(0, 0, "gc", List.of());
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        System.gc();
        return RESP2Response.OK;
    }
}
