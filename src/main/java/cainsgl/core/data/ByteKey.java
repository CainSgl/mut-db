package cainsgl.core.data;

import cainsgl.core.utils.HashUtil;

import java.nio.charset.StandardCharsets;

public class ByteKey
{
    byte[] key;
    int hashCode;

    public ByteKey(byte[] key)
    {
        this.key = key;
        hashCode = HashUtil.fastHash(key);
    }

    @Override
    public String toString()
    {
        return new String(key, StandardCharsets.UTF_8) + ":" + hashCode;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof ByteKey other)
        {
            if (other.key == this.key) {return true;}
            if(other.key.length != this.key.length) {return false;}
            for(int i = 0; i < this.key.length; i++)
            {
                if(other.key[i] != this.key[i])
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
