package cainsgl;

import cainsgl.core.data.ByteKey;
import cainsgl.core.structure.BigMap;
import cainsgl.core.structure.dict2.DictHt;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class TestMap
{
    public static byte[] getRandoByte() throws UnsupportedEncodingException
    {
        Random rand = new Random();
        String t = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int t2 = rand.nextInt(10);
        String s = "";
        for (int i = 0; i < t2; i++)
        {
            int i2 = rand.nextInt(25);
            s = s + t.substring(i2, i2 + 1);
        }
        byte[] bytes = s.getBytes("UTF-8");
        if (bytes.length == 0)
        {
            return new byte[]{'a', 'd'};
        }
        return bytes;
    }


    public static void main(String[] args) throws Exception
    {
        Map<ByteKey, Integer> Dict = new DictHt<>(4);
        Map<ByteKey,Integer> map= new HashMap<>();
        List<ByteKey> nodes = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            byte[] randoByte = getRandoByte();
            nodes.add(new ByteKey(randoByte));
        }

      //  Dict.put(nodes.get(0),"你好啊");
       // List<ByteKey> t=new ArrayList<>();
       // List<Integer> ids=new ArrayList<>();
        for(int i = 0; i < 10; i++)
        {
//            if(Dict.containsKey(nodes.get(i)))
//            {
//                if(map.containsKey(nodes.get(i)))
//                {
//                    System.out.println("yes");
//                    continue;
//                }
//            }
            Integer put = Dict.put(nodes.get(i), i);
            if(put!=null)
            {
                System.out.println(put);
            }
    //        Integer put1 = map.put(nodes.get(i), i);
//            if(put!=put1)
//            {
//                if(put==null)
//                {
//                    System.out.println("???");
//                }else
//                {
//                    if(!put.equals(put1))
//                    {
//                        t.add(nodes.get(i));
//                        ids.add(i);
//                        System.out.println("?????");
//                    }
//                }
//            }
        }
        int size = Dict.size();
        System.out.println(size);
        Iterator<Map.Entry<ByteKey, Integer>> iterator = Dict.entrySet().iterator();
        int asdasd=0;
        while(iterator.hasNext())
        {
            asdasd++;
            Map.Entry<ByteKey, Integer> next = iterator.next();
            if(next==null)
            {
                continue;
            }
            ByteKey key  =next.getKey();
            System.out.println(key);
            iterator.remove();
        }
        System.out.println(Dict.size());
       int j=0;
//        for(int i = 0; i < 300000; i++)
//        {
//            ByteKey byteKey = nodes.get(i);
//            Integer i1 = map.get(byteKey);
//            Integer o = Dict.get(byteKey);
//           if(!o.equals(i1))
//           {
//               System.out.println(o+" "+i1);
//           }
//        }
        for(int i = 0; i < 300000; i++)
        {
//            Object o = Dict.get(new ByteKey(getRandoByte()));
//            if(o==null)
//            {
//                System.err.println(i);
//                continue;
//            }
//            if(o.equals(i))
//            {
//                continue;
//            }
//
//            System.err.println(o);
        }

    //    Object o = Dict.get(nodes.get(0));
        return;
//        long stratTime = System.currentTimeMillis();
//        System.out.println("存开始时间Map:" + stratTime);
//        for (int i = 0; i < 300000; i++)
//        {
//            Dict.put(nodes.get(i), nodes.get(i));
//        }
//        System.out.println("结束时间Map:" + (System.currentTimeMillis() - stratTime));
//
//        stratTime = System.currentTimeMillis();
//        System.out.println("查开始时间map:" + stratTime);
//        for (int i = 0; i < 300000; i++)
//        {
//            Dict.get(nodes.get(i));
//        }
//        System.out.println("结束时间map:" + (System.currentTimeMillis() - stratTime));
//
//        System.gc();
//
//        Map<ByteKey, Object> map = new HashMap<>();
//        stratTime = System.currentTimeMillis();
//        System.out.println("存开始时间Dict:" + stratTime);
//        for (int i = 0; i < 300000; i++)
//        {
//            map.put(nodes.get(i),  nodes.get(i));
//        }
//        System.out.println("结束时间Dict:" + (System.currentTimeMillis() - stratTime));
//
//        stratTime = System.currentTimeMillis();
//        System.out.println("查开始时间Dict:" + stratTime);
//        for (int i = 0; i < 300000; i++)
//        {
//            map.get(nodes.get(i));
//        }
//        System.out.println("结束时间Dict:" + (System.currentTimeMillis() - stratTime));
//
//
//
//        for (int i = 0; i < 300000; i++)
//        {
//            Object o = Dict.get(nodes.get(i));
//            Object o1 = map.get(nodes.get(i));
//            if(o==o1)
//            {
//                continue;
//            }else
//            {
//                System.err.println("???");
//            }
//        }
        //  System.out.println(dict.size()+"   "+test.size()+"   "+nodes.size());
//        Dict dict = new Dict(4);
//        Map<Integer,MNode> test=new HashMap<>();
//        for(int i=0;i<10000;i++)
//        {
//            byte[] randoByte = getRandoByte();
//            MutObj put = dict.put(randoByte, new MutObjTest());
//            test.put(HashUtil.fastHash(randoByte),new MNode(randoByte,new MutObjTest(),HashUtil.fastHash(randoByte)));
//        }
//        int size = dict.size();
//        int size1=test.size();
//        for(Map.Entry<Integer,MNode> entry:test.entrySet())
//        {
//            MNode key = entry.getValue();
//            MutObj mutObj = dict.get(key.key);
//            if(mutObj==null)
//            {
//                System.out.println("我的数据呢");
//            }
//        }
//        System.out.println(dict);
    }

    public static void main1(String[] args)
    {
        BigMap bigMap = new BigMap(1500);
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++)
        {
            int i1 = random.nextInt(1000);
            list.add(i1);
            bigMap.put(i1);
        }
        for (int i = 0; i < 1000; i++)
        {
            boolean b = bigMap.containsKey(list.get(i));
            if (!b)
            {
                System.out.println(bigMap);
            }
        }
        for (int i = 0; i < 1000; i++)
        {
            int i1 = random.nextInt(1000, 1500);
            boolean b = bigMap.containsKey(i1);
            if (b)
            {
                System.out.println("?");
            }
        }
    }
}
