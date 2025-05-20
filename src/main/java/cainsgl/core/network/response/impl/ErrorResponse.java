package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ErrorResponse implements ElementResponse
{
    private final byte[] respBytes;
    private final int len;


    public ErrorResponse(String errorType, String message)
    {
        this(
                errorType.getBytes(StandardCharsets.UTF_8),
                message.getBytes(StandardCharsets.UTF_8)
        );
    }

    public ErrorResponse(byte[] errorTypeBytes, byte[] messageBytes)
    {
        if (errorTypeBytes == null || messageBytes == null)
        {
            throw new IllegalArgumentException("errorType和message的字节数组不能为null");
        }
        int totalLen = 1 + errorTypeBytes.length + 1 + messageBytes.length + 2;
        this.respBytes = new byte[totalLen];
        int offset = 0;
        respBytes[offset++] = '-';
        System.arraycopy(errorTypeBytes, 0, respBytes, offset, errorTypeBytes.length);
        offset += errorTypeBytes.length;
        respBytes[offset++] = ' ';
        System.arraycopy(messageBytes, 0, respBytes, offset, messageBytes.length);
        offset += messageBytes.length;
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
        return respBytes;
    }
}