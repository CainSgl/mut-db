package cainsgl.core.structure.dict.entry;

import cainsgl.core.data.MutObj;

import java.util.Arrays;

public class ListSetEntry extends SortedList implements DictEntry {

    public ListSetEntry(int c) {
        super(c);
    }

    @Override
    public boolean add(MNode v) {
        if (size == nodes.length) {
            reSeize();
        }
        for (int i = 0; i < size; i++) {
            if (nodes[i].code == v.code) {
                if (nodes[i].equals(v)) {
                    //hash冲突
                    nodes[i] = v;
                    return false;
                }
            }
        }
        nodes[size++] = v;
        return true;
    }

    @Override
    public MNode remove(byte[] key, int targetCode)
    {
        for (int i = 0; i < size; i++)
        {
            if (nodes[i].code == targetCode)
            {
                if (Arrays.equals(key, nodes[i].key))
                {
                    //删除他
                    MNode v = nodes[i];
                    for (int j = i + 1; j < size; j++)
                    {
                        nodes[j - 1] = nodes[j];
                    }
                    nodes[size - 1] = null;
                    return v;
                }
            }
        }
        return null;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public int expendSize()
    {
        return nodes.length/2;
    }
    private boolean expend;
    @Override
    public boolean expend()
    {
        if(expend)
        {
            expend = false;
            return true;
        }
        return false;
    }


    @Override
    protected void reSeize()
    {
        expend=true;
        super.reSeize();
    }


    @Override
    public MutObj getObj(byte[] key, int targetCode)
    {
        for (int i = 0; i < size; i++)
        {
            if (nodes[i].code == targetCode)
            {
                if (Arrays.equals(key, nodes[i].key))
                {
                    return nodes[i].mutObj;
                }
            }
        }
        return null;
    }
}
