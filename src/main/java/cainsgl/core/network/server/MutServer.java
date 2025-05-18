package cainsgl.core.network.server;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.excepiton.MutServerStartException;
import cainsgl.core.system.Stopable;
import cainsgl.core.system.thread.ThreadManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MutServer implements Stopable
{
    ChannelFuture channelFuture;
    private MutServer()
    {

    }

    private void init(ClassLoader classLoader) throws Exception
    {
        new CommandConfiguration();
    }
    private void start() throws Exception
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Class<? extends ClassLoader> classLoaderClass = classLoader.getClass();
        if(!classLoaderClass.getName().equals("cainsgl.core.system.loader.MutClassLoader"))
        {
            throw new UnsupportedOperationException("unable to load class by other class loader,only MutClassLoader,error by "+classLoaderClass.getName());
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        try(ThreadManager.SERVER_BOSS_GROUP;ThreadManager.SERVER_WORKER_GROUP)
        {
            bootstrap.group(ThreadManager.SERVER_BOSS_GROUP,ThreadManager.SERVER_WORKER_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MutServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, MutConfiguration.backlog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture= bootstrap.bind(MutConfiguration.port).sync();
            MutConfiguration.log.info("MutServer started in port "+MutConfiguration.port);
            init(classLoader);
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e)
        {
            throw new MutServerStartException(e);
        }
    }

    @Override
    public void stop() throws Exception
    {
        channelFuture.get();
        ThreadManager.stop();
    }
}
