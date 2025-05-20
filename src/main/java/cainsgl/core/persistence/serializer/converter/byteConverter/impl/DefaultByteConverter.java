package cainsgl.core.persistence.serializer.converter.byteConverter.impl;

import cainsgl.core.persistence.serializer.converter.byteConverter.ByteConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/*
* 默认序列化器 - 使用Java原生序列化机制
* */
public class DefaultByteConverter<T> implements ByteConverter<T> {
    private static final Logger log = LoggerFactory.getLogger(DefaultByteConverter.class);

    @Override
    public byte[] convertToBytes(T value) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(value);
            return bos.toByteArray();
        }catch (IOException e){
            log.error("IOException while converting object to byte array", e);
        }
        return null;
    }

    @Override
    public T deConverter(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis)){
            return (T) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            log.error("IOException while converting object to byte array", e);
        }
        return null;
    }
}
