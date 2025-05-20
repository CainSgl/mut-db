package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.hash.HgetAllProcessor;
import cainsgl.core.command.base.processor.hash.HgetProcessor;
import cainsgl.core.command.base.processor.hash.HsetProcessor;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HashManager extends ShuntCommandManager<Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>>>
{
    public HashManager()
    {
        super(new HsetProcessor(), new HgetProcessor(), new HgetAllProcessor());
    }

    public HashManager(List<Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>>> data)
    {
        super(new HsetProcessor(), new HgetProcessor(), new HgetAllProcessor());
        for (Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> datum : data)
        {
            map.putAll(datum);
        }
    }

    public Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> separateImpl()
    {

        Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> res = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> next = iterator.next();
            ByteSuperKey key = next.getKey();
            if (!testKey(key.getBytes()))
            {
                //不是我的，移出去
                res.put(key, next.getValue());
                iterator.remove();
            }
        }
        return res;
    }

    @Override
    public void createImpl(List<Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>>> datas)
    {
        new HashManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> data)
    {
        map.putAll(data);
    }

    @Override
    public Map<ByteSuperKey, Map<ByteFastKey, TTLObj<ByteValue>>> destoryImpl()
    {
        return map;
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error("HashManager 出错",e);
    }

    @Override
    public byte[] serialization()
    {
        return new byte[0];
    }

    @Override
    public void deSerializer(byte[] data)
    {

    }
}
