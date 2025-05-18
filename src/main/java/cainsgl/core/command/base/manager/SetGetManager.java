package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.setget.GetProcessor;
import cainsgl.core.command.base.processor.setget.SetNxProcessor;
import cainsgl.core.command.base.processor.setget.SetProcessor;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;

import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.value.ByteValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetGetManager extends ShuntCommandManager<Map<ByteSuperKey, ByteValue>>
{

    public SetGetManager()
    {
        super(new SetProcessor(), new GetProcessor(),new SetNxProcessor());
    }


    public SetGetManager(List<Map<ByteSuperKey, ByteValue>> datas)
    {
        this();
        for (Map<ByteSuperKey, ByteValue> data : datas)
        {
            map.putAll(data);
        }
    }

    public Map<ByteSuperKey, ByteValue> map = new HashMap<>();

    @Override
    public Map<ByteSuperKey, ByteValue> separateImpl()
    {
        MutConfiguration.log.info("开始分裂");
        Map<ByteSuperKey, ByteValue> result = new HashMap<>();
        Iterator<Map.Entry<ByteSuperKey, ByteValue>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<ByteSuperKey, ByteValue> entry = iterator.next();
            ByteSuperKey key = entry.getKey();
            if (!testKey(key.getBytes()))
            {
                //不是我的移除去
                MutConfiguration.log.info("移除key,{}",key);
                result.put(key, entry.getValue());
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public final void createImpl(List<Map<ByteSuperKey, ByteValue>> datas)
    {
        MutConfiguration.log.info("合并一个");
        new SetGetManager(datas);
    }

    @Override
    public Integer overLoadImpl()
    {
        return map.size();
    }

    @Override
    public Map<ByteSuperKey, ByteValue> destoryImpl()
    {
        return map;
    }
}
