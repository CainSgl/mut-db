package cainsgl.core.command.base.processor.setget;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class SetNxProcessor extends CommandProcessor<SetGetManager>
{
    public SetNxProcessor()
    {
        super(2, 2, "setnx", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SetGetManager manager)
    {
        ByteSuperKey byteSuperKey = new ByteSuperKey(args[0]);
        ByteValue byteValue = manager.map.get(byteSuperKey);
        if (byteValue == null)
        {
            manager.map.put(byteSuperKey, new ByteValue(args[1]));
            return new NumberResponse(1);
        }else
        {
            return new NumberResponse(0);
        }
    }
}
