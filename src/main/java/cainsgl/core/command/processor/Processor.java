package cainsgl.core.command.processor;


import cainsgl.core.network.response.RESP2Response;

public interface Processor
{
    RESP2Response execute();

    void processArgs(byte[] args);

    int minCount();

    int maxCount();
}
