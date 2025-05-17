package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;

import java.nio.charset.StandardCharsets;

public class SimpleStringResponse implements ElementResponse
{
    private final byte[] respBytes;
    private final int len;

    public SimpleStringResponse(String content)
    {
        this(content.getBytes(StandardCharsets.UTF_8));
    }

    public SimpleStringResponse(byte[] contentBytes)
    {
        this.respBytes = new byte[1 + contentBytes.length + 2];
        int offset = 0;
        respBytes[offset++] = '+';
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
        return respBytes;
    }
}