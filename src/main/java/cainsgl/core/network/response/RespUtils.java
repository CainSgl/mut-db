package cainsgl.core.network.response;

import java.nio.charset.StandardCharsets;

public class RespUtils
{

    public static int getDigitCount(int n)
    {
        if (n == 0) {return 1;}
        if (n < 0)
        {
            n = -n;
        }
        int count = 0;
        while (n > 0)
        {
            count++;
            n /= 10;
        }
        return count;
    }
    public static int writeIntAsAscii(int n, byte[] dest, int offset)
    {
        if (n == 0)
        {
            dest[offset++] = '0';
            return offset;
        }

        boolean isNegative = n < 0;
        if (isNegative) {n = -n;}

        int temp = n;
        int digitCount = getDigitCount(n);
        int[] digits = new int[digitCount];
        for (int i = digitCount - 1; i >= 0; i--)
        {
            digits[i] = temp % 10;
            temp /= 10;
        }
        if (isNegative)
        {
            dest[offset++] = '-';
        }
        for (int digit : digits)
        {
            dest[offset++] = (byte) ('0' + digit);
        }
        return offset;
    }
}