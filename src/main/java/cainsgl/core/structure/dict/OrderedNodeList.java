package cainsgl.core.structure.dict;

import java.util.*;
import java.util.function.Predicate;

public class OrderedNodeList<T> implements List<T>
{
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData; // 底层数组
    private int size = 0; // 当前元素数量
    private final Comparator<T> comparator = Comparator.comparingInt(Object::hashCode); // 按 hashCode 排序

    // 构造方法（支持初始容量）
    public OrderedNodeList()
    {
        this.elementData = new Object[DEFAULT_CAPACITY];
    }

    public OrderedNodeList(int initialCapacity)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
    }

    // 核心方法：添加或覆盖元素（重复时覆盖）
    @Override
    public boolean add(T node)
    {
        if (node == null) {throw new NullPointerException("Element cannot be null");}

        // 1. 查找是否已存在（二分查找）
        int index = binarySearch(node);
        if (index >= 0)
        {
            // 存在重复，直接覆盖
            elementData[index] = node;
            return false;
        }

        // 2. 不存在重复，计算插入位置（binarySearch 返回 (-insertionPoint - 1)）
        int insertionPoint = -index - 1;

        // 3. 检查容量并扩容（两倍扩容）
        ensureCapacity(size + 1);

        // 4. 插入元素（后移后续元素）
        System.arraycopy(elementData, insertionPoint, elementData, insertionPoint + 1, size - insertionPoint);
        elementData[insertionPoint] = node;
        size++;
        return true;
    }

    public T put(T node)
    {
        if (node == null) {throw new NullPointerException("Element cannot be null");}

        // 1. 查找是否已存在（二分查找）
        int index = binarySearch(node);
        if (index >= 0)
        {
            // 存在重复，直接覆盖
            Object elementDatum = elementData[index];
            elementData[index] = node;
            return (T)elementDatum;
        }

        // 2. 不存在重复，计算插入位置（binarySearch 返回 (-insertionPoint - 1)）
        int insertionPoint = -index - 1;

        // 3. 检查容量并扩容（两倍扩容）
        ensureCapacity(size + 1);

        // 4. 插入元素（后移后续元素）
        System.arraycopy(elementData, insertionPoint, elementData, insertionPoint + 1, size - insertionPoint);
        elementData[insertionPoint] = node;
        size++;
        return null;
    }

    // 新增方法：弱添加（重复时不添加）
    public boolean weakAdd(T node)
    {
        if (node == null) {throw new NullPointerException("Element cannot be null");}

        int index = binarySearch(node);
        if (index >= 0)
        {
            return false; // 已存在，不添加
        }

        // 不存在，插入逻辑同 add
        int insertionPoint = -index - 1;
        ensureCapacity(size + 1);
        System.arraycopy(elementData, insertionPoint, elementData, insertionPoint + 1, size - insertionPoint);
        elementData[insertionPoint] = node;
        size++;
        return true;
    }

    // 二分查找（返回元素索引或插入点的取反-1）
    private int binarySearch(T target)
    {
        int low = 0;
        int high = size - 1;

        while (low <= high)
        {
            int mid = (low + high) >>> 1;
            T midVal = (T) elementData[mid];
            int cmp = comparator.compare(midVal, target);

            if (cmp < 0)
            {
                low = mid + 1;
            } else if (cmp > 0)
            {
                high = mid - 1;
            } else
            {
                // hashCode 相同，进一步检查 equals（处理哈希冲突）
                if (midVal.equals(target))
                {
                    return mid; // 找到完全匹配的元素
                }

                // 哈希冲突时，线性扫描附近元素（向前）
                for (int i = mid - 1; i >= low; i--)
                {
                    T current = (T) elementData[i];
                    if (comparator.compare(current, target) != 0) {break;}
                    if (current.equals(target)) {return i;}
                }

                // 线性扫描附近元素（向后）
                for (int i = mid + 1; i <= high; i++)
                {
                    T current = (T) elementData[i];
                    if (comparator.compare(current, target) != 0) {break;}
                    if (current.equals(target)) {return i;}
                }

                return -(mid + 1); // 未找到完全匹配，但 hashCode 相同，返回插入点
            }
        }
        return -(low + 1); // 未找到，返回插入点的取反-1
    }

    // 扩容逻辑（两倍扩容）
    private void ensureCapacity(int minCapacity)
    {
        if (minCapacity - elementData.length > 0)
        {
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity == 0 ? DEFAULT_CAPACITY : oldCapacity * 2; // 初始容量为0时设为默认值
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    // 以下为 List 接口必须实现的方法（简化实现）
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
    public boolean contains(Object o)
    {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Itr();
    }

    @Override
    public Object[] toArray()
    {
        return Arrays.copyOf(elementData, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a)
    {
        if (a.length < size)
        {
            return (E[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
        {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean remove(Object o)
    {
        int index = indexOf(o);
        if (index < 0) {return false;}
        remove(index);
        return true;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < size; i++)
        {
            elementData[i] = null;
        }
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elementData[index];
    }

    @Override
    public T set(int index, T element)
    {
        T oldValue = get(index);
        elementData[index] = element;
        return oldValue;
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Ordered list does not support arbitrary position add");
    }

    @Override
    public T remove(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        T oldValue = get(index);
        int numMoved = size - index - 1;
        if (numMoved > 0)
        {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // 帮助 GC
        return oldValue;
    }

    @Override
    public int indexOf(Object o)
    {
        if (o == null)
        {
            for (int i = 0; i < size; i++)
            {
                if (elementData[i] == null) {return i;}
            }
        } else
        {
            for (int i = 0; i < size; i++)
            {
                if (o.equals(elementData[i])) {return i;}
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        if (o == null)
        {
            for (int i = size - 1; i >= 0; i--)
            {
                if (elementData[i] == null) {return i;}
            }
        } else
        {
            for (int i = size - 1; i >= 0; i--)
            {
                if (o.equals(elementData[i])) {return i;}
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return null;
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return null;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        return null;
    }

    // 以下为可选实现的方法（简化处理）
    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object e : c)
        {
            if (!contains(e)) {return false;}
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        boolean modified = false;
        for (T e : c)
        {
            if (add(e)) {modified = true;}
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean modified = false;
        for (Object e : c)
        {
            if (remove(e)) {modified = true;}
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean modified = false;
        for (int i = 0; i < size; )
        {
            if (!c.contains(elementData[i]))
            {
                remove(i);
                modified = true;
            } else
            {
                i++;
            }
        }
        return modified;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter)
    {
        boolean modified = false;
        for (int i = 0; i < size; )
        {
            if (filter.test(get(i)))
            {
                remove(i);
                modified = true;
            } else
            {
                i++;
            }
        }
        return modified;
    }

    // 迭代器实现（简化版）
    private class Itr implements Iterator<T>
    {
        int cursor; // 下一个要返回的元素索引
        int lastRet = -1; // 最后一个返回的元素索引（-1 表示无）

        public boolean hasNext()
        {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public T next()
        {
            int i = cursor;
            if (i >= size)
            {
                throw new NoSuchElementException();
            }
            Object[] elementData = OrderedNodeList.this.elementData;
            if (i >= elementData.length)
            {
                throw new ConcurrentModificationException();
            }
            cursor = i + 1;
            return (T) elementData[lastRet = i];
        }

        public void remove()
        {
            if (lastRet < 0)
            {
                throw new IllegalStateException();
            }
            try
            {
                OrderedNodeList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex)
            {
                throw new ConcurrentModificationException();
            }
        }
    }


    /**
     * 快速查找与 target 匹配的节点（hashCode 和 equals 双重判断）
     *
     * @param target 要查找的目标对象（类型 T）
     * @return 匹配的节点（Optional，可能为空）
     */
    public T find(T target)
    {
        if (this.isEmpty())
        {
            return null;
        }

        // 1. 二分查找定位 hashCode 区间
        int index = Collections.binarySearch(this, target, comparator);

        // 2. 处理 binarySearch 的返回值（可能为负，或正但需要检查 equals）
        if (index >= 0)
        {
            // 找到 hashCode 相同的元素，向前后扫描检查 equals
            return scanNearby(index, target);
        } else
        {
            // 未找到 hashCode 相同的元素（插入点为 (-index - 1)），直接返回空
            return null;
        }
    }

    /**
     * 扫描 hashCode 相同的附近元素，检查 equals 是否匹配
     */
    private T scanNearby(int startIndex, T target)
    {
        // 向前扫描（处理相同 hashCode 的元素）
        for (int i = startIndex; i >= 0; i--)
        {
            T current = this.get(i);
            if (current.hashCode() != target.hashCode())
            {
                break; // 超出 hashCode 相同区间
            }
            if (current.equals(target))
            {
                return current;
            }
        }

        // 向后扫描（处理相同 hashCode 的元素）
        for (int i = startIndex + 1; i < this.size(); i++)
        {
            T current = this.get(i);
            if (current.hashCode() != target.hashCode())
            {
                break; // 超出 hashCode 相同区间
            }
            if (current.equals(target))
            {
                return current;
            }
        }
        return null; // 无匹配元素
    }
}
