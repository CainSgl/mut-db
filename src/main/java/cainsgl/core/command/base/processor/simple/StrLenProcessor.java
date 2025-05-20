package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SetGetManager;
import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class StrLenProcessor extends CommandProcessor<SetGetManager>
{

    public StrLenProcessor()
    {
        super(0, 1, "strlen", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SetGetManager manager)
    {
        TTLObj<ByteValue> t = manager.map.get(new ByteSuperKey(args[0]));
        if(t==null)
        {
            return new NumberResponse(-2);
        }
        ByteValue wrapper = t.getWrapper();
        if(wrapper==null)
        {
            return new NumberResponse(-2);
        }
        return new NumberResponse(wrapper.getBytes().length);
    }
}
