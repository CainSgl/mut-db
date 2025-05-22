package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class SetNxProcessor extends CommandProcessor<StringManager>
{
    public SetNxProcessor()
    {
        super(2, 2, "setnx", List.of("string"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        ByteSuperKey byteSuperKey = new ByteSuperKey(args[0]);
        TTL2Obj ttlObj = manager.map.get(byteSuperKey);
        if (ttlObj == null)
        {
            manager.map.put(byteSuperKey, new TTL2Obj(new ByteValue(args[1])));
            return  NumberResponse.valueOf(1);
        }
        if (ttlObj.getExpireTime()<0)
        {
            ttlObj.clear();
            manager.map.put(byteSuperKey,new TTL2Obj(new ByteValue(args[1])));
            return  NumberResponse.valueOf(1);
        }
        return  NumberResponse.valueOf(0);
    }
}
