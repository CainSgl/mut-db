package cainsgl;

import cainsgl.core.network.server.MutServer;
import cainsgl.core.network.server.MutServerBuilder;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MutServerBuilder.build().start();
    }
}
