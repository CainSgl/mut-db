package cainsgl.core.command.base.processor.string;

import cainsgl.core.command.base.manager.StringManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.List;

public class GetProcessor extends CommandProcessor<StringManager>
{
    public GetProcessor()
    {
        super(1, 1, "get", List.of("string"));
    }

    @Override
    public RESP2Response execute(byte[][] args, StringManager manager)
    {
        TTLObj<ByteValue> ttlObj = manager.map.get(new ByteSuperKey(args[0]));
        if (ttlObj == null)
        {
            return RESP2Response.NIL;
        }
        ByteValue wrapper = ttlObj.getWrapper();
        if (wrapper == null)
        {
            return RESP2Response.NIL;
        }
        return new BulkStringResponse(wrapper.getBytes());
    }
}
