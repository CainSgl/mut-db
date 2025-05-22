package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.string.*;
import cainsgl.core.command.base.processor.simple.StrLenProcessor;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;

import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;

import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.storge.map.base.ConveterMapSerializer;

import java.util.*;

public class StringManager extends ShuntCommandManager<Map<ByteSuperKey, TTL2Obj>> implements MutSerializable
{
    public StringManager()
    {
        super(new SetProcessor(), new GetProcessor(), new SetNxProcessor(),new TTLProcessor(),new StrLenProcessor(),new ExpireProcessor(),new DelProcessor());
    }

    public StringManager(List<Map<ByteSuperKey, TTL2Obj>> datas)
    {
        this();
        for (Map<ByteSuperKey, TTL2Obj> data : datas)
        {
            map.putAll(data);
        }

        for (Map.Entry<ByteSuperKey, TTL2Obj> entry : map.entrySet())
        {
            entry.getValue().recover(this, (t) -> {
                map.remove(entry.getKey());
            });
        }
    }

    public Map<ByteSuperKey, TTL2Obj> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, TTL2Obj> separateImpl()
    {
        MutConfiguration.log.info("开始分裂");
        Map<ByteSuperKey, TTL2Obj> result = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, TTL2Obj>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, TTL2Obj> entry = iterator.next();
            ByteSuperKey key = entry.getKey();
            if (!testKey(key.getBytes()))
            {
                //不是我的移除去
                MutConfiguration.log.info("移除key,{}", key);
                TTL2Obj value = entry.getValue();
                value.await();
                result.put(key,value );
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public final void createImpl(List<Map<ByteSuperKey, TTL2Obj>> datas)
    {
        MutConfiguration.log.info("创建新manager");
        new StringManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, TTL2Obj> data)
    {
        MutConfiguration.log.info("开始合并");
        for (Map.Entry<ByteSuperKey, TTL2Obj> entry : map.entrySet())
        {
            ByteSuperKey key = entry.getKey();
            if (testKey(key.getBytes()))
            {
                //是我的key，添加上
                MutConfiguration.log.info("添加key,{}", key);
                TTL2Obj value = entry.getValue();
                value.recover(this,(t)->{
                    map.remove(key);
                });
                map.put(key,value );
            }
        }

    }

    @Override
    public Map<ByteSuperKey, TTL2Obj> destoryImpl()
    {
        return map;
    }

    @Override
    public List<ElementResponse> scanData()
    {
        List<ElementResponse> result = new ArrayList<>();
        for (Map.Entry<ByteSuperKey, TTL2Obj> entry : map.entrySet())
        {
            ByteSuperKey key = entry.getKey();
            result.add(new BulkStringResponse(key.getBytes()));
        }
        return result;
    }

    @Override
    public byte[] serialization()
    {
        ConveterMapSerializer<ByteSuperKey, TTL2Obj> serializer = new ConveterMapSerializer<>(ByteSuperKey.class, TTL2Obj.class);
        return serializer.serialize(map);
    }

    @Override
    public void deSerializer(byte[] data)
    {
        ConveterMapSerializer<ByteSuperKey, TTL2Obj> serializer = new ConveterMapSerializer<>(ByteSuperKey.class, TTL2Obj.class);
        Map<ByteSuperKey, TTL2Obj> deserialize = serializer.deserialize(data);

        for (Map.Entry<ByteSuperKey, TTL2Obj> next : deserialize.entrySet())
        {
            ByteSuperKey key = next.getKey();
            TTL2Obj value = next.getValue();
            map.put(key, value);
            value.activate(this, (v) -> {
                map.remove(key);
            });
        }
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error(e.getMessage(),e);
    }
}
