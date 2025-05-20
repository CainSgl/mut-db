package cainsgl.core.command.base.processor.setget;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.system.GcSystem;

import java.util.List;

public class TTLProcessor extends CommandProcessor<SetGetManager>
{

    public TTLProcessor()
    {
        super(1, 1, "ttl", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SetGetManager manager)
    {
        TTLObj<ByteValue> ttlObj = manager.map.get(new ByteSuperKey(args[0]));
        if(ttlObj!=null)
        {
            return new NumberResponse(ttlObj.getExpireTime());
        }
        return new NumberResponse(-2);
    }
}
