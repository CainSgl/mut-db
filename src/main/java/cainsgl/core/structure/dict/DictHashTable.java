package cainsgl.core.structure.dict;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.MutObj;
import cainsgl.core.structure.dict.entry.DictEntry;
import cainsgl.core.structure.dict.entry.ListSetEntry;
import cainsgl.core.structure.dict.entry.MNode;
import cainsgl.core.utils.HashUtil;

public class DictHashTable {
    public DictEntry[] entries;
    public int used;
    public int mask;

    public DictHashTable(int capacity) {
        entries = new DictEntry[capacity];
        for (int i = 0; i < capacity; i++) {
            entries[i] = new ListSetEntry(MutConfiguration.initial_capacity);
        }
        mask = capacity - 1;
    }

    public MutObj get(byte[] key, int code) {
        return entries[code & mask].getObj(key, code);
    }

    public MutObj get(byte[] key) {
        int code = HashUtil.fastHash(key);
        return entries[code & mask].getObj(key, code);
    }

    public void put(MNode node) {
        boolean addTrue = entries[node.code & mask].add(node);
        if (addTrue) {
            used++;
        }
    }

    public MutObj put(byte[] key, MutObj o2) {
        int code = HashUtil.fastHash(key);
        int i = code & mask;
        boolean addTrue = entries[i].add(new MNode(key, o2, code));
        if (addTrue) {
            // 添加数据成功；used++
            used++;
        }else {
            // 添加数据失败；返回false
            return null;
        }
        return o2;
    }

    public MutObj remove(byte[] key, int code)
    {

        MutObj remove = entries[code & mask].remove(key, code).mutObj;
        if (remove != null)
        {
            used--;
        }
        return remove;
    }

    public MutObj remove(byte[] key)
    {
        int code = HashUtil.fastHash(key);
        return remove(key, code);
    }

    public MNode removeLast(int index)
    {
        MNode mNode = entries[index & mask].removeLast();
        if (mNode != null) {
            used--;
        }
        return mNode;
    }
}
