package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.hash.HsetProcessor;
import cainsgl.core.command.base.processor.list.*;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;

import java.util.*;

public class ListManager extends ShuntCommandManager<Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>>>
{
    public ListManager()
    {
        super(new LpushProcessor(),new RpushProcessor(),new RpopProcessor(),new LpopProcessor(),new LrangeProcessor());
    }

    public ListManager(List<Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>>> data)
    {
        this();
        for (Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> m : data)
        {
            map.putAll(m);
        }
    }

    public Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> separateImpl()
    {
        Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> result = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, TTLObj<LinkedList<ByteValue>>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> next = iterator.next();
            ByteSuperKey key = next.getKey();
            if (!testKey(key.getBytes()))
            {
                //不是我的移除去
                MutConfiguration.log.info("移除key,{}", key);
                result.put(key, next.getValue());
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public void createImpl(List<Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>>> datas)
    {
        new ListManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> data)
    {
        map.putAll(data);
    }

    @Override
    public Map<ByteSuperKey, TTLObj<LinkedList<ByteValue>>> destoryImpl()
    {
        return map;
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error("错误", e);
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
