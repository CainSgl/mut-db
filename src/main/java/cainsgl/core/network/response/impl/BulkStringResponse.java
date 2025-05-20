package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RespUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BulkStringResponse implements ElementResponse
{
    private final byte[] respBytes;
    private final int len;
    private final boolean isNull;

    public BulkStringResponse(String content)
    {
        this(content != null ? content.getBytes(StandardCharsets.UTF_8) : null);
    }

    public BulkStringResponse(byte[] contentBytes)
    {
        this(contentBytes, false);
    }

    public BulkStringResponse()
    {
        this(new byte[0], false);
    }

    public BulkStringResponse(boolean isNull)
    {
        this(null, isNull);
    }

    private BulkStringResponse(byte[] contentBytes, boolean isNull)
    {
        this.isNull = isNull;
        if (isNull)
        {
            this.respBytes = new byte[]{'$', '-', '1', '\r', '\n'};
            this.len = respBytes.length;
            return;
        }
        if (contentBytes == null)
        {
            contentBytes = new byte[0];
        }
        int contentLength = contentBytes.length;
        int lengthDigitCount = RespUtils.getDigitCount(contentLength);
        byte[] lengthBytes = new byte[lengthDigitCount];
        RespUtils.writeIntAsAscii(contentLength, lengthBytes, 0);
        int totalLen = 1 + lengthDigitCount + 2 + contentBytes.length + 2;
        this.respBytes = new byte[totalLen];
        int offset = 0;
        respBytes[offset++] = '$';
        System.arraycopy(lengthBytes, 0, respBytes, offset, lengthDigitCount);
        offset += lengthDigitCount;
        respBytes[offset++] = '\r';
        respBytes[offset++] = '\n';
        System.arraycopy(contentBytes, 0, respBytes, offset, contentBytes.length);
        offset += contentBytes.length;
        respBytes[offset++] = '\r';
        respBytes[offset] = '\n';
        this.len = respBytes.length;
    }

    @Override
    public int len()
    {
        return len;
    }

    @Override
    public int writeByte(byte[] buf, int off)
    {
        System.arraycopy(respBytes, 0, buf, off, len);
        return off + len;
    }

    @Override
    public byte[] getBytes()
    {
        return Arrays.copyOf(respBytes, respBytes.length);
    }

    public boolean isNull()
    {
        return isNull;
    }
}