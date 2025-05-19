package cainsgl.core.persistence.test.persistence.RDB_TestGroup;

import cainsgl.core.persistence.RDB.RDBListener;

import java.util.Scanner;

public class Test_RDB_Write {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true){
            // 模拟添加数据操作
            scanner.next();
            // 计数器累加
            RDBListener.addInsertCont();
        }
    }

}
