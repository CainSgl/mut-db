package cainsgl.core.storge.config;

import cainsgl.core.data.key.ByteFastKey;
import cainsgl.core.data.key.ByteSuperKey;
import cainsgl.core.data.ttl.TTL2Obj;
import cainsgl.core.data.ttl.TTLObj;
import cainsgl.core.data.value.ByteValue;
import cainsgl.core.storge.converter.Converter;

import java.nio.charset.StandardCharsets;

public class StorgeConfiguration
{
    private static class StringConverter extends Converter<String>
    {
        @Override
        public byte[] toBytes(String obj)
        {
            return obj.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String fromBytes(byte[] bytes)
        {
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private static class IntegerConverter extends Converter<Integer>
    {
        @Override
        public byte[] toBytes(Integer obj) {
            return new byte[]{
                    (byte) (obj >> 24),
                    (byte) (obj >> 16),
                    (byte) (obj >> 8),
                    (byte) obj.intValue()
            };
        }

        @Override
        public Integer fromBytes(byte[] bytes) {
            if (bytes.length != 4) {
                throw new IllegalArgumentException("Invalid integer byte array length");
            }
            return ((bytes[0] & 0xFF) << 24) |
                    ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) |
                    (bytes[3] & 0xFF);
        }
    }

    private static class ByteSuperKeyConverter extends Converter<ByteSuperKey>
    {
        @Override
        public byte[] toBytes(ByteSuperKey obj)
        {
            return obj.getBytes();
        }

        @Override
        public ByteSuperKey fromBytes(byte[] bytes)
        {
            return new ByteSuperKey(bytes);
        }
    }

    private static class ByteFastKeyConverter extends Converter<ByteFastKey>
    {

        @Override
        public byte[] toBytes(ByteFastKey obj)
        {
            return obj.getBytes();
        }

        @Override
        public ByteFastKey fromBytes(byte[] bytes)
        {
            return new ByteFastKey(bytes);
        }
    }

    private static class ByteValueConverter extends Converter<ByteValue>
    {
        @Override
        public byte[] toBytes(ByteValue obj)
        {
            return obj.getBytes();
        }

        @Override
        public ByteValue fromBytes(byte[] bytes)
        {
            return new ByteValue(bytes);
        }
    }

    private static class ByteArrayConverter extends Converter<byte[]>
    {

        @Override
        public byte[] toBytes(byte[] obj)
        {
            return obj;
        }

        @Override
        public byte[] fromBytes(byte[] bytes)
        {
            return bytes;
        }
    }


    public StorgeConfiguration()
    {
        new StringConverter();
        new IntegerConverter();
        new ByteSuperKeyConverter();
        new ByteFastKeyConverter();
        new ByteValueConverter();
        new ByteArrayConverter();
        //重要的
      //  new TTLObj.TTLObjConverter();
        new TTL2Obj.TTLObjConverter();
    }
}
