package cainsgl.core.data.key;



import cainsgl.core.data.AbstractByteObj;

import java.nio.charset.StandardCharsets;

public class ByteSuperKey extends AbstractByteObj
{
    public final static int uuid=1;
    byte[] key;
    int hashCode;

    public ByteSuperKey(byte[] key)
    {
        this.key = key;
        hashCode = ByteSuperKey.superHash(key);
    }

    @Override
    public String toString()
    {
        return new String(key, StandardCharsets.UTF_8) + ":" + hashCode;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof ByteSuperKey other)
        {
            if (other.key == this.key) {return true;}
            if(other.key.length != this.key.length) {return false;}
            for(int i = 0; i < this.key.length; i++)
            {
                if(other.key[i] != this.key[i])
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int R1 = 15;
    private static final int R2 = 13;
    private static final int M = 5;
    private static final int N = 0xe6546b64;
    private static final int GOLDEN_RATIO = 0x9e3779b9;

    public static int superHash(byte[] bytes) {
        if (bytes == null) return 0;

        int hash = GOLDEN_RATIO;
        int length = bytes.length;
        int i = 0;

        // 批量处理 4 字节块（32位）
        while (i + 4 <= length) {
            int chunk = getIntLE(bytes, i);
            chunk *= C1;
            chunk = Integer.rotateLeft(chunk, R1);
            chunk *= C2;

            hash ^= chunk;
            hash = Integer.rotateLeft(hash, R2);
            hash = hash * M + N;

            i += 4;
        }

        // 处理剩余 1-3 字节
        switch (length - i) {
            case 3:
                hash ^= (bytes[i + 2] & 0xFF) << 16;
            case 2:
                hash ^= (bytes[i + 1] & 0xFF) << 8;
            case 1:
                hash ^= bytes[i] & 0xFF;
                hash *= C1;
                hash = Integer.rotateLeft(hash, R1);
                hash *= C2;
                break;
        }

        // 最终混合
        hash ^= length;
        hash = fmix(hash);
        return hash;
    }


    private static int getIntLE(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) |
                ((bytes[offset + 1] & 0xFF) << 8) |
                ((bytes[offset + 2] & 0xFF) << 16) |
                ((bytes[offset + 3] & 0xFF) << 24);
    }


    private static int fmix(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    @Override
    public byte[] getBytes()
    {
        return key;
    }
}
