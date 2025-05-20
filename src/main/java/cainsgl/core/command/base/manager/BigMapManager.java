package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.bigmap.BitCountProcessor;
import cainsgl.core.command.base.processor.bigmap.BitPosProcessor;
import cainsgl.core.command.base.processor.bigmap.GetBitProcessor;
import cainsgl.core.command.base.processor.bigmap.SetBitProcessor;
import cainsgl.core.command.manager.ExclusiveThreadManager;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.structure.AutoResizeBigMap;

import java.util.HashMap;
import java.util.Map;

public class BigMapManager extends ExclusiveThreadManager
{
    public BigMapManager()
    {
        super( new BitCountProcessor(),new BitPosProcessor(),new SetBitProcessor(),new GetBitProcessor());
    }
    public Map<ByteSuperKey,AutoResizeBigMap> map=new HashMap<>();
}
