package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.bigmap.BitCountProcessor;
import cainsgl.core.command.base.processor.bigmap.BitPosProcessor;
import cainsgl.core.command.base.processor.bigmap.GetBitProcessor;
import cainsgl.core.command.base.processor.bigmap.SetBitProcessor;
import cainsgl.core.command.manager.ExclusiveThreadManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.storge.map.base.ConveterMapSerializer;
import cainsgl.core.structure.AutoResizeBigMap;
import cainsgl.core.system.thread.ThreadManager;

import java.util.HashMap;
import java.util.Map;

public class BigMapManager extends ExclusiveThreadManager implements MutSerializable
{
    static {
        new AutoResizeBigMap.Converter();
    }

    public BigMapManager()
    {
        super(new BitCountProcessor(), new BitPosProcessor(), new SetBitProcessor(), new GetBitProcessor());
    }

    public Map<ByteSuperKey, AutoResizeBigMap> map = new HashMap<>();

    @Override
    public byte[] serialization()
    {
        ConveterMapSerializer<ByteSuperKey, AutoResizeBigMap> serializer = new ConveterMapSerializer<>(ByteSuperKey.class, AutoResizeBigMap.class);
        return serializer.serialize(map);
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        super.exceptionCaught(e);
    }

    @Override
    public void deSerializer(byte[] data)
    {
        try{
            ConveterMapSerializer<ByteSuperKey, AutoResizeBigMap> serializer = new ConveterMapSerializer<>(ByteSuperKey.class, AutoResizeBigMap.class);
            Map<ByteSuperKey, AutoResizeBigMap> deMap = serializer.deserialize(data);
            this.map.putAll(deMap);
        }catch(Exception e){
            MutConfiguration.log.error("BigMap:Failed to deserialize map", e);
        }


    }
}
