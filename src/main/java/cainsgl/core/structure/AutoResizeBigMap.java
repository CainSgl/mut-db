package cainsgl.core.structure;

public class AutoResizeBigMap
{
    private byte[] map;
    private static final byte[] SET_MAP = new byte[]{
            0b00000001,
            0b00000010,
            0b00000100,
            0b00001000,
            0b00010000,
            0b00100000,
            0b01000000,
            (byte) 0b10000000
    };
    private int maxCapacity;
    private int count = 0;
    private int maxId = -1;
    private final int minCapacity;

    public AutoResizeBigMap(int initialCapacity)
    {
        this(initialCapacity, Math.max(1, initialCapacity / 2));
    }

    public AutoResizeBigMap(int initialCapacity, int minCapacity)
    {
        if (initialCapacity <= 0 || minCapacity <= 0)
        {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.minCapacity = minCapacity;
        this.maxCapacity = initialCapacity;
        int byteCount = (initialCapacity + 7) >>> 3;  // 等价于 (initialCapacity+7)/8
        map = new byte[byteCount];
    }

    // 核心：插入元素（自动扩容）
    public void put(int id)
    {
        if (id < 0) {throw new IllegalArgumentException("id must be non-negative");}
        if (id >= maxCapacity)
        {
            resizeUp(id);
        }

        if (containsKey(id)) {return;}

        int where = id >>> 3;
        int index = id & 7;
        map[where] |= SET_MAP[index];
        count++;
        if (id > maxId)
        {
            maxId = id;  // 更新最大id
        }
        if (minId == -1 || id < minId)
        {
            minId = id;
        }
    }

    // 核心：移除元素（可能触发缩容）
    public boolean remove(int id)
    {
        if (checkId(id))
        {
            if (!containsKey(id)) {return false;}

            int where = id >>> 3;
            int index = id & 7;
            map[where] &= ~SET_MAP[index];
            count--;

            // 如果移除的是当前最小id，需要重新查找
            if (id == minId)
            {
                minId = findNewMinId(minId);
            }
            if (id == maxId)
            {
                maxId = findNewMaxId(maxId);
            }
            checkResizeDown();
            return true;
        }
        return false;
    }

    // 检查是否需要缩容（当容量冗余超过20%时触发）
    private void checkResizeDown()
    {
        if (maxCapacity <= minCapacity)
        {
            return;  // 已达最小容量，不缩容
        }

        // 计算有效容量需求：最大id + 1（因为id从0开始）
        int requiredCapacity = maxId + 1;
        // 冗余阈值：当前容量的20%
        int redundancyThreshold = (int) (maxCapacity * 0.2);

        // 如果当前容量比需求大超过20%，则缩容
        if (maxCapacity - requiredCapacity > redundancyThreshold)
        {
            resizeDown(requiredCapacity);
        }
    }

    // 扩容实现（至少扩容50%或足够容纳目标id）
    private void resizeUp(int targetId)
    {
        int newCapacity = Math.max(
                (int) (maxCapacity * 1.5),  // 扩容50%
                targetId + 1                // 至少能容纳当前id
        );
        performResize(newCapacity);
    }

    // 缩容实现（至少保留最小容量）
    private void resizeDown(int requiredCapacity)
    {
        int newCapacity = Math.max(
                requiredCapacity + (int) (requiredCapacity * 0.2),  // 保留20%余量
                minCapacity                                         // 不低于最小容量
        );
        performResize(newCapacity);
    }

    // 执行实际的扩容/缩容操作
    private void performResize(int newCapacity)
    {
        int newByteCount = (newCapacity + 7) >>> 3;
        byte[] newMap = new byte[newByteCount];

        // 复制原数据（缩容时会自动截断超出部分）
        int copyLength = Math.min(map.length, newByteCount);
        System.arraycopy(map, 0, newMap, 0, copyLength);

        this.map = newMap;
        this.maxCapacity = newCapacity;
    }

    // 查找当前存在的最大id（效率优化：倒序遍历字节数组）
    private int findNewMaxId(int from)
    {
        // 从最高位字节开始倒序检查
        for (int i = from; i >= 0; i--)
        {
            if (map[i] == 0)
            {
                continue;  // 空字节跳过
            }

            // 检查当前字节的每一位（从高位到低位）
            for (int j = 7; j >= 0; j--)
            {
                int currentId = (i << 3) + j;
                if (currentId >= maxCapacity)
                {
                    continue;  // 超出当前容量的id无效
                }
                if ((map[i] & SET_MAP[j]) != 0)
                {
                    return currentId;
                }
            }
        }
        return -1;  // 无元素
    }

    // 以下为原BigMap保留的核心方法
    public boolean containsKey(int id)
    {
        if (checkId(id))
        {
            int where = id >>> 3;
            if (where >= map.length)
            {
                return false;  // 超出当前字节数组范围
            }
            int index = id & 7;
            return (map[where] & SET_MAP[index]) != 0;
        }
        return false;
    }

    private boolean checkId(int id)
    {
        return id >= 0 && id < maxCapacity;
    }

    // 辅助方法
    public int size() {return count;}

    public int currentCapacity() {return maxCapacity;}

    private int minId = -1;

    public int currentMaxId() {return maxId;}

    // 新增：查找最小置位id的核心方法
    public int findMinId()
    {
        // 缓存有效时直接返回
        if (minId != -1 && containsKey(minId))
        {
            return minId;
        }
        // 缓存失效时重新查找
        return minId = findNewMinId(0);
    }

    // 实际查找逻辑（与缓存解耦）
    private int findNewMinId(int from)
    {
        if (count == 0)
        {
            return -1;  // 无元素
        }

        // 遍历字节数组（从低位到高位）
        for (int i = from; i < map.length; i++)
        {
            if (map[i] == 0)
            {
                continue;  // 空字节跳过
            }

            // 检查当前字节的每一位（从低位到高位）
            for (int j = 0; j < 8; j++)
            {
                int currentId = (i << 3) + j;
                // 超出当前容量的id无效（可能因缩容导致）
                if (currentId >= maxCapacity)
                {
                    continue;
                }
                if ((map[i] & SET_MAP[j]) != 0)
                {
                    return currentId;  // 找到最小置位id
                }
            }
        }
        return -1;  // 理论上不会到达（count>0时必然有元素）
    }
}