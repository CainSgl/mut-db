package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RespUtils;

import java.util.Arrays;

public class NumberResponse implements ElementResponse
{
    private final byte[] respBytes;
    private final int len;

    // 构造普通数字响应（如 ":123\r\n"）
    public NumberResponse(int number)
    {
        // 计算数字的 ASCII 字节长度（含符号）
        int numDigitCount = RespUtils.getDigitCount(number);
        // 总长度 = 1（':'） + 数字位数 + 2（'\r\n'）
        int totalLen = 1 + numDigitCount + 2;
        this.respBytes = new byte[totalLen];

        int offset = 0;
        respBytes[offset++] = ':';  // 协议标识

        // 将数字转换为 ASCII 字节并填充
        byte[] numBytes = new byte[numDigitCount];
        RespUtils.writeIntAsAscii(number, numBytes, 0);  // 假设 RespUtils 支持 long 转换（需补充该方法）
        System.arraycopy(numBytes, 0, respBytes, offset, numDigitCount);
        offset += numDigitCount;

        respBytes[offset++] = '\r';  // 结束符
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

    // 可选：如果需要支持 "空数字"（虽然 RESP2 标准中数字通常不为空，可根据业务扩展）
    public static NumberResponse nullResponse()
    {
        return new NumberResponse(new byte[]{':', '-', '1', '\r', '\n'}, 5);
    }

    private NumberResponse(byte[] respBytes, int len)
    {
        this.respBytes = respBytes;
        this.len = len;
    }
}
