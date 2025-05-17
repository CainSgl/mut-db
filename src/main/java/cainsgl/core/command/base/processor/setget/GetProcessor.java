package cainsgl.core.command.base.processor.setget;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.List;

public class GetProcessor extends CommandProcessor<SetGetManager>
{
    public GetProcessor()
    {
        super(1, 1, "get", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SetGetManager manager)
    {
        ByteValue byteValue = manager.map.get(new ByteSuperKey(args[0]));
        if (byteValue == null)
        {
            return RESP2Response.NIL;
        }
        return new BulkStringResponse(byteValue.getBytes());
    }
}
