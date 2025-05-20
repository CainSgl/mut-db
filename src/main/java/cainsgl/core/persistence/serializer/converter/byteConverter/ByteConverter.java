package cainsgl.core.persistence.serializer.converter.byteConverter;

/*
* 字节转化器接口
* 1. 将指定数据转化为字节数组
* */
public interface ByteConverter<T> {

    /* 将实例对象序列化为byte数组 */
    byte[] convertToBytes(T value);

    /* 通过byte数组构建实例对象 */
    T deConverter(byte[] bytes);

}
