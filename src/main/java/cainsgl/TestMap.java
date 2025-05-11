package cainsgl;

import cainsgl.core.data.MutObjTest;
import cainsgl.core.structure.BigMap;
import cainsgl.core.structure.dict.Dict;
import cainsgl.core.structure.dict.entry.MNode;
import cainsgl.core.utils.HashUtil;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class TestMap
{
    public static byte[] getRandoByte() throws UnsupportedEncodingException
    {
        Random rand = new Random();
        String t="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int t2=rand.nextInt(10);
        String s="";
        for(int i=0;i<t2;i++)
        {
            int i2 = rand.nextInt(25);
            s=s+t.substring(i2,i2+1);
        }
        byte[] bytes = s.getBytes("UTF-8");
        if(bytes.length==0)
        {
            return new byte[]{'a','d'};
        }
        return bytes;
    }

    public static void main(String[] args) throws Exception
    {
        Dict dict=new Dict(32);
        Map<Integer,MNode> test=new HashMap<>();
        List<MNode> nodes=new ArrayList<>();
        for(int i=0;i<300000;i++)
        {
            byte[] randoByte = getRandoByte();
            MNode mNode = new MNode(randoByte, new MutObjTest(), HashUtil.fastHash(randoByte));
            nodes.add(mNode);
        }

        long stratTime=System.currentTimeMillis();
        System.out.println("存开始时间Map:"+stratTime);
        for(int i=0;i<300000;i++)
        {
            MNode mNode = nodes.get(i);
            //我们的也会去new一次
            test.put(HashUtil.fastHash(mNode.key),new MNode(mNode.key,mNode.mutObj,mNode.code));
        }
        System.out.println("结束时间Map:"+(System.currentTimeMillis()-stratTime));

        stratTime=System.currentTimeMillis();
        System.out.println("查开始时间map:"+stratTime);
        for(int i=0;i<300000;i++)
        {
            MNode mNode = nodes.get(i);
            test.get(HashUtil.fastHash(mNode.key));
        }
        System.out.println("结束时间map:"+(System.currentTimeMillis()-stratTime));
        System.out.println(test.size());
        test=null;
        System.gc();


        stratTime=System.currentTimeMillis();
        System.out.println("存开始时间Dict:"+stratTime);
        for(int i=0;i<300000;i++)
        {
            MNode mNode = nodes.get(i);
            dict.put(mNode.key,mNode.mutObj);
        }
        System.out.println("结束时间Dict:"+(System.currentTimeMillis()-stratTime));

        stratTime=System.currentTimeMillis();
        System.out.println("查开始时间Dict:"+stratTime);
        for(int i=0;i<300000;i++)
        {
            MNode mNode = nodes.get(i);
            dict.get(mNode.key);
        }
        System.out.println("结束时间Dict:"+(System.currentTimeMillis()-stratTime));
        System.out.println(dict.size());
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
        BigMap bigMap=new BigMap(1500);
        List<Integer> list=new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<1000;i++)
        {
            int i1 = random.nextInt(1000);
            list.add(i1);
            bigMap.put(i1);
        }
        for(int i=0;i<1000;i++)
        {
            boolean b = bigMap.containsKey(list.get(i));
            if(!b)
            {
                System.out.println(bigMap);
            }
        }
        for(int i=0;i<1000;i++)
        {
            int i1 = random.nextInt(1000,1500);
            boolean b = bigMap.containsKey(i1);
            if(b)
            {
                System.out.println("?");
            }
        }
    }
}
