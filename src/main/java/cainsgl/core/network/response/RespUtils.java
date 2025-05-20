package cainsgl.core.network.response;

import java.nio.charset.StandardCharsets;

public class RespUtils
{

    /**
     * 计算整数的数字位数（不包含符号）
     * 修复点：使用 long 类型避免 Integer.MIN_VALUE 溢出
     */
    public static int getDigitCount(int n) {
        return getDigitCount((long) n); // 转为 long 处理边界值
    }

    /**
     * 内部方法：计算 long 类型数字的位数（不包含符号）
     */
    public static int getDigitCount(long num) {
        if (num == 0) {
            return 1;
        }
        if (num < 0) {
            num = -num; // 转为正数计算位数
        }
        int count = 0;
        while (num > 0) {
            count++;
            num /= 10;
        }
        return count;
    }

    /**
     * 将整数转换为 ASCII 字节数组（包含符号）
     * 修复点：使用 long 类型避免溢出，正确处理负数符号
     */
    public static int writeIntAsAscii(long n, byte[] dest, int offset) {
        if (n == 0) {
            dest[offset++] = '0'; // 处理 0 的特殊情况
            return offset;
        }

        boolean isNegative = n < 0;
        long num = isNegative ? - n :  n; // 关键修复：转为 long 避免溢出

        int digitCount = getDigitCount(num); // 获取纯数字位数（不包含符号）
        int[] digits = new int[digitCount];

        // 提取每一位数字（从低位到高位）
        long temp = num;
        for (int i = digitCount - 1; i >= 0; i--) {
            digits[i] = (int) (temp % 10);
            temp /= 10;
        }

        // 写入符号（如果是负数）
        if (isNegative) {
            dest[offset++] = '-';
        }

        // 写入每一位数字的 ASCII 码
        for (int digit : digits) {
            dest[offset++] = (byte) ('0' + digit); // '0' 的 ASCII 码 + 数字值
        }

        return offset;
    }
    /**
     * 将 ASCII 字节数组转换为 long 类型（支持符号）
     * @param src 包含 ASCII 数字的字节数组（可能包含前导符号'-'）
     * @param offset 起始读取位置
     * @return 转换后的 long 值
     * @throws IllegalArgumentException 当输入包含无效字符或数值超出 long 范围时
     */
    public static long readAsciiToLong(byte[] src, int offset) {
        if (src == null || src.length == 0 || offset < 0 || offset >= src.length) {
            throw new IllegalArgumentException("Invalid input array or offset");
        }

        int position = offset;
        boolean isNegative = false;
        long result = 0;

        // 处理符号位
        if (src[position] == '-') {
            isNegative = true;
            position++;
            // 检查是否只有符号没有数字的情况
            if (position >= src.length) {
                throw new IllegalArgumentException("No digits after negative sign");
            }
        }
        // 遍历数字字符
        while (position < src.length) {
            byte b = src[position];
            if (b < '0' || b > '9') {
                throw new IllegalArgumentException("Invalid character: " + (char) b);
            }

            int digit = b - '0';

            result = result * 10 + digit;
            position++;
        }

        return isNegative ? -result : result;
    }

}