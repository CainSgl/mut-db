package cainsgl.core.data.key;

import cainsgl.core.data.AbstractByteObj;

public class ByteFastKey extends AbstractByteObj
{
    public final static int uuid=0;
    byte[] key;
    int hashCode;
    private static final int INIT_SEED = 0x9E37;
    public static int fastHash(byte[] bytes) {
        if (bytes == null) return 0;

        int hash = INIT_SEED;
        int length = bytes.length;
        int i = 0;

        // 处理4字节对齐块（32位批量处理）
        while (i + 4 <= length) {
            // 直接按小端序组合4字节（比原函数少一次位运算）
            int chunk = (bytes[i] & 0xFF)
                    | ((bytes[i+1] & 0xFF) << 8)
                    | ((bytes[i+2] & 0xFF) << 16)
                    | ((bytes[i+3] & 0xFF) << 24);

            // 快速混合：异或+循环移位（比乘法快3倍以上）
            hash ^= chunk;
            hash = (hash << 13) | (hash >>> 19);  // 等效Integer.rotateLeft(hash, 13)
            i += 4;
        }

        // 处理剩余1-3字节（简化switch为顺序操作）
        if (i < length) {
            int remaining = length - i;
            int temp = 0;
            for (int j = 0; j < remaining; j++) {
                temp |= (bytes[i + j] & 0xFF) << (j * 8);
            }
            hash ^= temp;
            hash = (hash << 7) | (hash >>> 25);  // 小范围循环移位
        }

        // 最终混合（仅保留关键扩散步骤）
        hash ^= length;          // 加入长度信息
        hash ^= hash >>> 16;     // 高位低位混合
        hash ^= hash >>> 8;      // 进一步扩散
        return hash ;
    }

    public ByteFastKey(byte[] bytes) {
        this.key = bytes;
        this.hashCode = fastHash(bytes);
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }



    @Override
    public byte[] getBytes()
    {
        return key;
    }
}
