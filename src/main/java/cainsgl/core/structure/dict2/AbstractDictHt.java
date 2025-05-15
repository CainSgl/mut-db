package cainsgl.core.structure.dict2;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AbstractDictHt<K, V> implements Map<K, V>
{
    // private List<Node<K, V>>[] buckets;
    //  transient AbstractDictHt.Node<K, V>[] table;
    AbstractDictHt.Node<K, V>[] table;
    private final int mask;
    int size;

    @Override
    public String toString()
    {
        return Arrays.toString(table);
    }

    private Set<Map.Entry<K, V>> entrySet;
    //public int modCount;
    protected ConcurrentLinkedDeque<Node<K, V>> cache = null;
    protected volatile boolean useCache = false;

    public AbstractDictHt(int capacity)
    {
        if (capacity > 0 && (capacity & (capacity - 1)) != 0)
        {
            throw new IllegalArgumentException("dict capacity must be 2^n");
        }
        table = new AbstractDictHt.Node[capacity];
        mask = capacity - 1;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object o)
    {
        return get(o) != null;
    }

    @Override
    public boolean containsValue(Object o)
    {
        throw new UnsupportedOperationException("不支持直接从值里找数据");
    }

    @Override
    public V get(Object o)
    {
        K key = (K) o;
        int hashcode = key.hashCode();
        Node<K, V> bucket = table[hashcode & mask];
        if (bucket == null) {return null;}
        return bucket.findNodeByHash(key, hashcode);
    }

    @Override
    public V put(K key, V v)
    {
        int hashcode = key.hashCode();
        int index = hashcode & mask;
        if (table[index] == null)
        {
            table[index] = new Node<>(hashcode, key, v, null);
            size++;
            return null;
        }
        V oldV = table[index].insertNodeByHash(key, v, hashcode, index, table);
        if (oldV == null)
        {
            size++;
        }
        return oldV;
    }

    public boolean weakPut(K key, V v)
    {
        int hashcode = key.hashCode();
        int index = hashcode & mask;
        if (table[index] == null)
        {
            table[index] = new Node<>(hashcode, key, v, null);
            size++;
            return true;
        }
        if (table[index].insertWeakNodeByHash(key, v, hashcode, index, table))
        {
            size++;
            return true;
        }
        return false;
    }

    @Override
    public V remove(Object o)
    {
        K key = (K) o;
        int hashcode = key.hashCode();
        int index = hashcode & mask;
        if (table[index] != null)
        {
            V v = table[index].removeNodeByHash(key, hashcode, index, table);
            if (v != null)
            {
                size--;
            }
            return v;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        map.forEach(this::put);
    }

    @Override
    public void clear()
    {
        useCache = false;
        cache = null;
        Arrays.fill(table, null);
        size = 0;
    }

    @Override
    public Set<K> keySet()
    {
        throw new UnsupportedOperationException("不支持直接获取keySet");
    }

    @Override
    public Collection<V> values()
    {
        throw new UnsupportedOperationException("不支持直接获取values");
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        if (entrySet == null)
        {
            entrySet = new EntrySet<>();
        }
        return entrySet;
        //  return null;
    }

    final V addNode(Node<K, V> node)
    {
        int index = node.hash & mask;
        if (table[index] == null)
        {
            table[index] = node;
            size++;
        } else
        {
            V v = table[index].insertNodeByHash(node.key, node.value, node.hash, index, table);
            if (v == null)
            {
                size++;
            }
        }
        return null;
    }


    final void replaceAll(List<Node<K, V>> nodes)
    {
        for (Node<K, V> node : nodes)
        {
            this.addNode(node);
        }
    }

    public int addNodes(Node<K, V> current)
    {
        //从他的头开始
        int count = 0;
        while (current != null)
        {
            int index = current.hash & mask;
            if (table[index] == null)
            {
                table[index] = current;
                count++;
                size++;
            } else
            {
                if (table[index].insertWeakNodeByHash(current.key, current.value, current.hash, index, table))
                {
                    count++;
                    size++;
                }
            }
            current = current.next;
        }
        return count;
    }

    final class CombinationEntrySet<E extends Map.Entry<K, V>> extends EntrySet<E>
    {
        EntrySet<E> other;
        EntrySet<E> current;

        public CombinationEntrySet(EntrySet<E> other, EntrySet<E> current)
        {
            this.other = other;
            this.current = current;
        }

        @Override
        public void clear()
        {
            other.clear();
            current.clear();
        }

        @Override
        public int size()
        {
            return other.size() + current.size();
        }

        @Override
        public Iterator<Entry<K, V>> iterator()
        {
            EntryIterator iterator = (EntryIterator) other.iterator();
            return iterator.combined((EntryIterator) current.iterator());
        }


    }

    class EntrySet<E extends Map.Entry<K, V>> extends AbstractSet<Map.Entry<K, V>>
    {
        public EntrySet()
        {

        }

        @Override
        public void clear()
        {
            AbstractDictHt.this.clear();
        }

        @Override
        public int size()
        {
            return AbstractDictHt.this.size();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return AbstractDictHt.this.new EntryIterator();
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c)
        {
            throw new UnsupportedOperationException();
        }

        public CombinationEntrySet<E> combinat(EntrySet<E> c)
        {
            return new CombinationEntrySet<>(this, c);
        }
    }

    public final static class Node<K, V> implements Map.Entry<K, V>
    {
        final K key;
        final int hash;
        V value;
        Node<K, V> next;

        //        int length;
        Node(int hash, K key, V value, AbstractDictHt.Node<K, V> next)
        {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public int hashCode()
        {
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            return key.equals(obj);
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            Node<K, V> current = this;
            while (current != null)
            {
                sb.append(current.key.toString()).append(":").append(current.value.toString()).append(",");
                if (current == this)
                {
                    throw new IllegalArgumentException("无线循环列表");
                }
                current = current.next;
            }
            sb.append("]");
            return sb.toString();
        }

        public boolean insertWeakNodeByHash(K key, V value, int hashcode, int index, Node[] nodes)
        {
            Node<K, V> prev = this;
            Node<K, V> current = this.next;
            if (prev.hash > hashcode)
            {
                //你来当第一个
                nodes[index] = new Node<>(hashcode, key, value, prev);
                return true;
            }
            if (prev.hash == hashcode)
            {
                if (prev.key.equals(key))
                {
                    //不覆盖
                    return false;
                }
            }
            while (current != null)
            {
                if (current.hash < hashcode)
                {
                    prev = current;
                    current = current.next;
                    continue;
                }
                if (current.hash == hashcode)
                {
                    if (current.key.equals(key))
                    {
                        //不覆盖
                        return false;
                    }
                    //hash冲突
                    prev.next = new Node<>(hashcode, key, value, current);
                    return true;
                }
                //这里就是大于
                prev.next = new Node<>(hashcode, key, value, current);
                return true;
            }
            prev.next = new Node<>(hashcode, key, value, null);
            return true;
        }

        public V insertNodeByHash(K key, V value, int hashcode, int index, Node[] nodes)
        {

            Node<K, V> prev = this;
            Node<K, V> current = this.next;
            if (prev.hash > hashcode)
            {
                //你来当第一个
                nodes[index] = new Node<>(hashcode, key, value, prev);
                return null;
            }
            if (prev.hash == hashcode)
            {
                if (prev.key.equals(key))
                {
                    //直接覆盖
                    nodes[index] = new Node<>(hashcode, key, value, current);
                    return prev.value;
                }
            }
            while (current != null)
            {
                if (current.hash < hashcode)
                {
                    prev = current;
                    current = current.next;
                    continue;
                }
                if (current.hash == hashcode)
                {
                    if (current.key.equals(key))
                    {
                        //覆盖
                        prev.next = new Node<>(hashcode, key, value, current.next);
                        return current.value;
                    }
                    prev.next = new Node<>(hashcode, key, value, current);
                    return null;
                }
                //这里就是大于
                prev.next = new Node<>(hashcode, key, value, current);
                return null;
            }
            prev.next = new Node<>(hashcode, key, value, null);
            return null;
        }

        public V findNodeByHash(K key, int hashcode)
        {
            Node<K, V> current = this;
            while (current != null)
            {
                if (current.hash < hashcode)
                {
                    current = current.next;
                    continue;
                } else if (current.hash > hashcode)
                {
                    return null;
                }
                if (current.key.equals(key))
                {
                    //保证当前的hash相同，上面这个是两次判断简化了
                    return current.value;
                }
                current = current.next;
            }
            return null;

        }

        public V removeNodeByHash(K key, int hashcode, int index, Node[] nodes)
        {
            Node<K, V> prev = this;
            Node<K, V> current = this.next;
            if (prev.hash == hashcode)
            {
                if (prev.key.equals(key))
                {
                    //直接覆盖
                    nodes[index] = current;
                    return prev.value;
                }
            }
            while (current != null)
            {
                if (current.hash < hashcode)
                {
                    prev = current;
                    current = current.next;
                    continue;
                }
                if (current.hash == hashcode)
                {
                    if (current.key.equals(key))
                    {
                        //覆盖
                        prev.next = current.next;
                        return current.value;
                    } else
                    {
                        //寻找下一个
                        prev = current;
                        current = current.next;
                        continue;
                    }
                }
                //没有了，不用删了
                return null;
            }
            return null;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V v)
        {
            V oldValue = this.value;
            this.value = v;
            return oldValue;
        }
    }

    final class CombinatEntryIterator extends EntryIterator
    {
        HashIterator other;
        HashIterator current;
        public CombinatEntryIterator(HashIterator other, HashIterator current)
        {
            this.other = other;
            this.current = current;
        }

        @Override
        public boolean hasNext()
        {
            if (current != null)
            {
                boolean b = current.hasNext();
                if (!b)
                {
                    current = null;
                    return other.hasNext();
                }
                return true;
            } else
            {
                return other.hasNext();
            }
        }

        @Override
        public Node<K, V> nextNode()
        {
            if (current != null)
            {

                return current.nextNode();
            } else
            {
                return other.nextNode();
            }
        }

        @Override
        public void remove()
        {
            if (current != null)
            {
                current.remove();
            } else
            {
                other.remove();
            }
        }
    }

    class EntryIterator extends AbstractDictHt<K, V>.HashIterator implements Iterator<Map.Entry<K, V>>
    {
        public final Map.Entry<K, V> next()
        {
            return this.nextNode();
        }

        public Iterator<Map.Entry<K, V>> combined(HashIterator iterator)
        {
            return new CombinatEntryIterator(this, iterator);
        }
    }


    abstract class HashIterator
    {
        Node<K, V> current;
        Node<K,V> prev;
        int index;
        //    int allData;
        List<Node<K, V>> replaceCache;

        HashIterator()
        {
            AbstractDictHt.this.useCache = true;
            AbstractDictHt.this.cache = new ConcurrentLinkedDeque<>();
            for (int i=0;i<table.length;i++)
            {
                current =table[i] ;
                if (current != null)
                {
                    index=i;
                    break;
                }
            }
            //   allData = AbstractDictHt.this.size;
            replaceCache = new LinkedList<>();
        }

        public boolean testAfterNode(int from)
        {
            if (current != null && current.next != null)
            {
                return true;
            }
            for (; from < AbstractDictHt.this.table.length; from++)
            {
                if (AbstractDictHt.this.table[from] != null)
                {
                    //说明还有
                    return true;
                }
            }
            return false;
        }

        public boolean hasNext()
        {
            //从index开是增加，看后续的表是否真没数据了
            boolean noNext = (!testAfterNode(index) && AbstractDictHt.this.cache.isEmpty());
            if (noNext)
            {
                AbstractDictHt.this.cache = null;
                AbstractDictHt.this.useCache = false;
                //重新放回数据
                AbstractDictHt.this.replaceAll(replaceCache);
                replaceCache = null;
            }
            return !noNext;
        }

        boolean removed;

        //    int cacheIndex;
        boolean atTheLast;
        public AbstractDictHt.Node<K, V> nextNode()
        {
            if (index == AbstractDictHt.this.table.length)
            {
                atTheLast=false;
                Node<K, V> kvNode = cache.pollFirst();
                if (kvNode == null)
                {
                    throw new NoSuchElementException();
                }
                removed = false;
                this.replaceCache.add(kvNode);
                return kvNode;
            }
            removed = false;
            Node<K,V> e=current;
            prev=current;
            current = current.next;
            while(current==null)
            {
                index++;
                if(index==AbstractDictHt.this.table.length)
                {
                    atTheLast=true;
                    return e;
                }
                prev=null;
                current = table[index];
            }
            return e;
        }

        //该方法有问题
        public void remove()
        {
            if (removed)
            {
                throw new NullPointerException();
            }
            removed=true;
            AbstractDictHt.this.size--;
            if(index==AbstractDictHt.this.table.length)
            {
                if(atTheLast)
                {
                    //删除最后一个元素即可
                    removeTheLast();
                }else
                {
                    replaceCache.removeLast();
                }
                return;
            }
            if(prev==null)
            {
                //说明切换桶了
                table[index]=table[index].next;
            }else
            {
                //正常删除
                prev.next=current.next;
            }


            //删除current
        }
    }

    private void removeTheLast()
    {
        int len=table.length-1;
        for(int i=len;i>0;len--)
        {
            Node<K,V> theLast=table[len];
            if(theLast==null)
            {
                continue;
            }
            //找到了
            Node<K,V> up=theLast;
            while(true)
            {
                if(theLast.next==null)
                {
                    //删除他即可
                    up.next=null;
                    return;
                }else
                {
                    up=theLast;
                    theLast=theLast.next;
                }
            }
        }
    }
}
