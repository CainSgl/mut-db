package cainsgl.core.persistence.test.mainMemory;

public class RedisObj<T> {

    private T value; // 存储的数据

    private byte type; // 存储的数据类型

    public RedisObj(T value, Byte type) {
        this.value = value;
        this.type = type;
    }

    public RedisObj(){

    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue(){
        return value;
    }

    public Byte getType(){
        return type;
    }

    public void setType(Byte type){
        this.type = type;
    }
}
