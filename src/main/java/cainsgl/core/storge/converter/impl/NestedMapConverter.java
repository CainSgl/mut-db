package cainsgl.core.storge.converter.impl;


import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.storge.map.base.ConveterMapSerializer;

import java.util.Map;

public class NestedMapConverter<K, V> extends Converter<Map<K, V>>
{
    private final Converter<K> keyConverter;
    private final Converter<V> valueConverter;

    public NestedMapConverter(Converter<K> keyConverter, Converter<V> valueConverter)
    {
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    public NestedMapConverter(Class<K> keyConverter, Class<V> valueConverter)
    {
        this.keyConverter = ConverterRegister.getConverter(keyConverter);
        this.valueConverter = ConverterRegister.getConverter(valueConverter);
    }

    @Override
    public byte[] toBytes(Map<K, V> map)
    {
        ConveterMapSerializer<K, V> conveterMapSerializer = new ConveterMapSerializer<>(keyConverter, valueConverter);
        return conveterMapSerializer.serialize(map);
    }

    @Override
    public Map<K, V> fromBytes(byte[] bytes)
    {
        ConveterMapSerializer<K, V> conveterMapSerializer = new ConveterMapSerializer<>(keyConverter, valueConverter);
        return conveterMapSerializer.deserialize(bytes);
    }
}
