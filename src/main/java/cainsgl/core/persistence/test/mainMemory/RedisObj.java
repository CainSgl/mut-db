package cainsgl.core.persistence.test.mainMemory;

public class RedisObj<T> {

    private T value; // 存储的数据

    private Class<?> type; // 存储的数据类型

    public RedisObj(T value, Class<?> type) {
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

    public Class<?> getType(){
        return type;
    }

    public void setType(Class<?> type){
        this.type = type;
    }
}
