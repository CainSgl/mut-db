package cainsgl.core.utils;

public class HashUtil
{
    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int R1 = 15;
    private static final int R2 = 13;
    private static final int M = 5;
    private static final int N = 0xe6546b64;
    private static final int GOLDEN_RATIO = 0x9e3779b9;

    /**
     * 高效计算 byte[] 的哈希值（低碰撞优化版）
     * @param bytes 输入字节数组（允许 null）
     * @return 哈希值（null 返回 0）
     */
    public static int fastHash(byte[] bytes) {
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

}
