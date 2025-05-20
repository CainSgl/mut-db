package cainsgl.core.persistence.test.serialzie;

import cainsgl.core.persistence.serializer.MutSerializable;

import java.io.Serializable;

public class MString implements Serializable {

    public String name;

    @Override
    public String toString() {
        return name;
    }
}
