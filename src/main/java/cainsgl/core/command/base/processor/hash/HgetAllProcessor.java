package cainsgl.core.command.base.processor.hash;

import cainsgl.core.command.base.manager.HashManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HgetAllProcessor extends CommandProcessor<HashManager>
{
    public HgetAllProcessor()
    {
        super(1, 1, "hgetall", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, HashManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        Map<ByteFastKey, TTL2Obj> map2 = manager.map.get(key);
        Iterator<Map.Entry<ByteFastKey,TTL2Obj>> iterator = map2.entrySet().iterator();
        List<ElementResponse> list = new ArrayList<>();
        while (iterator.hasNext())
        {
            Map.Entry<ByteFastKey, TTL2Obj> entry = iterator.next();
            list.add(new BulkStringResponse(entry.getKey().getBytes()));
            ByteValue wrapper = entry.getValue().getWrapper();
            if (wrapper != null)
            {
                list.add(new BulkStringResponse(wrapper.getBytes()));
            } else
            {
                list.add(RESP2Response.NIL);
            }
        }
        return new ArrayResponse(list);
    }
}
