package cainsgl.core.command.base.processor.simple;

import cainsgl.core.command.base.manager.SimpleCommandManager;
import cainsgl.core.data.key.ByteFastIgnoreCaseKey;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandProcessor2 extends cainsgl.core.command.processor.CommandProcessor<SimpleCommandManager>
{
    public CommandProcessor2()
    {
        super(0, 0, "COMMAND", List.of("null"));
    }

    @Override
    public RESP2Response execute(byte[][] args, SimpleCommandManager manager)
    {
        Map<ByteFastIgnoreCaseKey, cainsgl.core.command.processor.CommandProcessor<?>> allCommand = NetWorkConfig.getAllCommand();
        List<ElementResponse> elementResponses = new ArrayList<>();
        for (ByteFastIgnoreCaseKey key : allCommand.keySet())
        {
            cainsgl.core.command.processor.CommandProcessor<?> processor = allCommand.get(key);
            List<String> parameters = processor.parameters();
            if (parameters == null || parameters.isEmpty())
            {
                elementResponses.add(new BulkStringResponse(processor.commandName()));
            } else
            {
                List<ElementResponse> describe = new ArrayList<>();
                describe.add(new BulkStringResponse(processor.commandName()));
                for (String parameter : parameters)
                {
                    describe.add(new BulkStringResponse(parameter));
                }
                elementResponses.add(new ArrayResponse(describe));
            }
        }
        return new ArrayResponse(elementResponses);
    }
}
