package cainsgl.core.command.manager;

import cainsgl.core.network.response.RESP2Response;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;

import java.util.function.Consumer;

public interface Manager
{


    void execute(byte[][] args, Consumer<RESP2Response> task);

}
