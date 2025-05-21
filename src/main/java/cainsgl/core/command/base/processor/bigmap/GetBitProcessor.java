package cainsgl.core.command.base.processor.bigmap;

import cainsgl.core.command.base.manager.BigMapManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.utils.RespUtils;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.structure.AutoResizeBigMap;

import java.util.List;

public class GetBitProcessor extends CommandProcessor<BigMapManager>
{
    public GetBitProcessor()
    {
        super(2, 2, "getbit", List.of("key index"));
    }

    @Override
    public RESP2Response execute(byte[][] args, BigMapManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        int i = (int) RespUtils.readAsciiToLong(args[1], 0);
        AutoResizeBigMap bigMap = manager.map.get(key);
        if(bigMap == null)
        {
            return NumberResponse.valueOf(0);
        }
       if( bigMap.containsKey(i)){
           return  NumberResponse.valueOf(1);
       }else
       {
           return  NumberResponse.valueOf(0);
       }
    }
}

