package cainsgl.core.command.base.manager;

import cainsgl.core.command.base.processor.JsProcessor;
import cainsgl.core.command.manager.ExclusiveThreadManager;

public class ExecuteManager extends ExclusiveThreadManager
{
    public ExecuteManager()
    {
        super(new JsProcessor());
    }
}
