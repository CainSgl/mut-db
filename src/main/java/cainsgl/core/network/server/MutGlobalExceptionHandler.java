package cainsgl.core.network.server;

import cainsgl.core.config.MutConfiguration;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;

import java.net.SocketException;

public class MutGlobalExceptionHandler extends ChannelDuplexHandler
{

    public MutGlobalExceptionHandler()
    {

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof DecoderException DecoderEx)
        {
            Throwable cause1 = DecoderEx.getCause();
            String errorMsg=cause1.getMessage();
            MutConfiguration.log.warn(errorMsg);
            ctx.writeAndFlush(Unpooled.copiedBuffer("-"+errorMsg+ "\r\n", CharsetUtil.UTF_8));
        }else
        {
            if(cause instanceof SocketException)
            {
                return;
            }
            MutConfiguration.log.error("出乎意料的异常",cause);
        }
    }
}
