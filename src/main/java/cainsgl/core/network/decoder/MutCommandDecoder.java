package cainsgl.core.network.decoder;

import cainsgl.core.command.manager.Manager;
import cainsgl.core.excepiton.MutDecoderException;
import cainsgl.core.utils.adapter.CommandAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MutCommandDecoder extends ByteToMessageDecoder
{
    private CommandAdapter cmdAdapter;
    private byte[] cache;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception
    {
        int len = in.readableBytes();
        if (len == 0)
        {
            return;
        }
        if (cache == null)
        {
            cache = new byte[len];
            in.readBytes(cache);
        } else
        {
            int oldCacheLen = cache.length;
            byte[] newCache = new byte[oldCacheLen + len];
            System.arraycopy(cache, 0, newCache, 0, oldCacheLen);
            in.readBytes(newCache, oldCacheLen, len);
            cache = newCache;
        }
        if (cmdAdapter == null)
        {
            if(cache[0]=='\n'||cache[0]=='\r')
            {
                cache=null;
                return;
            }
            if (cache[0] != '*')
            {
                throw new MutDecoderException("请求体的开头不为*->" +cache[0] );
            }
            int i = 1;
            int dataNum = 0;
            while (i+1 < cache.length)
            {
                if (cache[i] == '\r' && cache[i + 1] == '\n')
                {
                    cmdAdapter = new CommandAdapter(dataNum,i+2);
                    break;
                }
                dataNum = cache[i] - '0' + dataNum * 10;
                i++;
            }

        }
        if (cmdAdapter != null)
        {
            if(cmdAdapter.decode(cache))
            {
                list.add(cmdAdapter);
                cache=null;
                cmdAdapter=null;
            }
        }
    }


    public void reset()
    {
        cache = null;
        cmdAdapter = null;
    }
}
