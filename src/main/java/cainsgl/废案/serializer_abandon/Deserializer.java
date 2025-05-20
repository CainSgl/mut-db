package cainsgl.废案.serializer_abandon;

import java.io.DataInputStream;
import java.io.IOException;


@FunctionalInterface
@Deprecated
public interface Deserializer {
    Object deserialize(DataInputStream in) throws IOException;
}
