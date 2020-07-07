package com.wlx.test;

import com.wlx.demo.test.idlehero.RollDice;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class DhTest {

    @Test
    public void testDice() throws Exception {
        // <170 170 200 230 260 300
        int[] type = new int[]{0, 0, 0, 0, 0, 0};
        List<Integer> data = new ArrayList<>();

        for (int i=0; i < 10000; i++) {
            int num = new RollDice().run(78);

            data.add(num);
            if (num < 170) {
                type[0]++;
            } else if (num < 200) {
                type[1]++;
            } else if (num < 230) {
                type[2]++;
            } else if (num < 260) {
                type[3]++;
            } else if (num < 300) {
                type[4]++;
            } else {
                type[5]++;
            }
        }

        System.out.println("0 - 170 的次数：" + type[0]);
        System.out.println("170 - 200 的次数：" + type[1]);
        System.out.println("200 - 230 的次数：" + type[2]);
        System.out.println("230 - 260 的次数：" + type[3]);
        System.out.println("260 - 300 的次数：" + type[4]);
        System.out.println("300+ 的次数：" + type[5]);

        File file = new File("C:\\Users\\weilx\\Desktop\\roll.txt");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), 1024)) {
            for (int i=0; i<data.size(); i++) {
                String msg = (1 + i) + "\t" + data.get(i) + "\n";
                bw.write(msg);
            }
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
