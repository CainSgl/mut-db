package cainsgl.core.persistence.test.persistence.RDB_TestGroup;

import cainsgl.core.persistence.serializer.MutSerializable;

import java.util.HashMap;
import java.util.Map;

public class TestManager implements MutSerializable {

    public static Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();

    @Override
    public byte[] serialization() {
        int totaLength = 0;
        int offset = 0;
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            totaLength += entry.getKey().length;
            totaLength += entry.getValue().length;
        }
        byte[] serialized = new byte[totaLength];

        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            System.arraycopy(entry.getKey(), 0, serialized, offset, entry.getKey().length);
            offset += entry.getKey().length;
            System.arraycopy(entry.getValue(), 0, serialized, offset, entry.getValue().length);
        }
        return serialized;
    }

    @Override
    public void deSerializer(byte[] data) {
        map.put("测试用Key".getBytes(), "测试用value".getBytes());
    }
}
