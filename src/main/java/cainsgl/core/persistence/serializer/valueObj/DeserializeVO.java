package cainsgl.core.persistence.serializer.valueObj;

import cainsgl.core.persistence.test.mainMemory.RedisObj;

public class DeserializeVO {

    private String key;

    private RedisObj<?> redisObj;

    public DeserializeVO(String key, RedisObj<?> redisObj) {
        this.key = key;
        this.redisObj = redisObj;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RedisObj<?> getRedisObj() {
        return redisObj;
    }

    public void setRedisObj(RedisObj<?> redisObj) {
        this.redisObj = redisObj;
    }
}
