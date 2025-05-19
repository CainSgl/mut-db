package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.simple.CommandProcessor2;
import cainsgl.core.command.base.processor.simple.PingProcessor;
import cainsgl.core.command.manager.ShareThreadManager;

public class SimpleCommandManager extends ShareThreadManager
{
    public SimpleCommandManager()
    {
        super(new CommandProcessor2(),new PingProcessor());
    }
}
