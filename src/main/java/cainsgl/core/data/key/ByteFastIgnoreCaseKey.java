package cainsgl.core.data.key;

import cainsgl.core.data.AbstractByteObj;

import java.io.ByteArrayOutputStream;

public class ByteFastIgnoreCaseKey extends AbstractByteObj
{
    public final static int uuid = 0;
    private final byte[] key;  // 改为final保证不可变性
    private final int hashCode;
    private static final int INIT_SEED = 0x9E37;

    public static int fastHash(byte[] bytes)
    {
        if (bytes == null || bytes.length == 0)
        {
            return 0;  // 处理空数组情况
        }

        int hash = INIT_SEED;
        int length = bytes.length;
        int i = 0;

        // 处理4字节对齐块（32位批量处理）
        while (i + 4 <= length)
        {
            int chunk = (bytes[i] & 0xFF)
                    | ((bytes[i + 1] & 0xFF) << 8)
                    | ((bytes[i + 2] & 0xFF) << 16)
                    | ((bytes[i + 3] & 0xFF) << 24);

            hash ^= chunk;
            hash = (hash << 13) | (hash >>> 19);  // 等效Integer.rotateLeft(hash, 13)
            i += 4;
        }

        // 处理剩余1-3字节
        if (i < length)
        {
            int remaining = length - i;
            int temp = 0;
            for (int j = 0; j < remaining; j++)
            {
                temp |= (bytes[i + j] & 0xFF) << (j * 8);
            }
            hash ^= temp;
            hash = (hash << 7) | (hash >>> 25);  // 小范围循环移位
        }

        // 最终混合
        hash ^= length;
        hash ^= hash >>> 16;
        hash ^= hash >>> 8;
        return hash;
    }

    public ByteFastIgnoreCaseKey(byte[] bytes)
    {
        ByteArrayOutputStream filteredBytes = new ByteArrayOutputStream();
        if (bytes != null)
        {
            for (byte b : bytes)
            {
                if (b >= 'A' && b <= 'Z')
                {
                    filteredBytes.write(b + 32);
                } else if (b >= 'a' && b <= 'z')
                {
                    filteredBytes.write(b);
                }
            }
        }
        this.key = filteredBytes.toByteArray();
        this.hashCode = fastHash(this.key);
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
