package cainsgl.core.command.base.processor;

import cainsgl.core.command.JavaScriptExecute;
import cainsgl.core.command.base.manager.ExecuteManager;
import cainsgl.core.command.processor.CommandProcessor;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.ErrorResponse;

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
        try
        {
            Object eval = JavaScriptExecute.eval(js);
            if (eval != null)
            {
                return new BulkStringResponse(eval.toString());
            }
            return RESP2Response.OK;
        } catch (Exception e)
        {
            MutConfiguration.log.error("执行js命令出错", e);
            return new ErrorResponse(e.getClass().getName(), e.getMessage());
        }
    }
}
