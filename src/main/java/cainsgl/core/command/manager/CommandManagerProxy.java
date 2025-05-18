package cainsgl.core.command.manager;


import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.command.processor.CommandProcessorProxy;

public class CommandManagerProxy implements CommandManager
{

    @SafeVarargs
    public CommandManagerProxy(CommandProcessor<CommandManager>... processors) {
        //注入Manger
        for (CommandProcessor<CommandManager> processor : processors) {
            new CommandProcessorProxy<>(this, processor);
        }
    }
}
