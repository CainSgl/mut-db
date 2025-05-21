package cainsgl.core.command.base.processor.bigmap;

import cainsgl.core.command.base.manager.BigMapManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.utils.RespUtils;
import cainsgl.core.network.response.impl.ErrorResponse;
import cainsgl.core.structure.AutoResizeBigMap;

import java.util.List;

public class SetBitProcessor extends CommandProcessor<BigMapManager>
{
    public SetBitProcessor()
    {
        super(3, 3, "setbit", List.of("key index value"));
    }

    @Override
    public RESP2Response execute(byte[][] args, BigMapManager manager)
    {
        ByteSuperKey key = new ByteSuperKey(args[0]);
        int i = (int) RespUtils.readAsciiToLong(args[1], 0);
        if(i<0)
        {
            return new ErrorResponse("ERR","index <0");
        }
        AutoResizeBigMap bigMap = manager.map.computeIfAbsent(key,(k) -> new AutoResizeBigMap(Math.max(i,3000), 48*4));
        if(args[2][0]=='1')
        {
            bigMap.put(i);
        }else{
            bigMap.remove(i);
        }
        return RESP2Response.OK;
    }
}
