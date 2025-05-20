package cainsgl.core.command.base.processor;

import cainsgl.core.command.base.manager.ExecuteManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.network.response.RESP2Response;

import java.util.List;

public class JsProcessor extends CommandProcessor<ExecuteManager>
{
    public JsProcessor()
    {
        super(1, 1, "js", List.of("command"));
    }

    @Override
    public RESP2Response execute(byte[][] args, ExecuteManager manager)
    {
        String js = new String(args[0]);
        return null;
    }
}
