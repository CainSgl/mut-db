package cainsgl.core.storge.map;

import java.io.IOException;
import java.util.Map;

public interface Serializer<K,V>
{
    byte[] serialize(Map<K, V > map);

    Map<K, V> deserialize(byte[] data);
}
