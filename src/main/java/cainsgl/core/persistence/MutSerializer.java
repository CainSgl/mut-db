package cainsgl.core.persistence;

public interface MutSerializer {

    byte[] serialization();

    void deSerializer(byte[] data);

}
