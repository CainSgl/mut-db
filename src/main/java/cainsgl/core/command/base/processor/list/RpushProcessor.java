package cainsgl.core.command.base.processor.list;

import cainsgl.core.command.base.manager.ListManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.LinkedList;
import java.util.List;

public class RpushProcessor extends CommandProcessor<ListManager>
{

    public RpushProcessor()
    {
        super(2, Integer.MAX_VALUE, "rpush", List.of("key value [value...]"));
    }

    @Override
    public RESP2Response execute(byte[][] args, ListManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        TTLObj<LinkedList<ByteValue>> listTTLObj = manager.map.get(key);
        if(listTTLObj == null)
        {

            //往头部加数据
            TTLObj<LinkedList<ByteValue>> ttl=manager.createTTL(createListByRight(args));
            manager.map.put(key,ttl);
            return  NumberResponse.valueOf(args.length-1);
        }
        LinkedList<ByteValue> wrapper = listTTLObj.getWrapper();
        if(wrapper == null)
        {
            //过期数据
            TTLObj<LinkedList<ByteValue>> ttl=manager.createTTL(createListByRight(args));
            manager.map.put(key,ttl);
            return  NumberResponse.valueOf(args.length-1);
        }
        addDataByRight(wrapper,args);
        return  NumberResponse.valueOf(args.length-1);


    }
    public LinkedList<ByteValue> addDataByRight( LinkedList<ByteValue> ls ,byte[][] args)
    {
        for (int i=1;i<args.length;i++)
        {
            ls.addLast(new ByteValue(args[i]));
        }
        return ls;
    }

    public LinkedList<ByteValue> createListByRight(byte[][] args)
    {
        LinkedList<ByteValue> ls = new LinkedList<>();
        for (int i=1;i<args.length;i++)
        {
            ls.addLast(new ByteValue(args[i]));
        }
        return ls;
    }
}
