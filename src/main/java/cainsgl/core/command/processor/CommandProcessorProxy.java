package cainsgl.core.command.processor;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.storge.aof.AofBuffer;

import java.util.function.Consumer;

public class CommandProcessorProxy<T> extends CommandProcessor<T>
{
    final T commandManager;
    final CommandProcessor<T> proxy;
    final AofBuffer aofBuffer;
    public CommandProcessorProxy(T commandManager, CommandProcessor<T> commandProcessor)
    {
        AofBuffer aofBuffer1=null;
        super(commandProcessor.minCount, commandProcessor.maxCount, commandProcessor.commandName, commandProcessor.parameters);
        this.commandManager = commandManager;
        this.proxy = commandProcessor;
        if(proxy.aof)
        {
            try{
                aofBuffer1 = new AofBuffer(commandProcessor.commandName);
            }catch (Exception e) {
                MutConfiguration.log.error("Could not create aofBuffer for command {}" , commandProcessor.commandName, e);
            }
        }
        aofBuffer = aofBuffer1;
        NetWorkConfig.register(commandProcessor.commandName, this);
    }

    @Override
    public boolean getAof()
    {
        return proxy.getAof();
    }

    public RESP2Response execute(byte[][] args, T manager)
    {
        //拿到自己的aof然后去执行一下
        return proxy.execute(args, commandManager);
    }

    @Override
    public void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager)
    {
        proxy.submit(args, consumer, commandManager);
        if(proxy.aof)
        {
            aof(args);
        }
    }

//    private static interface Submitable<T>{
//        void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager);
//    }
//
//    private static class AofSubmit<T> implements Submitable<T>
//    {
//         public void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager){
//
//        }
//    }
//    private static class DefaultSubmit<T> implements Submitable<T>
//    {
//        public void submit(byte[][] args, Consumer<RESP2Response> consumer, T manager){
//            proxy.submit(args, consumer, commandManager);
//        }
//    }

    @Override
    public void aof(byte[][] args)
    {
        aofBuffer.write(args);
    }
}
