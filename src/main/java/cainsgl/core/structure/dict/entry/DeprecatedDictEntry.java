package cainsgl.core.structure.dict.entry;

import cainsgl.core.data.MutObj;

@Deprecated
public class DeprecatedDictEntry
{
    SortedList list;

    public DeprecatedDictEntry()
    {
        list = new SortedSet(4);
    }

    public MutObj get(byte[] key, int code)
    {
        return list.getObj(key, code);
    }

    public boolean put(byte[] key, MutObj obj, int code)
    {
        return list.add(new MNode(key, obj, code));
    }

    public boolean put(MNode mNode)
    {
        return list.add(mNode);
    }

    public MutObj remove(byte[] key, int code)
    {
        return list.remove(key, code).mutObj;
    }

    public int size()
    {
        return list.size;
    }

    public MNode removeLast()
    {
        return list.removeLast();
    }
}
