package cainsgl.core.persistence.serializer;

public interface MutSerializable {

    byte[] serialization();

    void deSerializer(byte[] data);

    // 获取类的全限定名称
    default String getClassType(){
        return this.getClass().getName();
    }

}
