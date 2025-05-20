package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RespUtils;

import java.util.Arrays;

public class NumberResponse implements ElementResponse
{
    private final byte[] respBytes;
    private final int len;

    // 构造 long 类型数字响应（如 ":1234567890123\r\n" 或 ":-1234567890123\r\n"）
    public NumberResponse(long number) {
        // 计算包含符号的数字字节长度（符号占1位，数字占实际位数）
        int numDigitCount = RespUtils.getDigitCount(number);
        if (number < 0) {
            numDigitCount += 1; // 负数额外增加符号位长度
        }

        // 总长度 = 1（':'） + 数字位数（含符号） + 2（'\r\n'）
        int totalLen = 1 + numDigitCount + 2;
        this.respBytes = new byte[totalLen];

        int offset = 0;
        respBytes[offset++] = ':';  // 协议标识

        // 将 long 数字转换为 ASCII 字节并填充（包含符号）
        byte[] numBytes = new byte[numDigitCount];
        RespUtils.writeIntAsAscii(number, numBytes, 0); // 调用 long 版本转换方法
        System.arraycopy(numBytes, 0, respBytes, offset, numDigitCount);
        offset += numDigitCount;

        respBytes[offset++] = '\r';  // 结束符
        respBytes[offset] = '\n';

        this.len = respBytes.length;
    }

    public NumberResponse(int number)
    {
       this((long)number);
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
