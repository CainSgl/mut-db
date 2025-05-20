package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.List;

public class HelloWorldProcessor extends CommandProcessor<SimpleCommandManager>
{
    public HelloWorldProcessor()
    {
        super(0, 0, "hi", List.of());
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        return new BulkStringResponse("hello world");
    }
}
