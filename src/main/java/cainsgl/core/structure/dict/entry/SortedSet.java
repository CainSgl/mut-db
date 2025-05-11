package cainsgl.core.structure.dict.entry;
@Deprecated
public class SortedSet extends SortedList
{
    public SortedSet(int capacity)
    {
        super(capacity);
    }

    @Override
    public boolean add(MNode v)
    {
        if (v == null) {return false;}
        if (size >= nodes.length)
        {
            super.reSeize();
        }
        int insertPos = 0;
        while (insertPos < size && nodes[insertPos].code < v.code)
        {
            insertPos++;
        }

        if(nodes[insertPos]==null)
        {
            //这里明显就是插入到size
            nodes[insertPos] = v;
            size++;
            return true;
        }
        if(nodes[insertPos].code == v.code)
        {
            //判断是否重复
            if(v.equals(nodes[insertPos]))
            {
                nodes[insertPos] = v;
                return false;
            }
        }
        for (int i = size; i > insertPos; i--)
        {
            nodes[i] = nodes[i - 1];
        }
        nodes[insertPos] = v;
        size++;
        return true;
    }
}
