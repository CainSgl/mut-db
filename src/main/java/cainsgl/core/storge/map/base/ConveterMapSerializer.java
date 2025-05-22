package cainsgl.core.storge.map.base;

import cainsgl.core.storge.converter.Converter;
import cainsgl.core.storge.converter.ConverterRegister;
import cainsgl.core.storge.map.Serializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConveterMapSerializer<K, V> implements Serializer<K, V>
{
    Converter<K> kConverter;
    Converter<V> vConverter;

    public ConveterMapSerializer(Class<K> kClass, Class<V> vClass)
    {
        kConverter = ConverterRegister.getConverter(kClass);
        vConverter = ConverterRegister.getConverter(vClass);
        Objects.requireNonNull(kConverter);
        Objects.requireNonNull(vConverter);
    }

    public ConveterMapSerializer(Converter<K> keyConverter, Converter<V> valueConverter) {
        Objects.requireNonNull(keyConverter);
        Objects.requireNonNull(valueConverter);
        this.kConverter=keyConverter;
        this.vConverter=valueConverter;
    }

    @Override
    public byte[] serialize(Map<K, V> map)
    {
        Objects.requireNonNull(map, "Map cannot be null");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             DataOutputStream dataOut = new DataOutputStream(byteOut))
        {
            // 写入Map大小
            dataOut.writeInt(map.size());
            // 遍历写入每个键值对
            for (Map.Entry<K, V> entry : map.entrySet())
            {
                K key = entry.getKey();
                V value = entry.getValue();
                // 转换并写入键
                byte[] keyBytes = kConverter.toBytes(Objects.requireNonNull(key, "Key cannot be null"));
                dataOut.writeInt(keyBytes.length);  // 键字节长度
                dataOut.write(keyBytes);            // 键字节内容
                // 转换并写入值
                byte[] valueBytes = vConverter.toBytes(Objects.requireNonNull(value, "Value cannot be null"));
                dataOut.writeInt(valueBytes.length);  // 值字节长度
                dataOut.write(valueBytes);            // 值字节内容
            }
            return byteOut.toByteArray();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<K, V> deserialize(byte[] data)
    {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             DataInputStream dataIn = new DataInputStream(byteIn))
        {

            // 读取Map大小
            int size = dataIn.readInt();
            Map<K, V> result = new HashMap<>(size);

            // 读取所有键值对
            for (int i = 0; i < size; i++)
            {
                // 读取并转换键
                int keyLen = dataIn.readInt();
                byte[] keyBytes = new byte[keyLen];
                dataIn.readFully(keyBytes);  // 确保完整读取
                K key = kConverter.fromBytes(keyBytes);

                // 读取并转换值
                int valueLen = dataIn.readInt();
                byte[] valueBytes = new byte[valueLen];
                dataIn.readFully(valueBytes);  // 确保完整读取
                V value = vConverter.fromBytes(valueBytes);

                result.put(key, value);
            }

            return result;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


}
