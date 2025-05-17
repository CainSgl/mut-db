package cainsgl.core.network.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

public class RESP2Decoder extends ByteToMessageDecoder
{
    // 解析状态
    private enum State
    {
        PARSE_ARRAY_HEADER,
        PARSE_BULK_HEADER,
        PARSE_BULK_CONTENT
    }

    private State state = State.PARSE_ARRAY_HEADER;
    private int arrayLength = -1;
    private int bulkLength = -1;
    private List<byte[]> results;
    private int currentElementIndex = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        while (true)
        {
            switch (state)
            {
                case PARSE_ARRAY_HEADER:
                    if (!parseArrayHeader(in)) {return;}
                    break;
                case PARSE_BULK_HEADER:
                    if (!parseBulkHeader(in)) {return;}
                    break;
                case PARSE_BULK_CONTENT:
                    if (!parseBulkContent(in, out)) {return;}
                    break;
            }
        }
    }

    private boolean parseArrayHeader(ByteBuf in)
    {
        if (in.readableBytes() < 3)
        {
            return false; // 至少需要 "*1\r\n"
        }

        int startIdx = in.readerIndex();
        if (in.getByte(startIdx) != '*')
        {
            throw new IllegalArgumentException("Invalid RESP array header");
        }

        int crlfPos = findCrlf(in, startIdx + 1);
        if (crlfPos == -1) {return false;}

        // 解析数组长度
        arrayLength = readInt(in, startIdx + 1, crlfPos);
        in.readerIndex(crlfPos + 2);
        results = new ArrayList<>(arrayLength);
        state = State.PARSE_BULK_HEADER;
        return true;
    }

    private boolean parseBulkHeader(ByteBuf in)
    {
        if (in.readableBytes() < 3)
        {
            return false; // 至少需要 "$1\r\n"
        }

        int startIdx = in.readerIndex();
        if (in.getByte(startIdx) != '$')
        {
            throw new IllegalArgumentException("Invalid RESP bulk header");
        }

        int crlfPos = findCrlf(in, startIdx + 1);
        if (crlfPos == -1) {return false;}

        bulkLength = readInt(in, startIdx + 1, crlfPos);
        in.readerIndex(crlfPos + 2);
        state = State.PARSE_BULK_CONTENT;
        return true;
    }

    private boolean parseBulkContent(ByteBuf in, List<Object> out)
    {
        // 需要内容长度 + 2字节的CRLF
        if (in.readableBytes() < bulkLength + 2) {return false;}

        int startIdx = in.readerIndex();
        // 验证CRLF
        if (in.getByte(startIdx + bulkLength) != '\r' ||
                in.getByte(startIdx + bulkLength + 1) != '\n')
        {
            throw new IllegalArgumentException("Invalid bulk content ending");
        }

        byte[] content = new byte[bulkLength];
        in.readBytes(content);
        in.skipBytes(2); // 跳过CRLF

        results.add(content);
        currentElementIndex++;

        if (currentElementIndex < arrayLength)
        {
            state = State.PARSE_BULK_HEADER;
        } else
        {
            // 完成数组解析
            byte[][] resultArray = results.toArray(new byte[0][]);
            out.add(resultArray);
            reset();
        }
        return true;
    }

    private int findCrlf(ByteBuf in, int fromIndex)
    {
        int end = in.writerIndex();
        for (int i = fromIndex; i < end - 1; i++)
        {
            if (in.getByte(i) == '\r' && in.getByte(i + 1) == '\n')
            {
                return i;
            }
        }
        return -1;
    }

    private int readInt(ByteBuf in, int start, int end)
    {
        int result = 0;
        for (int i = start; i < end; i++)
        {
            byte b = in.getByte(i);
            if (b < '0' || b > '9')
            {
                throw new IllegalArgumentException("Invalid integer format");
            }
            result = result * 10 + (b - '0');
        }
        return result;
    }

    private void reset()
    {
        state = State.PARSE_ARRAY_HEADER;
        arrayLength = -1;
        bulkLength = -1;
        results = null;
        currentElementIndex = 0;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}