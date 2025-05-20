package cainsgl.core.network.server;


import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ErrorResponse;
import cainsgl.core.utils.adapter.CommandAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MutServerCommandHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if (msg instanceof byte[][] resp)
        {
            CommandProcessor<?> cmd = NetWorkConfig.getCmd(resp[0]);
            if (cmd == null)
            {
                MutConfiguration.log.warn("找不到命令=>{}", new String(resp[0]));
                ByteBufAllocator alloc = ctx.alloc();
                byte[] bytes;
                if (resp.length > 1)
                {
                    StringBuilder sb = new StringBuilder(resp.length - 1);
                    for(int i = 1; i < resp.length; i++)
                    {
                        sb.append("`").append(new String(resp[1])).append("`").append(",");
                    }
                    bytes = new ErrorResponse("ERR", "unknown command `" + new String(resp[0]) + "`, with args beginning with:"+ sb).getBytes();
                } else
                {
                    bytes = new ErrorResponse("ERR", "unknown command `" + new String(resp[0]) + ", with args beginning with:").getBytes();
                }
                ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                ctx.writeAndFlush(byteBuf);
                return;
            }
            byte[][] args = new byte[resp.length - 1][];
            System.arraycopy(resp, 1, args, 0, resp.length - 1);
            if (args.length < cmd.minCount() || args.length > cmd.maxCount())
            {
                //参数个数不对
                MutConfiguration.log.warn("参数个数不对,命令:{}，个数:{}",new String(resp[0]),args.length);
                ByteBufAllocator alloc = ctx.alloc();
                byte[] bytes = new ErrorResponse("ERR", "wrong number of arguments").getBytes();
                ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                ctx.writeAndFlush(byteBuf);
            } else
            {
                MutConfiguration.log.info("executing command: {}", cmd.commandName());
                cmd.submit(args, resp2Response -> {
                    try{
                        if (resp2Response == null)
                        {
                            resp2Response = RESP2Response.NIL;
                        }
                        ByteBufAllocator alloc = ctx.alloc();
                        byte[] bytes = resp2Response.getBytes();
                        ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                        ctx.writeAndFlush(byteBuf);
                    }catch (Exception e)
                    {
                        MutConfiguration.log.error("序列化出错",e);
                    }
                }, null);
            }
            return;
        }
        if (msg instanceof CommandAdapter commandAdapter)
        {
            CommandProcessor<?> executor = commandAdapter.getExecutor();
            byte[][] args = commandAdapter.getArgs();
            if (args.length > executor.maxCount() || args.length < executor.minCount())
            {
                return;
            }
            executor.submit(args, resp2Response -> {
                ByteBufAllocator alloc = ctx.alloc();
                byte[] bytes = resp2Response.getBytes();
                ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                ctx.writeAndFlush(byteBuf);
            }, null);
        }
    }
}
