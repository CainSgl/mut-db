package cainsgl.core.persistence.serializer.converter;

import cainsgl.core.persistence.serializer.converter.byteConverter.ByteConverter;
import cainsgl.core.persistence.serializer.converter.byteConverter.ByteConverterRegistry;
import cainsgl.core.persistence.serializer.valueObj.ConverterVO;

import java.util.HashMap;
import java.util.Map;

/*
* SuperConverter
* 1. 提供将给定的任意Map类型转化为默认Map类型的方法
* 2. 提供将任意类序列化为byte数组的方法
* */
public class SuperConverter {

    public static <K, V> ConverterVO defaultConverter(Map<K, V> map) {
        // 记录数据类型
        String keyClassName = "";
        String valueClassName = "";

        Map<byte[], byte[]> defaultMap = new HashMap<byte[], byte[]>();

        ByteConverter converter = null;

        for (Map.Entry<K, V> entry : map.entrySet()) {
            // 挨个将entry序列化为字节数组存入Map<byte[], byte[]>
            byte[] keyBytes = converterToBytes(entry.getKey(), converter);
            byte[] valueBytes = converterToBytes(entry.getValue(), converter);
            defaultMap.put(keyBytes, valueBytes);
            if(keyClassName.isEmpty()){
                keyClassName = entry.getKey().getClass().getName();
            }
            if(valueClassName.isEmpty()){
                valueClassName = entry.getValue().getClass().getName();
            }
        }
        return new ConverterVO(defaultMap, keyClassName, valueClassName);
    }

    private static <T> byte[] converterToBytes(T obj, ByteConverter<T> converter) {
        if(null == obj){
            return new byte[0];
        }
        Class<T> clazz = (Class<T>) obj.getClass();
        if(converter == null){
            converter = ByteConverterRegistry.getConverter(clazz);
        }
        return converter.convertToBytes(obj);
    }

}
