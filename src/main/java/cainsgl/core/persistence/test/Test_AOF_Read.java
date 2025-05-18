package cainsgl.core.persistence.test;

import cainsgl.core.persistence.AOF.AOFListener;

import java.util.List;

public class Test_AOF_Read {
    public static void main(String[] args) {
        List<String> value = AOFListener.getValue();
        System.out.println(value);
    }
}
