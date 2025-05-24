package cainsgl.core.network.server;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.excepiton.MutServerStartException;
import cainsgl.core.storge.aof.AofFileExecutor;
import cainsgl.core.storge.config.StorgeConfiguration;
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

    private void init(long startTime) throws Exception
    {
        new StorgeConfiguration();
        new CommandConfiguration();
        //在这里去执行所有的aof的内容
        AofFileExecutor aofFileExecutor = new AofFileExecutor();
        aofFileExecutor.execute();



        System.out.println("____                                                                                      ");
        System.out.println("        ,'  , `.                 ___      .--.--.                                                  ");
        System.out.println("     ,-+-,.' _ |               ,--.'|_   /  /    '.                                                ");
        System.out.println("  ,-+-. ;   , ||         ,--,  |  | :,' |  :  /`. /             __  ,-.                    __  ,-. ");
        System.out.println(" ,--.'|'   |  ;|       ,'_ /|  :  : ' : ;  |  |--`            ,' ,'/ /|    .---.         ,' ,'/ /| ");
        System.out.println("|   |  ,', |  ':  .--. |  | :.;__,'  /  |  :  ;_       ,---.  '  | |' |  /.  ./|  ,---.  '  | |' | ");
        System.out.println("|   | /  | |  ||,'_ /| :  . ||  |   |    \\  \\    `.   /     \\ |  |   ,'.-' . ' | /     \\ |  |   ,' ");
        System.out.println("'   | :  | :  |,|  ' | |  . .:__,'| :     `----.   \\/    /  |'  :  / /___/ \\: |/    /  |'  :  /   ");
        System.out.println(";   . |  ; |--' |  | ' |  | |  '  : |__   __ \\  \\  |.    ' / ||  | '  .   \\  ' .    ' / ||  | '    ");
        System.out.println("|   : |  | ,    :  | : ;  ; |  |  | '.'| /  /`--'  /'   ;   /|;  : |   \\   \\   '   ;   /|;  : |    ");
        System.out.println("|   : '  |/     '  :  `--'   \\ ;  :    ;'--'.     / '   |  / ||  , ;    \\   \\  '   |  / ||  , ;    ");
        System.out.println(";   | |`-'      :  ,      .-./ |  ,   /   `--'---'  |   :    | ---'      \\   \\ |   :    | ---'     ");
        System.out.println("|   ;/           `--`----'      ---`-'               \\   \\  /             '---\" \\   \\  /           ");
        System.out.println("'---'                                                 `----'                     `----'            ");
        System.out.println("MutServer started in port:"+MutConfiguration.PORT);
        System.out.println("Server start cost time:" +(System.currentTimeMillis()-startTime)/1000 +"s");
    }

    private void start() throws Exception
    {
        long startTime=System.currentTimeMillis();
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
                    .option(ChannelOption.SO_BACKLOG, MutConfiguration.BACKLOG)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
         //   System.out.println("当前MutConfig类加载器: " + MutConfiguration.class.getClassLoader());
            channelFuture= bootstrap.bind(MutConfiguration.PORT).sync();
          //  MutConfiguration.log.info("MutServer started in port "+MutConfiguration.PORT+" startTime:"+   GcSystem.SERVER_START_TIME);
            init(startTime);
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
