package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.setget.GetProcessor;
import cainsgl.core.command.base.processor.setget.SetNxProcessor;
import cainsgl.core.command.base.processor.setget.SetProcessor;
import cainsgl.core.command.base.processor.setget.TTLProcessor;
import cainsgl.core.command.base.processor.simple.StrLenProcessor;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;

import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTLObj;

import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.*;

public class SetGetManager extends ShuntCommandManager<Map<ByteSuperKey, TTLObj<ByteValue>>>
{
    public SetGetManager()
    {
        super(new SetProcessor(), new GetProcessor(), new SetNxProcessor(),new TTLProcessor(),new StrLenProcessor());
    }

    public SetGetManager(List<Map<ByteSuperKey, TTLObj<ByteValue>>> datas)
    {
        this();
        for (Map<ByteSuperKey, TTLObj<ByteValue>> data : datas)
        {
            map.putAll(data);
        }
    }

    public Map<ByteSuperKey, TTLObj<ByteValue>> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, TTLObj<ByteValue>> separateImpl()
    {
        MutConfiguration.log.info("开始分裂");
        Map<ByteSuperKey, TTLObj<ByteValue>> result = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, TTLObj<ByteValue>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, TTLObj<ByteValue>> entry = iterator.next();
            ByteSuperKey key = entry.getKey();
            if (!testKey(key.getBytes()))
            {
                //不是我的移除去
                MutConfiguration.log.info("移除key,{}", key);
                result.put(key, entry.getValue());
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public final void createImpl(List<Map<ByteSuperKey, TTLObj<ByteValue>>> datas)
    {
        MutConfiguration.log.info("创建新manager");
        new SetGetManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, TTLObj<ByteValue>> data)
    {
        MutConfiguration.log.info("开始合并");
        for (Map.Entry<ByteSuperKey, TTLObj<ByteValue>> entry : map.entrySet())
        {
            ByteSuperKey key = entry.getKey();
            if (testKey(key.getBytes()))
            {
                //是我的key，添加上
                MutConfiguration.log.info("添加key,{}", key);
                map.put(key, entry.getValue());
            }
        }

    }

    @Override
    public Map<ByteSuperKey, TTLObj<ByteValue>> destoryImpl()
    {
        return map;
    }

    @Override
    public List<ElementResponse> scanData()
    {
        List<ElementResponse> result = new ArrayList<>();
        for (Map.Entry<ByteSuperKey, TTLObj<ByteValue>> entry : map.entrySet())
        {
            ByteSuperKey key = entry.getKey();
            result.add(new BulkStringResponse(key.getBytes()));
        }
        return result;
    }

    @Override
    public byte[] serialization()
    {
        return null;
    }

    @Override
    public void deSerializer(byte[] data)
    {

    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error(e.getMessage(),e);
    }
}
