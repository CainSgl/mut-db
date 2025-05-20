package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.RespUtils;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LrangeProcessor extends CommandProcessor<ListManager>
{

    public LrangeProcessor()
    {
        super(1, 3, "lrange", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, ListManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        TTLObj<LinkedList<ByteValue>> listTTLObj = manager.map.get(key);
        if (listTTLObj == null)
        {
            return RESP2Response.NIL;
        }
        LinkedList<ByteValue> wrapper = listTTLObj.getWrapper();
        if (wrapper == null || wrapper.isEmpty())
        {
            return RESP2Response.NIL;
        }
        if(args.length==1)
        {
            return new ArrayResponse(getListByRange(0,-1,wrapper));
        }

        return new ArrayResponse(getListByRange((int)RespUtils.readAsciiToLong(args[1],0),(int)RespUtils.readAsciiToLong(args[2],0),wrapper));
    }

    private List<ElementResponse> getListByRange(int from, int to, LinkedList<ByteValue> wrapper)
    {

        if (to < from)
        {
            List<ElementResponse> list = new ArrayList<>(wrapper.size());
            for (ByteValue v : wrapper)
            {
                list.add(new BulkStringResponse(v.getBytes()));
            }
            return list;
        }
        List<ElementResponse> list = new ArrayList<>(to - from);
        ListIterator<ByteValue> it = wrapper.listIterator ();
        for (int i = 0; i < from; i++) {
            it.next ();
        }
        for (int i = from; i < to; i++) {
            list.add(new BulkStringResponse(it.next().getBytes()));
        }
        return list;
    }
}

