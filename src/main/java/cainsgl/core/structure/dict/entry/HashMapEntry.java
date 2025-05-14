package cainsgl.core.structure.dict.entry;

@Deprecated
public class HashMapEntry // extends HashMap<Integer,MNode> implements DictEntry
{
//    int lastSize;
//
//    public HashMapEntry(int initialCapacity)
//    {
//        super(initialCapacity);
//        lastSize=initialCapacity;
//    }
//
//    @Override
//    public MutObj getObj(byte[] key, int code)
//    {
//        MNode mNode = super.get(code);
//        if (mNode == null)
//        {return null;}
//        return mNode.mutObj;
//    }
//
//    @Override
//    public boolean add(MNode node)
//    {
//        MNode put = super.put(node.code, node);
//        if(put!=null)
//        {
//            //说明原来有数据，hash冲突
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public MNode remove(byte[] key, int code)
//    {
//        MNode remove = super.get(code);
//        if (Arrays.equals(remove.key, key))
//        {
//            return super.remove(code);
//        } else
//        {
//            return null;
//        }
//    }
//
//    @Override
//    public MNode removeLast()
//    {
//        Iterator<Map.Entry<Integer, MNode>> iterator = super.entrySet().iterator();
//        if(iterator.hasNext())
//        {
//            Integer key = iterator.next().getKey();
//            return super.remove(key);
//        }
//       return null;
//    }
//
//    @Override
//    public int size()
//    {
//        return super.size();
//    }
//
//    @Override
//    public int expendSize()
//    {
//        return super.size()-lastSize;
//    }
//
//    @Override
//    public boolean conflict()
//    {
//        return lastSize<super.size()/2;
//    }
}
