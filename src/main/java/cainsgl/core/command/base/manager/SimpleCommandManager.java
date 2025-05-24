package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.simple.*;
import cainsgl.core.command.manager.ShareThreadManager;

public class SimpleCommandManager extends ShareThreadManager
{
    public SimpleCommandManager()
    {
        super(new CommandProcessor2(), new PingProcessor(), new HelloWorldProcessor(), new ScanProcessor(), new GcProcessor(), new DbSizeProcessor(), new InfoProcessor(), new TypeProcessor(),new SaveProcessor(),new ZipProcessor());
    }


}
