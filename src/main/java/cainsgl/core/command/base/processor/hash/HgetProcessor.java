package cainsgl.core.command.base.processor.hash;

import cainsgl.core.command.base.manager.HashManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HgetProcessor extends CommandProcessor<HashManager>
{
    public HgetProcessor()
    {
        super(2, 2, "hget", List.of("key","field"));
    }

    @Override
    public RESP2Response execute(byte[][] args, HashManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        ByteFastKey field = new ByteFastKey(args[1]);
        Map<ByteFastKey, TTL2Obj> map2 = manager.map.get(key);
        if(map2 == null)
        {
            return RESP2Response.NIL;
        }
        TTL2Obj ttlObj = map2.get(field);
        if(ttlObj == null)
        {
            return RESP2Response.NIL;
        }
        ByteValue wrapper = ttlObj.getWrapper();
        if(wrapper == null)
        {
            return RESP2Response.NIL;
        }
        return new BulkStringResponse(wrapper.getBytes());
    }
}
