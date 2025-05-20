package cainsgl.core.persistence.serializer.converter.byteConverter.impl;

import cainsgl.core.persistence.serializer.converter.byteConverter.ByteConverter;

public class StringByteConverter implements ByteConverter<String> {

    @Override
    public byte[] convertToBytes(String value) {
        return value.getBytes();
    }

    @Override
    public String deConverter(byte[] bytes) {
        return new String(bytes);
    }

}
