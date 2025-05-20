package cainsgl.废案.serializer_abandon.valueObj;

import cainsgl.core.persistence.test.mainMemory.RedisObj;

/* 纪录类 - 仅用于记录反序列化后得到的 key 以及 redisObj */
@Deprecated
public record DeserializeVO(String key, RedisObj<?> redisObj) {
    @Override
    public String toString() {
        return key + ":" + redisObj.getValue() + "; type: " + redisObj.getType();
    }
}
