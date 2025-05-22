package cainsgl.core.command.manager;

import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.network.response.ElementResponse;


import java.util.List;
import java.util.function.Consumer;

public interface CommandManager
{
     void exceptionCaught(Exception e);
     default TTL2Obj createTTL(long expireTime, ByteValue wrapper, Consumer<TTL2Obj> delCall)
     {
          return new TTL2Obj(expireTime,wrapper,this,delCall);
     }
     default TTL2Obj createTTL(ByteValue wrapper)
     {
          return new TTL2Obj(wrapper);
     }

     default List<ElementResponse> scanData()
     {
          return null;
     }
}
