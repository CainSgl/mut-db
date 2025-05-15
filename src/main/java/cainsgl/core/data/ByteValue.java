package cainsgl.core.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteValue
{
    byte[] key;

    public ByteValue(byte[] key)
    {
        this.key = key;

    }

    @Override
    public String toString()
    {
        return new String(key, StandardCharsets.UTF_8) ;
    }

    @Override
    public int hashCode()
    {
       throw new UnsupportedOperationException("不支持value计算hashcode，请使用ByteKey");
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {return false;}
        if (obj == this) {return true;}
        if (obj instanceof ByteKey other) {return Arrays.equals(key, other.key);}
        return false;
    }
}
