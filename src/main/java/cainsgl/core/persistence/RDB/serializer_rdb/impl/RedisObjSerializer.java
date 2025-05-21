package cainsgl.core.persistence.RDB.serializer_rdb.impl;

import cainsgl.core.persistence.RDB.serializer_rdb.BasicValueHandler;
import cainsgl.core.persistence.RDB.serializer_rdb.valueObj.DeserializeVO;
import cainsgl.core.persistence.test.mainMemory.RedisObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RedisObjSerializer {

    private static final Logger log = LoggerFactory.getLogger(RedisObjSerializer.class);

    // 类型标记常量
    private static final byte TYPE_STRING = 0;
    private static final byte TYPE_INTEGER = 1;
    private static final byte TYPE_LIST = 2;

    // 基本类型处理器（现仅支持 String、Integer、List）
    private static final Map<Byte, BasicValueHandler> BASIC_TYPE_HANDLERS = new HashMap<>();

    /* 注册各个处理器；根据不同的RedisObj，写入value的时候处理逻辑可能不一样
    *  1. 使用策略模式，通过RedisObj中的type值来获取不同的handler
    *  2. 直接使用函数式接口来为不同的handler注入各自的处理逻辑
    *  3. handler是函数式接口的容器，提供了方法的调用；为方法做了统一封装
    * */
    static {
        // String 类型处理器
        BASIC_TYPE_HANDLERS.put(TYPE_STRING, new BasicValueHandler(
                (out, value) -> out.writeUTF((String) value),
                in -> in.readUTF()
        ));

        // Integer 类型处理器
        BASIC_TYPE_HANDLERS.put(TYPE_INTEGER, new BasicValueHandler(
                (out, value) -> out.writeInt((Integer) value),
                DataInputStream::readInt
        ));

        // List 类型处理器（递归处理）
        BASIC_TYPE_HANDLERS.put(TYPE_LIST, new BasicValueHandler(
                (out, value) -> serializeList(out, (List<?>) value),
                RedisObjSerializer::deserializeList
        ));
    }

    // 序列化 RedisObj；先序列化type以及key，再由 serializeValue 序列化 value 值
    public byte[] serialize(RedisObj<?> obj, String key) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream dataStream = new DataOutputStream(byteStream)) {

            // 序列化 key
            dataStream.writeUTF(key);

            // 2. 序列化 Type
            byte type = obj.getType();
            dataStream.write(type);

            // 3. 序列化 Value
            Object value = obj.getValue();
            if (value == null) {
                dataStream.writeBoolean(false); // 标记为 null
            } else {
                dataStream.writeBoolean(true);   // 标记为非 null
                log.info("当前类数据为: {}", value);
                serializeValue(dataStream, type, value);
            }

            return byteStream.toByteArray();
        }
    }

    // 反序列化 RedisObj
    public DeserializeVO deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             DataInputStream dataStream = new DataInputStream(byteStream)) {

            // 1. 反序列化 key
            String key = dataStream.readUTF();

            // 2. 反序列化 Type
            byte type = dataStream.readByte();

            // 3. 反序列化 Value
            Object value = null;
            if (dataStream.readBoolean()) { // 检查是否为非 null
                value = deserializeValue(dataStream, type);
            }

            RedisObj<Object> obj = new RedisObj<>();
            obj.setType(type);
            obj.setValue(value);
            return new DeserializeVO(key, obj);
        }
    }

    // 一般RedisBoj类型的 value 序列化逻辑
    private void serializeValue(DataOutputStream out, byte type, Object value) throws IOException {
        BasicValueHandler handler = BASIC_TYPE_HANDLERS.get(type);
        if (handler != null) {
            handler.serialize(out, value);
        } else {
            throw new IOException("Unsupported type: " + type);
        }
    }

    // 一般redisObj类型的 value 反序列化逻辑
    private Object deserializeValue(DataInputStream in, byte type) throws IOException {
        BasicValueHandler handler = BASIC_TYPE_HANDLERS.get(type);
        if (handler != null) {
            return handler.deserialize(in);
        } else {
            throw new IOException("Unsupported type: " + type);
        }
    }

    // redisObj<List<?>> 的递归序列化
    private static void serializeList(DataOutputStream out, List<?> list) throws IOException {
        out.writeInt(list.size()); // 写入元素数量
        for (Object element : list) {
            // 根据元素类型写入标记和值
            if (element instanceof String) {
                out.writeByte(TYPE_STRING);
                out.writeUTF((String) element);
            }
            else if (element instanceof Integer) {
                out.writeByte(TYPE_INTEGER);
                out.writeInt((Integer) element);
            }
            else if (element instanceof List) {
                out.writeByte(TYPE_LIST);
                // 递归处理嵌套 List
                serializeList(out, (List<?>) element);
            } else {
                throw new IOException("Unsupported type: " + element.getClass() + ". Just Such Type: " +
                        "Type_String, " +
                        "Type_Integer, " +
                        "Type_List.");
            }
        }
    }

    // redisObj<List<?>> 类型的反序列化逻辑
    private static List<Object> deserializeList(DataInputStream in) throws IOException {
        int size = in.readInt();
        List<Object> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            byte typeTag = in.readByte();
            switch (typeTag) {
                case TYPE_STRING:
                    list.add(in.readUTF());
                    break;
                case TYPE_INTEGER:
                    list.add(in.readInt());
                    break;
                case TYPE_LIST:
                    list.add(deserializeList(in)); // 递归反序列化嵌套 List
                    break;
                default:
                    throw new IOException("Unknown type tag: " + typeTag);
            }
        }
        return list;
    }

}
