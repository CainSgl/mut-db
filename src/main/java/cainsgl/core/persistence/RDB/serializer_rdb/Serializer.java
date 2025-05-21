package cainsgl.core.persistence.RDB.serializer_rdb;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Serializer {
    void serialize(DataOutputStream out, Object value) throws IOException;
}
