package cainsgl.core.network.response;

import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.NumberResponse;
import cainsgl.core.network.response.impl.SimpleStringResponse;

public interface RESP2Response
{
    BulkStringResponse NIL = new BulkStringResponse(true);
    SimpleStringResponse OK = new SimpleStringResponse("OK");
    NumberResponse NONE=NumberResponse.nullResponse();
    NumberResponse NONE2=NumberResponse.null2Response();
    byte[] getBytes();

}
