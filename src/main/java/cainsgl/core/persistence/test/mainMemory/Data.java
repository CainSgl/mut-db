package cainsgl.core.persistence.test.mainMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data {

    private static HashMap<String, RedisObj<?>> dataGroup;

    static {
        dataGroup = new HashMap<>();
        init();
    }

    private static void init(){

        RedisObj<String> redisObj0 = new RedisObj<>("jack", String.class);
        RedisObj<String> redisObj1 = new RedisObj<>("mary", String.class);
        RedisObj<String> redisObj2 = new RedisObj<>("lee", String.class);
        RedisObj<String> redisObj3 = new RedisObj<>("hong", String.class);

        RedisObj<List<String>> listRedisObj = new RedisObj<>();
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        listRedisObj.setValue(list);
        listRedisObj.setType(List.class);

        RedisObj<List<List<String>>> listListRedisObj = new RedisObj<>();
        List<List<String>> listList = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        List<String> list2 = new ArrayList<>();
        list2.add("李华");
        list2.add("王五");
        list2.add("张三");
        list2.add("李四");
        listList.add(list1);
        listList.add(list2);
        listListRedisObj.setValue(listList);
        listListRedisObj.setType(List.class);

        dataGroup.put("name1", redisObj0);
        dataGroup.put("name2", redisObj1);
        dataGroup.put("name3", redisObj2);
        dataGroup.put("name4", redisObj3);
        dataGroup.put("MyList", listRedisObj);
        dataGroup.put("MyList2(List<List<String>>)", listListRedisObj);
    }

    public static HashMap<String, RedisObj<?>> getData(){
        return dataGroup;
    }

    public static void put(String key, RedisObj<?> obj){
        dataGroup.put(key, obj);
    }

}
