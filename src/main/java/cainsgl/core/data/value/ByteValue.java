package cainsgl.core.data.value;

import cainsgl.core.data.AbstractByteObj;

import java.nio.charset.StandardCharsets;

public class ByteValue extends AbstractByteObj
{
    public final static int uuid=2;
    byte[] value;

    public ByteValue(byte[] key)
    {
        this.value = key;

    }

    @Override
    public String toString()
    {
        return new String(value, StandardCharsets.UTF_8) ;
    }

    @Override
    public int hashCode()
    {
       throw new UnsupportedOperationException("不支持value计算hashcode，请使用ByteKey");
    }

    @Override
    public byte[] getBytes()
    {
        return value;
    }
}
