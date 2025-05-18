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

import java.util.function.Consumer;

public class MutServerCommandHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if (msg instanceof byte[][] resp)
        {
            //直接解析好了
            CommandProcessor<?> cmd = NetWorkConfig.getCmd(resp[0]);
            if (cmd != null)
            {
                MutConfiguration.log.info("executing command: {}", cmd.commandName());
                byte[][] args = new byte[resp.length - 1][];
                System.arraycopy(resp, 1, args, 0, resp.length - 1);
                if (args.length < cmd.minCount() || args.length > cmd.maxCount())
                {
                    //参数个数不对
                    MutConfiguration.log.warn("参数个数不对");
                    ByteBufAllocator alloc = ctx.alloc();
                    byte[] bytes = new ErrorResponse("ERR","wrong number of arguments").getBytes();
                    ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                    ctx.writeAndFlush(byteBuf);
                } else
                {
                    cmd.submit(args, resp2Response -> {
                        if (resp2Response == null)
                        {
                            resp2Response = RESP2Response.NIL;
                        }
                        ByteBufAllocator alloc = ctx.alloc();
                        byte[] bytes = resp2Response.getBytes();
                        ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                        ctx.writeAndFlush(byteBuf);
                    }, null);
                }
            } else
            {
                //找不到命令
                MutConfiguration.log.warn("找不到命令=>{}", new String(resp[0]));
                ByteBufAllocator alloc = ctx.alloc();
                byte[] bytes = new ErrorResponse("ERR","wrong number of arguments for '"+new String(resp[0])+"'command").getBytes();
                ByteBuf byteBuf = alloc.directBuffer(bytes.length).writeBytes(bytes);
                ctx.writeAndFlush(byteBuf);
            }
            return;
        }


        if (msg instanceof CommandAdapter commandAdapter)
        {
            CommandProcessor<?> executor = commandAdapter.getExecutor();
//            if(executor == null)
//            {
//                //不存在的命令
//
//            }
            byte[][] args = commandAdapter.getArgs();
            if (args.length > executor.maxCount() || args.length < executor.minCount())
            {
                //有异常
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
