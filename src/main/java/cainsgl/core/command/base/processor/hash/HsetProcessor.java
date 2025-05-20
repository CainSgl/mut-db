package cainsgl.core.command.base.processor.hash;

import cainsgl.core.command.base.manager.HashManager;
import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HsetProcessor  extends CommandProcessor<HashManager>
{
    public HsetProcessor()
    {
        super(3, 3, "hset", List.of("key","field","value"));
    }

    @Override
    public RESP2Response execute(byte[][] args, HashManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        ByteFastKey field = new ByteFastKey(args[1]);
        Map<ByteFastKey, TTLObj<ByteValue>> map2 = manager.map.computeIfAbsent(key, k -> new HashMap<>());
        map2.put(field, new TTLObj<>(new ByteValue(args[2])));
        return RESP2Response.OK;
    }
}
