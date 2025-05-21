package cainsgl.core.persistence.RDB.serializer_rdb;

import java.io.DataInputStream;
import java.io.IOException;


@FunctionalInterface
public interface Deserializer {
    Object deserialize(DataInputStream in) throws IOException;
}
