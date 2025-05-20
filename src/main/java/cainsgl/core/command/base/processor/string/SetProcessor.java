package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.RespUtils;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class SetProcessor extends CommandProcessor<StringManager>
{
    public SetProcessor()
    {
        super(2, 5, "set", List.of("string", "string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        //看有没有nx或ex
        if (args.length == 3)
        {
            if(args[2][0]=='n')
            {
                //说明是执行nx
                TTLObj<ByteValue> n = manager.map.get(key);
                if(n==null||n.getWrapper()==null)
                {
                    //赋值
                    manager.map.put(key, manager.createTTL( new ByteValue(args[1])));
                    return  NumberResponse.valueOf(1);
                }else
                {
                    return  NumberResponse.valueOf(0);
                }
            }else
            {
                //忽略
                manager.map.put(key, manager.createTTL( new ByteValue(args[1])));
                return  NumberResponse.valueOf(0);
            }
        }
        if(args.length==4)
        {
            if (args[2][0]=='e')
            {
                //说明是有周期
                long l = RespUtils.readAsciiToLong(args[3], 0);
                manager.map.put(key, manager.createTTL(l, new ByteValue(args[1]), (_) -> {
                    manager.map.remove(key);
                }));
                return RESP2Response.OK;
            }
        }
        if(args.length==5)
        {
            //都存在，直接找周期即可
            long l;
            if(args[2][0]=='n')
            {
                 l = RespUtils.readAsciiToLong(args[4], 0);
            }else
            {
                 l = RespUtils.readAsciiToLong(args[3], 0);
            }
            TTLObj<ByteValue> ttlObj = manager.map.get(key);
            if(ttlObj==null||ttlObj.getWrapper()==null)
            {
                manager.map.put(key, manager.createTTL(l, new ByteValue(args[1]), (_) -> {
                    manager.map.remove(key);
                }));
                return  NumberResponse.valueOf(1);
            }else
            {
                return  NumberResponse.valueOf(0);
            }

        }

        manager.map.put(key, manager.createTTL( new ByteValue(args[1])));

        return RESP2Response.OK;
    }

}
