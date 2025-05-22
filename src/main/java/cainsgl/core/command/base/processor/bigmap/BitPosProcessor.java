package cainsgl.core.command.base.processor.bigmap;

import cainsgl.core.command.base.manager.BigMapManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.structure.AutoResizeBigMap;

import java.util.List;

public class BitPosProcessor extends CommandProcessor<BigMapManager>
{
    public BitPosProcessor()
    {
        super(1, 1, "bigpos", List.of("key index"),true);
    }

    @Override
    public RESP2Response execute(byte[][] args, BigMapManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        AutoResizeBigMap bigMap = manager.map.get(key);
        if (bigMap == null)
        {
            return RESP2Response.NONE;
        }
        return NumberResponse.valueOf(bigMap.findMinId());
    }
}


