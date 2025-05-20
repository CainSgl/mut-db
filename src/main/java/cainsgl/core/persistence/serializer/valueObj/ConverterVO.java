package cainsgl.core.persistence.serializer.valueObj;

import java.util.Map;

public class ConverterVO {

    private Map<byte[], byte[]> values;

    private String keyType;

    private String valueType;

    public ConverterVO(Map<byte[], byte[]> values, String keyType, String valueType) {
        this.values = values;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public ConverterVO(){

    }

    public Map<byte[], byte[]> getValues() {
        return values;
    }

    public void setValues(Map<byte[], byte[]> values) {
        this.values = values;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}
