package cainsgl;

import cainsgl.core.data.MutObj;
import cainsgl.core.data.MutObjTest;
import cainsgl.core.structure.dict.entry.MNode;
import cainsgl.core.structure.dict.entry.SortedList;
import cainsgl.core.structure.dict.entry.SortedSet;
import cainsgl.core.utils.HashUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestMainn
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
        return s.getBytes("UTF-8");
    }


    public static void main(String[] args) throws Exception
    {
        //     MutServerBuilder.build().start();
        SortedList list=new SortedSet(4);
        List<MNode> nodes=new ArrayList<MNode>();

        for(int i=0;i<100;i++)
        {
            byte[] randoByte = getRandoByte();
            MNode mNode = new MNode(randoByte, new MutObjTest(), HashUtil.fastHash(randoByte));
            list.add(mNode);
            nodes.add(mNode);
        }
        for(int i=0;i<nodes.size();i++)
        {
            MNode mNode = nodes.get(i);
            MutObj mNode1 = list.getObj(mNode.key, mNode.code);
            if(mNode1==mNode.mutObj)
            {
                System.out.println(mNode);
            }else
            {
                System.err.println("???");
            }
        }

    }
}
