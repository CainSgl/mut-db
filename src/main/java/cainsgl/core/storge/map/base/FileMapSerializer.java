package cainsgl.core.storge.map.base;

import cainsgl.core.config.MutConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileMapSerializer<K, V> extends ConveterMapSerializer<K, V> {
    private final String targetPath;

    /**
     * 构造器：指定文件路径并初始化转换器
     */
    public FileMapSerializer(Class<K> kClass, Class<V> vClass, String targetPath) {
        super(kClass, vClass);  // 调用父类构造器初始化转换器
        this.targetPath = Objects.requireNonNull(targetPath, "Target path cannot be null");
    }

    @Override
    public byte[] serialize(Map<K, V> map)
    {
        serializeToFile(map);
        return null;
    }
    public void setOtherInfo(byte[] otherInfo)
    {
        this.otherInfo = otherInfo;
    }
    public byte[] getOtherInfo()
    {
        return otherInfo;
    }
    public void hasOtherInfo()
    {
        hasOtherInfo=true;
    }
    private boolean hasOtherInfo=false;
    public byte[] otherInfo;
    public void serializeToFile(Map<K, V> map) {
        Objects.requireNonNull(map, "Map cannot be null");
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(targetPath)))) {
            // 直接写入Map大小（与父类格式保持一致）
            dos.writeInt(map.size());
            // 遍历键值对直接写入文件流
            for (Map.Entry<K, V> entry : map.entrySet()) {
                K key = Objects.requireNonNull(entry.getKey(), "Key cannot be null");
                V value = Objects.requireNonNull(entry.getValue(), "Value cannot be null");

                // 转换键并写入（边转换边写入）
                byte[] keyBytes = super.kConverter.toBytes(key);
                dos.writeInt(keyBytes.length);  // 键字节长度
                dos.write(keyBytes);            // 键字节内容

                // 转换值并写入（边转换边写入）
                byte[] valueBytes = super.vConverter.toBytes(value);
                dos.writeInt(valueBytes.length);  // 值字节长度
                dos.write(valueBytes);            // 值字节内容
            }
            if(otherInfo != null) {
                dos.writeInt(otherInfo.length);
                dos.write(otherInfo);
            }
            //写入额外数据
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize to file: " + targetPath, e);
        }
    }

    @Override
    public Map<K, V> deserialize(byte[] data) {
        return deserializeFromFile();
    }

    /**
     * 从文件反序列化时保持流式读取
     */
    public Map<K, V> deserializeFromFile() {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(targetPath)))) {
            int size = dis.readInt();
            Map<K, V> result = new HashMap<>(size);

            for (int i = 0; i < size; i++) {
                // 读取键（流式处理）
                int keyLen = dis.readInt();
                byte[] keyBytes = new byte[keyLen];
                dis.readFully(keyBytes);
                K key = super.kConverter.fromBytes(keyBytes);

                // 读取值（流式处理）
                int valueLen = dis.readInt();
                byte[] valueBytes = new byte[valueLen];
                dis.readFully(valueBytes);
                V value = super.vConverter.fromBytes(valueBytes);

                result.put(key, value);
            }
            if(!hasOtherInfo)
            {
                return result;
            }
            try{
                int i = dis.readInt();
                this.otherInfo = new byte[i];
                dis.readFully(this.otherInfo);
            }catch (Exception e)
            {
                MutConfiguration.log.error("no other info found",e);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize from file: " + targetPath, e);
        }
    }


}