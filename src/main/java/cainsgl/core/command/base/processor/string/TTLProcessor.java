package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class TTLProcessor extends CommandProcessor<StringManager>
{

    public TTLProcessor()
    {
        super(1, 1, "ttl", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        TTLObj<ByteValue> ttlObj = manager.map.get(new ByteSuperKey(args[0]));
        if(ttlObj!=null)
        {
            return  NumberResponse.valueOf(ttlObj.getExpireTime());
        }
        return  NumberResponse.valueOf(-2);
    }
}
