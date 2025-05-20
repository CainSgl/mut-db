package cainsgl.core.persistence.test.persistence.AOF_TestGroup;

import cainsgl.core.persistence.AOF.impl.AOFListener3;
import cainsgl.core.persistence.AOF.valueObj.AOFReadVO;

import java.util.Arrays;
import java.util.List;

public class Test_AOF_Read2 {

    static byte[][] arg = new byte[2][10];
    static byte[][] arg2 = new byte[2][10];

    static {
        arg[1] = "jack".getBytes();
        arg[0] = "name".getBytes();
        arg2[1] = "mary".getBytes();
        arg2[0] = "name10".getBytes();
    }

    public static void main(String[] args) throws InterruptedException {
        AOFListener3 aofListener3 = new AOFListener3("set".getBytes(), 100001L);

//        aofListener3.addCommand(arg2, aofListener3.writeFactor().getWriteFactor(), System.currentTimeMillis());
//        Thread.sleep(1000);
//        aofListener3.addCommand(arg, aofListener3.writeFactor().getWriteFactor(), System.currentTimeMillis());

        List<AOFReadVO> aofReadVOS = aofListener3.readAOF();
        for (AOFReadVO aofReadVO : aofReadVOS) {
            System.out.println(new String(aofReadVO.getCommand()));
            System.out.println(aofReadVO.getCommandTime());
            System.out.println(Arrays.deepToString(aofReadVO.getArgs()));
        }
    }
}
