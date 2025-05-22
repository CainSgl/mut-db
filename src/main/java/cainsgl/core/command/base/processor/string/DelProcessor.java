package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;

import java.util.List;

public class DelProcessor extends CommandProcessor<StringManager>
{
    public DelProcessor()
    {
        super(1, 1, "del", List.of("string"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {


        TTL2Obj ttlObj = manager.map.get(new ByteSuperKey(args[0]));
        if (ttlObj == null)
        {
            return NumberResponse.valueOf(1);
        }
        ttlObj.clear();
        return NumberResponse.valueOf(1);
    }
}


