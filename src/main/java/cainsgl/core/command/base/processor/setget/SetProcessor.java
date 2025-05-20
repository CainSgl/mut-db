package cainsgl.core.command.base.processor.setget;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;

import java.util.List;

public class SetProcessor extends CommandProcessor<SetGetManager>
{
    public SetProcessor()
    {
        super(2, 2, "set", List.of("string", "string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SetGetManager manager)
    {
        manager.map.put(new ByteSuperKey(args[0]), new ByteValue(args[1]));
        return RESP2Response.OK;
    }

}
