package cainsgl.core.persistence.serializer;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Serializer {
    void serialize(DataOutputStream out, Object value) throws IOException;
}
