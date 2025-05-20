package cainsgl.core.command.manager;


import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.command.processor.CommandProcessorProxy;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.persistence.MutSerializer;

public  class CommandManagerProxy implements CommandManager
{

    @SafeVarargs
    public CommandManagerProxy(CommandProcessor<CommandManager>... processors)
    {
        //注入Manger
        for (CommandProcessor<CommandManager> processor : processors)
        {
            new CommandProcessorProxy<>(this, processor);
        }
        CommandConfiguration.register(this);
    }

    @Override
    public void exceptionCaught(Exception e)
    {
        MutConfiguration.log.error("未捕获的异常",e);
    }
}
