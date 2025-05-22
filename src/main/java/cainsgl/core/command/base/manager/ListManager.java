package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.hash.HsetProcessor;
import cainsgl.core.command.base.processor.list.*;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.storge.map.base.ConveterMapSerializer;

import java.util.*;

public class ListManager extends ShuntCommandManager<Map<ByteSuperKey, LinkedList<TTL2Obj>>>
{
    public ListManager()
    {
        super(new LpushProcessor(),new RpushProcessor(),new RpopProcessor(),new LpopProcessor(),new LrangeProcessor());
    }

    public ListManager(List<Map<ByteSuperKey, LinkedList<TTL2Obj>>> data)
    {
        this();
        for (Map<ByteSuperKey, LinkedList<TTL2Obj>> m : data)
        {
            map.putAll(m);
        }
    }

    public Map<ByteSuperKey, LinkedList<TTL2Obj>> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, LinkedList<TTL2Obj>> separateImpl()
    {
        Map<ByteSuperKey, LinkedList<TTL2Obj>> result = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, LinkedList<TTL2Obj>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, LinkedList<TTL2Obj>> next = iterator.next();
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
    public List<ElementResponse> scanData()
    {
        List<ElementResponse> result = new ArrayList<>();
        for (Map.Entry<ByteSuperKey, LinkedList<TTL2Obj>> entry : map.entrySet())
        {
            result.add(new BulkStringResponse(entry.getKey().getBytes()));
        }
        return result;
    }

    @Override
    public void createImpl(List<Map<ByteSuperKey, LinkedList<TTL2Obj>>> datas)
    {
        new ListManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, LinkedList<TTL2Obj>> data)
    {
        map.putAll(data);
    }

    @Override
    public Map<ByteSuperKey, LinkedList<TTL2Obj>> destoryImpl()
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
        // TTLObj<LinkedList<ByteValue>>
       return new byte[]{1};
    }

    @Override
    public void deSerializer(byte[] data)
    {

    }
}
