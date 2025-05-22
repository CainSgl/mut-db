package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.hash.HgetAllProcessor;
import cainsgl.core.command.base.processor.hash.HgetProcessor;
import cainsgl.core.command.base.processor.hash.HsetProcessor;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.storge.converter.impl.NestedMapConverter;
import cainsgl.core.storge.map.base.ConveterMapSerializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HashManager extends ShuntCommandManager<Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>>>
{

    static
    {
        //去注册转换器
        //  new NestedMapConverter<>(ByteFastKey.class, ByteValue.class);
    }


    public HashManager()
    {
        super(new HsetProcessor(), new HgetProcessor(), new HgetAllProcessor());
    }

    public HashManager(List<Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>>> data)
    {
        super(new HsetProcessor(), new HgetProcessor(), new HgetAllProcessor());
        for (Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> datum : data)
        {
            map.putAll(datum);
        }
    }

    public Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> separateImpl()
    {

        Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> res = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, Map<ByteFastKey, TTL2Obj>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> next = iterator.next();
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
    public void createImpl(List<Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>>> datas)
    {
        new HashManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public void addData(Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> data)
    {
        map.putAll(data);
    }

    @Override
    public Map<ByteSuperKey, Map<ByteFastKey, TTL2Obj>> destoryImpl()
    {
        return map;
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error("HashManager 出错", e);
    }

    @Override
    public byte[] serialization()
    {
        //转成字节，后面反序列化的时候同样
//        ConveterMapSerializer<ByteSuperKey, Object> serializer = new ConveterMapSerializer<>(ConverterRegister.getConverter(ByteSuperKey.class), new Converter<>()
//        {
//
//            private final ConveterMapSerializer<ByteFastKey, TTLObj> serializer = new ConveterMapSerializer<>(ByteFastKey.class, TTLObj.class);
//
//            @Override
//            public byte[] toBytes(Object obj)
//            {
//                Map<ByteFastKey, TTLObj> map1 = (Map<ByteFastKey, TTLObj>) obj;
//                return serializer.serialize(map1);
//            }
//
//            @Override
//            public Object fromBytes(byte[] bytes)
//            {
//                return null;
//            }
//        });
//        return serializer.serialize((Map) map);
        return new byte[]{1};
    }

    @Override
    public void deSerializer(byte[] data)
    {
//        ConveterMapSerializer<ByteSuperKey, Object> serializer = new ConveterMapSerializer<>(ConverterRegister.getConverter(ByteSuperKey.class), new Converter<>()
//        {
//            private final ConveterMapSerializer<ByteFastKey, TTLObj> serializer = new ConveterMapSerializer<>(ByteFastKey.class, TTLObj.class);
//
//            @Override
//            public byte[] toBytes(Object obj)
//            {
//                return null;
//            }
//
//            @Override
//            public Object fromBytes(byte[] bytes)
//            {
//               return serializer.deserialize(bytes);
//            }
//        });
//        Map<ByteSuperKey, Map<ByteFastKey, TTLObj>> deserialize = (Map)serializer.deserialize(data);
//        //成功转换，只不过这时候里面的TTLObj仍然有问题，只有过期时间，没有激活
//        for (Map.Entry<ByteSuperKey, Map<ByteFastKey, TTLObj>> next : deserialize.entrySet())
//        {
//            Map<ByteFastKey, TTLObj> value = next.getValue();
//            for (Map.Entry<ByteFastKey, TTLObj> next1 : value.entrySet())
//            {
//                ByteFastKey key = next1.getKey();
//                TTLObj value1 = next1.getValue();
//                value1.activate(this, (ttlObj -> {
//                    value.remove(key);
//                }));
//            }
//        }
//        this.map.putAll((Map)deserialize);
    }
}
