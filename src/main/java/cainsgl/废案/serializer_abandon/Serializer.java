package cainsgl.废案.serializer_abandon;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
@Deprecated
public interface Serializer {
    void serialize(DataOutputStream out, Object value) throws IOException;
}
