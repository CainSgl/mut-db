package cainsgl.core.structure.dict.entry;

import cainsgl.core.data.MutObj;

public interface DictEntry
{
    MutObj getObj(byte[] key, int code);
    boolean add(MNode node);
    MNode remove(byte[] key, int code);
    MNode removeLast();
    int size();

    int expendSize();

    boolean expend();
    // boolean conflict();

}
