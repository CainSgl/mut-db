package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.utils.RespUtils;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;

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
        LinkedList<TTL2Obj> list = manager.map.get(key);
        if(list == null)
        {
            return RESP2Response.NIL;
        }
        if(list.isEmpty())
        {
            manager.map.remove(key);
            return RESP2Response.NIL;
        }

        if(args.length==1)
        {
            return new ArrayResponse(getListByRange(0,-1,list));
        }

        return new ArrayResponse(getListByRange((int)RespUtils.readAsciiToLong(args[1],0),(int)RespUtils.readAsciiToLong(args[2],0),list));
    }

    private List<ElementResponse> getListByRange(int from, int to, LinkedList<TTL2Obj> wrapper)
    {

        if (to < from)
        {
            List<ElementResponse> list = new ArrayList<>(wrapper.size());
            for (TTL2Obj v : wrapper)
            {
                ByteValue wrapper1 = v.getWrapper();
                if(wrapper1 != null)
                {
                    list.add(new BulkStringResponse(wrapper1.getBytes()));
                }
            }
            return list;
        }
        List<ElementResponse> list = new ArrayList<>(to - from);
        ListIterator<TTL2Obj> it = wrapper.listIterator ();
        for (int i = 0; i < from; i++) {
            if(it.hasNext())
            {
                it.next ();
            }else
            {
                return new LinkedList<>();
            }
        }
        for (int i = from; i < to; i++) {
            if(it.hasNext())
            {
                ByteValue wrapper1 = it.next().getWrapper();
                if(wrapper1 != null)
                {
                    list.add(new BulkStringResponse(wrapper1.getBytes()));
                }
            }else
            {
                return list;
            }
        }
        return list;
    }
}

