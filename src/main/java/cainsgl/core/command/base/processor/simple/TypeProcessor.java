package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.List;

public class TypeProcessor extends CommandProcessor<SimpleCommandManager>
{
    public TypeProcessor()
    {
        super(1, 1, "type", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
       return new BulkStringResponse("string");
    }
}
