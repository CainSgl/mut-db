package cainsgl.core.persistence.test.command;

import cainsgl.core.persistence.AOF.AOFListener;
import cainsgl.core.persistence.RDB.RDBListener;
import cainsgl.core.persistence.test.mainMemory.Data;
import cainsgl.core.persistence.test.mainMemory.RedisObj;

public class SetProcessor {

    public void execute(String key, byte[] value, Long expire) {
        RedisObj<Byte[]> redisObj = new RedisObj<>();
        Data.getData().put(key, redisObj);
        RDBListener.addInsertCont();
        AOFListener.addPacket(key, value, expire, AOFListener.SET_BUFFER_POSITION);
    }

}
