package cainsgl.core.structure.dict.entry;

import cainsgl.core.data.MutObj;

import java.util.Arrays;

@Deprecated
public class SortedList
{
    MNode[] nodes;
    public int size;

    public SortedList(int capacity)
    {
        nodes = new MNode[capacity];
        this.size = 0;
    }

    public boolean add(MNode v)
    {
        if (v == null) {return false;}
        if (size >= nodes.length)
        {
            reSeize();
        }
        int insertPos = 0;
        while (insertPos < size && nodes[insertPos].code < v.code)
        {
            insertPos++;
        }
        for (int i = size; i > insertPos; i--)
        {
            nodes[i] = nodes[i - 1];
        }
        nodes[insertPos] = v;
        size++;
        return true;
    }

    public MutObj getObj(byte[] key, int targetCode)
    {
        int i = findIndex(key, targetCode);
        if (i == -1)
        {
            return null;
        } else
        {
            return nodes[i].mutObj;
        }
    }

    public int findIndex(byte[] key, int targetCode)
    {
        if (size == 0) {return -1;}

        int left = 0;
        int right = size - 1;
        int mid;

        // 插值查找核心逻辑（处理均匀分布）
        while (left <= right
                && nodes[left].code <= targetCode
                && nodes[right].code >= targetCode)
        {
            if (nodes[left].code == nodes[right].code)
            {
                mid = left;
            } else
            {
                // 插值公式：根据目标code在[left, right]区间的比例估算位置
                mid = left + (int) ((double) (targetCode - nodes[left].code)
                        * (right - left)
                        / (nodes[right].code - nodes[left].code));
                mid = Math.max(left, Math.min(mid, right));
            }

            int midCode = nodes[mid].code;
            if (midCode == targetCode)
            {
                // 找到code匹配点，向左右扩展找到所有同code区间
                return findInCodeRange(mid, targetCode, key);
            } else if (midCode < targetCode)
            {
                left = mid + 1;
            } else
            {
                right = mid - 1;
            }
        }

        // 未找到code范围，检查边界元素（处理非均匀分布的极端情况）
        if (left < size && nodes[left].code == targetCode)
        {
            return findInCodeRange(left, targetCode, key);
        }
        if (right >= 0 && nodes[right].code == targetCode)
        {
            return findInCodeRange(right, targetCode, key);
        }

        return -1;
    }


    public MNode remove(byte[] key, int targetCode)
    {
        int index = findIndex(key, targetCode);
        if (index == -1)
        {
            return null;
        } else
        {
            MNode node = nodes[index];
            //覆盖掉原来的值
            for (int i = index+1; i < size; i++)
            {
                nodes[i - 1] = nodes[i];
            }
            nodes[size - 1] = null;
            size--;
            return node;
        }
    }

    /**
     * 在code匹配的位置向左右扩展，找到所有同code元素并匹配key
     *
     * @param start      初始匹配位置
     * @param targetCode 目标code
     * @param key        目标key
     * @return 匹配的节点
     */
    private int findInCodeRange(int start, int targetCode, byte[] key)
    {
        // 向右扩展找到最大的同code索引
        int end = start;
        while (end + 1 < size && nodes[end + 1].code == targetCode)
        {
            end++;
        }

        // 向左扩展找到最小的同code索引
        int begin = start;
        while (begin - 1 >= 0 && nodes[begin - 1].code == targetCode)
        {
            begin--;
        }

        // 在[begin, end]区间内查找key匹配的节点
        for (int i = begin; i <= end; i++)
        {
            if (Arrays.equals(nodes[i].key, key))
            {
                return i;
            }
        }
        return -1;
    }

    protected void reSeize()
    {
        MNode[] newNodes = new MNode[nodes.length * 2];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        nodes = newNodes;
    }

    @Override
    public String toString()
    {
        return Arrays.toString(nodes);
    }

    public MNode removeLast()
    {
        if(size==0)
        {
            //本身就是空的了
            return null;
        }
        size--;
        MNode node = nodes[size];
        nodes[size] = null;
        return node;
    }
}
