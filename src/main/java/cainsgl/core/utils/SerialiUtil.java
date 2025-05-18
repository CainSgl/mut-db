package cainsgl.core.utils;

public class SerialiUtil
{
    public static byte[] intTOByte(int num)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (num >> 24 & 0xFF);
        bytes[1] = (byte) (num >> 16 & 0xFF);
        bytes[2] = (byte) (num >> 8 & 0xFF);
        bytes[3] = (byte) (num & 0xFF);
        return bytes;
    }

    public static int writeDataToInt(byte[] bytes, int offset, int num)
    {
        // 参数校验（零拷贝要求目标数组必须有效）
        if (bytes == null) {
            throw new IllegalArgumentException("字节数组不能为null");
        }
        if (offset < 0 || offset + 3 >= bytes.length) {
            throw new ArrayIndexOutOfBoundsException("偏移量超出数组范围，有效范围: 0 <= offset <= " + (bytes.length - 4));
        }
        // 大端序（高位在前）写入4字节
        bytes[offset]     = (byte) ((num >> 24) & 0xFF);  // 最高8位
        bytes[offset + 1] = (byte) ((num >> 16) & 0xFF);  // 次高8位
        bytes[offset + 2] = (byte) ((num >> 8) & 0xFF);   // 次低8位
        bytes[offset + 3] = (byte) (num & 0xFF);          // 最低8位
        return offset + 4;  // 返回新的偏移量，用于链式写入
    }

    public static int readIntFromBytes(byte[] data, int offset)
    {
        // 参数校验
        if (data == null) {
            throw new IllegalArgumentException("字节数组不能为null");
        }
        if (offset < 0 || offset + 3 >= data.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "偏移量超出数组范围，有效范围: 0 <= offset <= " + (data.length - 4)
            );
        }
        // 大端序（高位在前）读取4字节并组合为int
        return ((data[offset]     & 0xFF) << 24) |  // 最高8位（左移24位）
                ((data[offset + 1] & 0xFF) << 16) |  // 次高8位（左移16位）
                ((data[offset + 2] & 0xFF) << 8)  |  // 次低8位（左移8位）
                (data[offset + 3] & 0xFF);           // 最低8位（直接相加）
    }
}
