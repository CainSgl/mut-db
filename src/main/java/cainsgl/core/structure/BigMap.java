package cainsgl.core.structure;

public class BigMap
{
    private byte[] map;
    private static final byte[] SET_MAP = new byte[]{
            0b00000001, // 第0位
            0b00000010, // 第1位
            0b00000100, // 第2位
            0b00001000, // 第3位
            0b00010000, // 第4位
            0b00100000, // 第5位
            0b01000000, // 第6位
            (byte)0b10000000  // 第7位
    };
    int maxCapacity;
    int count=0;
    public BigMap(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be positive");
        }
        this.maxCapacity = maxCapacity;
        // 计算需要的字节数：(maxCapacity + 7) / 8
        int byteCount = (maxCapacity + 7) >>> 3;
        map = new byte[byteCount];
    }

    public void put(int id) {
        checkId(id);
        if(containsKey(id))
        {
            return;
        }
        count++;
        int where = id >>> 3;
        int index = id & 7;
        map[where] |= SET_MAP[index];
    }

    public boolean containsKey(int id) {
        checkId(id);
        int where = id >>> 3;
        int index = id & 7;
        return (map[where] & SET_MAP[index]) != 0;
    }

    private void checkId(int id) {
        if (id < 0 || id >= maxCapacity) {
            throw new IllegalArgumentException("id out of range: " + id);
        }
    }
    public boolean AllSet()
    {
        return count==maxCapacity;
    }

}
