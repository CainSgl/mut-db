package cainsgl.core.network.server;

import cainsgl.core.network.decoder.MutCommandDecoder;
import cainsgl.core.system.Stopable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

public class MutServerInitializer extends ChannelInitializer<SocketChannel>
{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
        ChannelPipeline pipeline = socketChannel.pipeline();
        MutCommandDecoder mutCommandDecoder = new MutCommandDecoder();
        pipeline.addLast(mutCommandDecoder);
        pipeline.addLast();
        pipeline.addLast(new MutGlobalExceptionHandler(mutCommandDecoder));
    }


}
