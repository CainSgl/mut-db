package cainsgl.core.command.manager;

import cainsgl.core.command.processor.Processor;
import cainsgl.core.network.config.NetWorkConfig;
import cainsgl.core.network.response.RESP2Response;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCommandManager implements Manager, Shunt
{
    List<CommandAdaptor> CommandAdaptors;
    public AbstractCommandManager()
    {
        CommandAdaptors=new ArrayList<CommandAdaptor>();
    }

    public abstract class CommandAdaptor implements Processor
    {
        private static final int HASH_INCREMENT = 1640531527;
        private static int nextId;
        int id;
      //  Processor processor;

        public CommandAdaptor(String commandName)
        {
            NetWorkConfig.register(commandName, this);
            nextId += HASH_INCREMENT;
            this.id = nextId;

           // this.processor = processor;
        }
    }

    @Override
    public void execute(byte[][] args, Consumer<RESP2Response> task)
    {

    }
}
