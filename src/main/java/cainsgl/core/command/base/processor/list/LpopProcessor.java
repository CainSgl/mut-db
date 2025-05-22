package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.LinkedList;
import java.util.List;

public class LpopProcessor extends CommandProcessor<ListManager>
{

    public LpopProcessor()
    {
        super(1,1, "lpop", List.of("key value [value...]"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, ListManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        LinkedList<TTL2Obj> list = manager.map.get(key);
        if(list == null)
        {
            return  RESP2Response.NIL;
        }
        while(true)
        {
            if(list.isEmpty())
            {
                manager.map.remove(key);

                return RESP2Response.NIL;
            }
            TTL2Obj ttl2Obj = list.removeFirst();
            ByteValue wrapper = ttl2Obj.getWrapper();
            if(wrapper != null)
            {
                return new BulkStringResponse(wrapper.getBytes());
            }
        }
    }

}


