package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.LinkedList;
import java.util.List;

public class RpopProcessor extends CommandProcessor<ListManager>
{

    public RpopProcessor()
    {
        super(1,1, "rpop", List.of("key"));
    }

    @Override
    public RESP2Response execute(byte[][] args, ListManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        TTLObj<LinkedList<ByteValue>> listTTLObj = manager.map.get(key);
        if(listTTLObj == null)
        {

            //往头部加数据
            return  RESP2Response.NIL;
        }
        LinkedList<ByteValue> wrapper = listTTLObj.getWrapper();
        if(wrapper == null||wrapper.isEmpty())
        {
            //过期数据
            return  RESP2Response.NIL;
        }
        ByteValue byteValue = wrapper.removeLast();
        if(byteValue == null)
        {
            return  RESP2Response.NIL;
        }
        return new BulkStringResponse(byteValue.getBytes());
    }

}