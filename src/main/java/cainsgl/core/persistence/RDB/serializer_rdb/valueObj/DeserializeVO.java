package cainsgl.core.persistence.RDB.serializer_rdb.valueObj;

import cainsgl.core.persistence.test.mainMemory.RedisObj;

/* 纪录类 - 仅用于记录反序列化后得到的 key 以及 redisObj */
public record DeserializeVO(String key, RedisObj<?> redisObj) {
    @Override
    public String toString() {
        return key + ":" + redisObj.getValue() + "; type: " + redisObj.getType();
    }
}
