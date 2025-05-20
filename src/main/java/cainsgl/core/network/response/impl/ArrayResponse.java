package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RespUtils;

import java.util.List;

public class ArrayResponse implements ElementResponse
{
    byte[] data;
    int len;
    private static final byte separator = '*';

    public ArrayResponse(ElementResponse... elements)
    {
        int elementCount = elements.length;
        int totalHeaderLen = 1 + RespUtils.getDigitCount(elementCount) + 2;
        int totalElementLen = 0;
        for (ElementResponse element : elements)
        {
            totalElementLen += element.len();
        }
        this.data = new byte[totalHeaderLen + totalElementLen];
        int offset = writeHeader(elementCount, data, 0);
        for (ElementResponse element : elements)
        {
            offset = element.writeByte(data, offset);
        }
        len = offset;
    }

    public ArrayResponse(List<ElementResponse> elements)
    {
        ElementResponse[] array = elements.toArray(new ElementResponse[0]);
        this(array);
    }
    private int writeHeader(int elementCount, byte[] dest, int offset)
    {
        dest[offset++] = '*';
        offset = RespUtils.writeIntAsAscii(elementCount, dest, offset);
        dest[offset++] = '\r';
        dest[offset++] = '\n';
        return offset;
    }

    @Override
    public int len()
    {
        return len;
    }

    @Override
    public int writeByte(byte[] buf, int off)
    {
        System.arraycopy(data, 0, buf, off, len);
        return off + len;
    }

    @Override
    public byte[] getBytes()
    {
        return data;
    }
}
