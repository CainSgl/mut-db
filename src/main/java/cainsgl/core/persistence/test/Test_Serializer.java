package cainsgl.core.persistence.test;

import cainsgl.core.persistence.serializer.impl.RedisObjSerializer;
import cainsgl.core.persistence.serializer.valueObj.DeserializeVO;
import cainsgl.core.persistence.test.mainMemory.RedisObj;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Test_Serializer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 示例1：序列化 String
        RedisObj<String> strObj = new RedisObj<>("jack", String.class);
        RedisObjSerializer serializer = new RedisObjSerializer();
        byte[] strData = serializer.serialize(strObj, "name");
        System.out.println(strData.toString());
        DeserializeVO deserializedStr = serializer.deserialize(strData);
        System.out.println(deserializedStr.getKey() + ":" + deserializedStr.getRedisObj().getValue() + "; " + deserializedStr.getRedisObj().getType()); // 输出 "Hello"
    }
}
