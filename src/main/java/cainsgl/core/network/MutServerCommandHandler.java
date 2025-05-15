package cainsgl.core.network;

import cainsgl.core.command.manager.Manager;
import cainsgl.core.utils.adapter.CommandAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MutServerCommandHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if(msg instanceof CommandAdapter commandAdapter)
        {
            Manager cmd = commandAdapter.getCmd();
            byte[][] args = commandAdapter.getArgs();
            cmd.execute(args,(res)->{
                ctx.writeAndFlush(res.getBytes());
            });
        }
    }
}
