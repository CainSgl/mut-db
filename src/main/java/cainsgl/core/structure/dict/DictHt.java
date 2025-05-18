package cainsgl.core.structure.dict;

import java.util.Iterator;
import java.util.Map;

public class DictHt<K, V> extends AbstractDictHt<K, V>
{
    public DictHt(int capacity)
    {
        super(capacity);
    }

    @Override
    public int size()
    {
        if (useCache)
        {
            return cache.size() + super.size();
        }
        return super.size();
    }

    @Override
    public boolean isEmpty()
    {
        if (useCache)
        {
            return cache.isEmpty() && super.isEmpty();
        }
        return super.isEmpty();
    }



    @Override
    public boolean containsKey(Object o)
    {
        if (useCache)
        {
            boolean b = super.containsKey(o);
            if (b)
            {
                return true;
            }
            K k = (K) o;
            int hashcode = k.hashCode();
            return cache.stream().anyMatch((node) -> {
                if (node.hash == hashcode && node.equals(k))
                {
                    return true;
                }
                return false;
            });
        }
        return super.containsKey(o);
    }


    @Override
    public V get(Object o)
    {
        if (useCache)
        {
            V v = super.get(o);
            if (v != null)
            {
                return v;
            }
            K key = (K) o;
            int hashcode = key.hashCode();
            for (Node<K, V> entry : cache)
            {
                // 匹配 hash 和 key
                if (entry.hash == hashcode && (entry.key.equals(key)))
                {
                    return entry.value;
                }
            }
            return null;
        }
        return super.get(o);
    }

    @Override
    public V put(K key, V v)
    {
        if (useCache)
        {
            super.cache.add(new Node<>(key.hashCode(), key, v, null));
            return super.get(key);
        }
        return super.put(key, v);
    }

    @Override
    public V remove(Object o)
    {
        if (useCache)
        {
            V e = super.remove(o);
            if (e == null)
            {
                K key = (K) o;
                int hashcode = key.hashCode();
                Iterator<Node<K, V>> iterator = cache.iterator();
                while (iterator.hasNext())
                {
                    Node<K, V> entry = iterator.next();
                    // 匹配 hash 和 key
                    if (entry.hash == hashcode && (entry.key.equals(key)))
                    {
                        V value = entry.value;
                        iterator.remove();
                        return value; // 返回被删除的值
                    }
                }
            }
            return e;
        }
        return super.remove(o);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        if (useCache)
        {
            map.forEach((k, v) -> {
                cache.add(new Node<>(k.hashCode(), k, v, null));
            });
            return;
        }
        super.putAll(map);
    }

}
