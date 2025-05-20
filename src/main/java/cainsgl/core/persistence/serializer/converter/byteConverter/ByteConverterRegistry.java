package cainsgl.core.persistence.serializer.converter.byteConverter;

import cainsgl.core.persistence.serializer.converter.byteConverter.impl.DefaultByteConverter;
import cainsgl.core.persistence.serializer.converter.byteConverter.impl.IntegerByteConverter;
import cainsgl.core.persistence.serializer.converter.byteConverter.impl.StringByteConverter;

import java.util.HashMap;
import java.util.Map;

/*
* 字节转化器注册器
*  1. 注册字节转化器到Map中
* */
public class ByteConverterRegistry {

    private static final Map<Class<?>, ByteConverter<?>> BYTE_CONVERTERS  = new HashMap<>();

    // 注册字节转化器
    static {
        // 注册字节序列化器
        init();
    }

    private static void init(){
        register(Integer.class, new IntegerByteConverter());
        register(String.class, new StringByteConverter());
    }

    private static void register(Class<?> clazz, ByteConverter<?> converter) {
        BYTE_CONVERTERS.put(clazz, converter);
    }

    // 根据字节转化器负责的类型获取字节转换器
    public static <T> ByteConverter<T> getConverter(Class<T> clazz){
        return (ByteConverter<T>) BYTE_CONVERTERS.computeIfAbsent(clazz, c -> new DefaultByteConverter());
    }

}
