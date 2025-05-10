package cainsgl.core.system;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ThreadManager
{
    public static final EventLoopGroup bossGroup;

    static
    {
        bossGroup=new NioEventLoopGroup();
    }
}
