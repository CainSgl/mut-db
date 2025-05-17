package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.List;

public class PingProcessor extends CommandProcessor<SimpleCommandManager>
{

    public PingProcessor()
    {
        super(0, 1, "ping", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        if (args.length == 0)
        {
            return new BulkStringResponse("pong");
        } else
        {
            return new BulkStringResponse(args[0]);
        }
    }
}
