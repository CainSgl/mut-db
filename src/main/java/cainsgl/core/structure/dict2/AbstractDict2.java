package cainsgl.core.structure.dict2;

import cainsgl.core.config.MutConfiguration;

import java.util.*;

public abstract class AbstractDict2<K, V> implements Map<K, V>
{
    protected AbstractDictHt<K, V> MainTable;
    protected AbstractDictHt<K, V> AssistTable;
    //transient Set<K> keySet;


//    public AbstractDict2(int capacity)
//    {
//        // keySet = new HashSet<K>(capacity);
//        MainTable = new DictHt<>(capacity);
//    }
//
//    public AbstractDict2()
//    {
//        this(MutConfiguration.initial_capacity);
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        AbstractDict2<K, V> clone = (AbstractDict2<K, V>) super.clone();
        if (clone.MainTable != null)
        {
            clone.MainTable = (AbstractDictHt<K, V>) this.MainTable.clone();
        }
        if (clone.AssistTable != null)
        {
            clone.AssistTable = (AbstractDictHt<K, V>) this.AssistTable.clone();
        }
        return clone;
    }

    @Override
    public int size()
    {
        return MainTable.size();
    }

    @Override
    public boolean isEmpty()
    {
        return MainTable.isEmpty();
    }

    @Override
    public boolean containsKey(Object o)
    {
        return MainTable.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o)
    {
        return MainTable.containsValue(o);
    }

    @Override
    public V get(Object o)
    {
        return MainTable.get(o);
    }

    @Override
    public V put(K k, V v)
    {
        return MainTable.put(k, v);
    }

    @Override
    public V remove(Object o)
    {
        return MainTable.remove(o);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        MainTable.putAll(map);
    }

    @Override
    public void clear()
    {
        MainTable.clear();
    }

    @Override
    public Set<K> keySet()
    {
        return MainTable.keySet();
    }

    @Override
    public Collection<V> values()
    {
        return MainTable.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return MainTable.entrySet();
    }
}
