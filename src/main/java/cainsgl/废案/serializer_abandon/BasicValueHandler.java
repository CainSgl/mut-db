package cainsgl.废案.serializer_abandon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
* 提供序列化与反序列化的容器化管理
* */
@Deprecated
public class BasicValueHandler {

    private final Serializer serializer;

    private final Deserializer deserializer;

    public BasicValueHandler(Serializer serializer, Deserializer deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public void serialize(DataOutputStream out, Object value) throws IOException {
        serializer.serialize(out, value);
    }

    public Object deserialize(DataInputStream in) throws IOException {
        return deserializer.deserialize(in);
    }

}
