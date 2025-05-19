package cainsgl.core.persistence.test.persistence.AOF_TestGroup;

import cainsgl.core.persistence.AOF.impl.AOFListener;

import java.util.List;

public class Test_AOF_Read {
    public static void main(String[] args) {
        List<String> value = AOFListener.getValue();
        System.out.println(value);
    }
}
