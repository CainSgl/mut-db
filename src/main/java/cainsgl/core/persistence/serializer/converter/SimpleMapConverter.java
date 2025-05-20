package cainsgl.core.persistence.serializer.converter;

import cainsgl.core.persistence.serializer.MutSerializable;
import cainsgl.core.persistence.serializer.valueObj.ConverterVO;

import java.util.HashMap;
import java.util.Map;

/*
* 转化器
* 1. 负责将用户提供的Map<MutSerializable, MutSerializable>转化为默认类型 Map<byte[], byte[]>
* */
@Deprecated
public class SimpleMapConverter {

    public static ConverterVO converter(Map<MutSerializable, MutSerializable> map){
        return doConverterDefault(map);
    }

    // 将用户实现的接口类型转化为默认类型
    private static ConverterVO doConverterDefault(Map<MutSerializable, MutSerializable> map){
        Map<byte[], byte[]> result = new HashMap<>();
        String keyType = "";
        String valueType = "";
        for (Map.Entry<MutSerializable, MutSerializable> entry : map.entrySet()){
            result.put(entry.getKey().serialization(), entry.getValue().serialization());
            if(keyType.isEmpty()){
                keyType = entry.getKey().getClassType();
            }
            if(valueType.isEmpty()){
                valueType = entry.getKey().getClassType();
            }
        }
        return new ConverterVO(result, keyType, valueType);
    }

}
