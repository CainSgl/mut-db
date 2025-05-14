package cainsgl.core.network.server;

import cainsgl.core.network.decoder.MutCommandDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MutServerInitializer extends ChannelInitializer<SocketChannel>
{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
        ChannelPipeline pipeline = socketChannel.pipeline();
        MutCommandDecoder mutCommandDecoder = new MutCommandDecoder();
        pipeline.addLast(mutCommandDecoder);
        pipeline.addLast(new MutGlobalExceptionHandler(mutCommandDecoder));
    }


}
