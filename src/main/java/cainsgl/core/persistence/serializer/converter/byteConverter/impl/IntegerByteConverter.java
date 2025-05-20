package cainsgl.core.persistence.serializer.converter.byteConverter.impl;

import cainsgl.core.persistence.serializer.converter.byteConverter.ByteConverter;
import cainsgl.core.persistence.serializer.util.SeriUtil;

public class IntegerByteConverter implements ByteConverter<Integer> {

    @Override
    public byte[] convertToBytes(Integer value) {
        return SeriUtil.intToBytes(value);
    }

    @Override
    public Integer deConverter(byte[] bytes) {
        return SeriUtil.bytesToInt(bytes);
    }

}
