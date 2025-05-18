package cainsgl.core.structure.dict;

import cainsgl.core.config.MutConfiguration;

import java.util.Map;
import java.util.Set;

public class Dict<K, V> extends AbstractDict<K, V>
{
    private int rehash = -1;

    public Dict(int capacity)
    {
        MainTable = new DictHt<>(capacity);
    }

    public Dict()
    {
        this(MutConfiguration.initial_capacity);
    }

    private void rehashEnd()
    {
        rehash = -1;
        MainTable = AssistTable;
        AssistTable = null;
    }

    private void tryStartRehash()
    {
        //计算负载因子
//        float loadFactor = (float) MainTable.size() / (float) MainTable.table.length;
//        if (loadFactor > MutConfiguration.MAX_Load_Factor)
//        {
//            rehash = 0;
//            AssistTable = new NoCacheDictHt<>(MainTable.table.length * 2);
//        } else if (loadFactor < MutConfiguration.MIN_Load_Factor)
//        {
//            if (MainTable.table.length == 2)
//            {
//                return;
//            }
//            rehash = 0;
//            AssistTable = new NoCacheDictHt<>(MainTable.table.length / 2);
//        }
        float loadFactor = MainTable.size / (float) MainTable.table.length;
        if (loadFactor > MutConfiguration.MAX_Load_Factor)
        {
            rehash = 0;
            AssistTable = new NoCacheDictHt<>(MainTable.table.length * 2);
        } else if (loadFactor < MutConfiguration.MIN_Load_Factor)
        {
            if (MainTable.table.length == 2)
            {
                return;
            }
            rehash = 0;
            AssistTable = new NoCacheDictHt<>(MainTable.table.length / 2);
        }
    }

    private void rehash()
    {
        //这里是进行rehash
        AbstractDictHt.Node<K, V> header = MainTable.table[rehash];
        int i = AssistTable.addNodes(header);
        MainTable.size = MainTable.size - i;
        MainTable.table[rehash] = null;
        rehash++;
        if (rehash == MainTable.table.length)
        {
            rehashEnd();
        }
    }

    public void rehashComplete()
    {
        while (rehash != -1)
        {
            rehash();
        }
    }

    @Override
    public int size()
    {
        if (rehash != -1)
        {
            //   System.err.println("现在计算size可能有重复");
            rehashComplete();
            return super.size();
        }
        return super.size();
    }


    private V rehashRemoveByFactor(Object o)
    {
        V v;
        if (rehash > MainTable.table.length / 2 + 1)
        {
            //从辅助表里选
            v = AssistTable.remove(o);
            if (v == null)
            {
                return super.remove(o);
            }
        } else
        {
            v = super.remove(o);
            if (v == null)
            {
                return AssistTable.remove(o);
            }
        }
        return v;
    }


    @Override
    public V remove(Object o)
    {
        if (rehash != -1)
        {
            rehash();
            return rehashRemoveByFactor(o);
        }
        V remove = super.remove(o);
        if (remove != null)
        {
            tryStartRehash();
        }
        return remove;
    }


    private V rehashGetByFactor(Object o)
    {
        V v;
        if (rehash > MainTable.table.length / 2 + 1)
        {
            //从辅助表里选
            v = AssistTable.get(o);
            if (v == null)
            {
                return super.get(o);
            }
        } else
        {
            v = super.get(o);
            if (v == null)
            {
                return AssistTable.get(o);
            }
        }
        return v;
    }

    @Override
    public V get(Object o)
    {
        if (rehash != -1)
        {
            V v = rehashGetByFactor(o);
            rehash();
            return v;
        }
        return super.get(o);
    }


    @Override
    public V put(K k, V v)
    {
        if (rehash != -1)
        {
            V put = AssistTable.put(k, v);
            rehash();
            return put;
        }
        V oldV = super.put(k, v);
        //发送了hash冲突
        tryStartRehash();
        return oldV;
    }

    @Override
    public boolean isEmpty()
    {
        if (rehash != -1)
        {
            rehash();
            return super.isEmpty() && AssistTable.isEmpty();
        }
        return super.isEmpty();
    }

    @Override
    public boolean containsKey(Object o)
    {
        if (rehash != -1)
        {
            rehash();
            return this.get(o) != null;
        }
        return super.containsKey(o);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        if (rehash != -1)
        {
            AssistTable.putAll(map);
            rehash();
            return;
        }
        super.putAll(map);
        tryStartRehash();
    }

    @Override
    public Set<K> keySet()
    {
        if (rehash != -1)
        {
            Set<K> ks = AssistTable.keySet();
            ks.addAll(super.keySet());
            rehash();
            return ks;
        }
        return super.keySet();
    }

    @Override
    public void clear()
    {
        if (rehash != -1)
        {
            AssistTable.clear();
        }
        super.clear();
        rehashEnd();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        if (rehash != -1)
        {
         //   rehashComplete();
            System.err.println("正在渐进式hash，现在遍历会有重复");
            AbstractDictHt.EntrySet es = (AbstractDictHt.EntrySet) super.entrySet();
            AbstractDictHt.CombinationEntrySet combinat = es.combinat((AbstractDictHt.EntrySet) AssistTable.entrySet());
            return combinat;
        }
        return super.entrySet();
    }
}
