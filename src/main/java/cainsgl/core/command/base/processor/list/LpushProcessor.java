package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.HashManager;
import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LpushProcessor extends CommandProcessor<ListManager>
{

    public LpushProcessor()
    {
        super(2, Integer.MAX_VALUE, "lpush", List.of("key"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, ListManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        LinkedList<TTL2Obj> obj = manager.map.computeIfAbsent(key, (key1) -> new LinkedList<>());
        addDataByLeft(obj, args);
        return NumberResponse.valueOf(args.length - 1);
    }

    public LinkedList<TTL2Obj> addDataByLeft(LinkedList<TTL2Obj> ls, byte[][] args)
    {
        for (int i = 1; i < args.length; i++)
        {
            ls.addFirst(new TTL2Obj(new ByteValue(args[i])));
        }
        return ls;
    }

    public LinkedList<ByteValue> createListByLeft(byte[][] args)
    {
        LinkedList<ByteValue> ls = new LinkedList<>();
        for (int i = 1; i < args.length; i++)
        {
            ls.addFirst(new ByteValue(args[i]));
        }
        return ls;
    }
}

