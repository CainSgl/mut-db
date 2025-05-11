package cainsgl.core.structure.dict.entry;

import cainsgl.core.data.MutObj;

import java.util.Arrays;

public class MNode
{
    public byte[] key;
    public MutObj mutObj;
    public int code;
    public MNode(byte[] key, MutObj mutObj, int coed)
    {
        this.key = key;
        this.mutObj = mutObj;
        this.code = coed;
    }

    @Override
    public int hashCode()
    {
        return code;
    }

    @Override
    public boolean equals(Object obj)
    {
        MNode other = (MNode) obj;
        return Arrays.equals(other.key, key);
    }

    @Override
    public String toString()
    {
        return new String(key)+" :"+code;
    }
}
