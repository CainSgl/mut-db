package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class StrLenProcessor extends CommandProcessor<StringManager>
{

    public StrLenProcessor()
    {
        super(0, 1, "strlen", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        TTLObj<ByteValue> t = manager.map.get(new ByteSuperKey(args[0]));
        if(t==null)
        {
            return RESP2Response.NONE2;
        }
        ByteValue wrapper = t.getWrapper();
        if(wrapper==null)
        {
            return RESP2Response.NONE2;
        }
        return  NumberResponse.valueOf (wrapper.getBytes().length);
    }
}
