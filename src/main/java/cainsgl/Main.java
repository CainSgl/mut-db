package cainsgl;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.ConfigLoader;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.server.MutServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        MutServerBuilder.build().start();
    }

}
