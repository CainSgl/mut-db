package cainsgl.core.persistence.serializer.util;

public class SeriUtil {

    // 将一个int类型的数据转化为字节数组
    public static byte[] intToBytes(int value){
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };
    }

    // 将一个字节数组转化为整数
    public static int bytesToInt(byte[] bytes){
        return bytes[3] & 0xFF |
                (bytes[2] & 0xFF) << 8 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[0] & 0xFF) << 24;
    }

}
