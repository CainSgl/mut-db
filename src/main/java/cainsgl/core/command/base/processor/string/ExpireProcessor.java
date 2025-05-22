package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.utils.RespUtils;

import java.util.List;

public class ExpireProcessor extends CommandProcessor<StringManager>
{
    public ExpireProcessor()
    {
        super(2, 2, "expire", List.of("string"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        TTL2Obj ttlObj = manager.map.get(key);
        if (ttlObj == null)
        {
            return NumberResponse.valueOf(0);
        }
        if(ttlObj.getExpire()<0)
        {
            //重新创建即可
            TTL2Obj ttl = manager.createTTL(1000*RespUtils.readAsciiToLong(args[1], 0), ttlObj.getWrapper(), (t) -> {
                manager.map.remove(key);
            });
            manager.map.put(key,ttl);
            return NumberResponse.valueOf(1);
        }
        //说明这是存活的对象，为了避免被延迟删除，我们直接调用api
        ttlObj.setExpireTime(1000*RespUtils.readAsciiToLong(args[1], 0));
        return NumberResponse.valueOf(1);
    }
}


