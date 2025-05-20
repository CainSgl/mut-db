package cainsgl.core.command.manager;

import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.RESP2Response;
import cainsgl.core.network.response.impl.ArrayResponse;
import cainsgl.core.persistence.MutSerializer;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.List;
import java.util.function.Consumer;

public interface CommandManager
{
     void exceptionCaught(Exception e);
     default <T>TTLObj<T> createTTL(long expireTime,T wrapper, Consumer<TTLObj<T>> delCall)
     {
          return new TTLObj<T>(expireTime,wrapper,this,delCall);
     }
     default <T>TTLObj<T> createTTL(T wrapper)
     {
          return new TTLObj<T>(wrapper);
     }
     default List<ElementResponse> scanData()
     {
          return null;
     }
}
