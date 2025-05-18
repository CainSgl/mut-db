package cainsgl.core.network.response;

public interface ElementResponse extends  RESP2Response
{
    byte[] CRLF={'\r','\n'};
    int len();
    int writeByte(byte[] buf,int off);
}
