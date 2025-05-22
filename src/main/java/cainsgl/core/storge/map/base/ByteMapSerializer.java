package cainsgl.core.storge.map.base;

import cainsgl.core.storge.map.Serializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ByteMapSerializer
{
    /**
     * 序列化 Map&lt;byte[], byte[]&gt; 为字节数组
     */

    public  byte[] serialize(Map<byte[], byte[]> map) throws IOException
    {
        if (map == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             DataOutputStream dataOut = new DataOutputStream(byteOut)) {
            // 写入Map大小
            dataOut.writeInt(map.size());
            // 遍历写入每个键值对
            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();
                // 检查键值非空
                if (key == null || value == null) {
                    throw new IllegalArgumentException("Key or value cannot be null");
                }
                // 写入键长度 + 键内容
                dataOut.writeInt(key.length);
                dataOut.write(key);
                // 写入值长度 + 值内容
                dataOut.writeInt(value.length);
                dataOut.write(value);
            }

            return byteOut.toByteArray();
        }
    }

    public  Map<byte[], byte[]> deserialize(byte[] data) throws IOException
    {
        if (data == null)
        {
            throw new IllegalArgumentException("Data cannot be null");
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             DataInputStream dataIn = new DataInputStream(byteIn))
        {

            // 读取Map大小
            int size = dataIn.readInt();
            Map<byte[], byte[]> result = new HashMap<>(size);

            // 读取所有键值对
            for (int i = 0; i < size; i++)
            {
                // 读取键
                int keyLen = dataIn.readInt();
                byte[] key = new byte[keyLen];
                dataIn.readFully(key);

                // 读取值
                int valueLen = dataIn.readInt();
                byte[] value = new byte[valueLen];
                dataIn.readFully(value);

                result.put(key, value);
            }

            return result;
        }
    }
}
