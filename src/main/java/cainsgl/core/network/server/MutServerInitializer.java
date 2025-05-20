package cainsgl.core.network.server;

import cainsgl.core.network.decoder.RESP2Decoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MutServerInitializer extends ChannelInitializer<SocketChannel>
{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new RESP2Decoder());
        pipeline.addLast(new MutServerCommandHandler());
        pipeline.addLast(new MutGlobalExceptionHandler());
    }


}
