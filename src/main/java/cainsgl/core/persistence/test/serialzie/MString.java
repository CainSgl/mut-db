package cainsgl.core.persistence.test.serialzie;

import cainsgl.core.persistence.serializer.MutSerializable;

public class MString implements MutSerializable {

    public String name;

    @Override
    public byte[] serialization() {
        return name.getBytes();
    }

    @Override
    public void deSerializer(byte[] data) {
        name = new String(data);
    }

    @Override
    public String toString() {
        return name;
    }
}
