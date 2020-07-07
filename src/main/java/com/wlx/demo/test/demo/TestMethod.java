package com.wlx.demo.test.demo;


import com.wlx.demo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestMethod {

    public static void main(String[] args) throws Exception {
        List<Long> list = testT();

        Long[] longs = list.toArray(new Long[0]); // 加上这句才会报错

    }

    private static List<Long> testT() throws Exception {
        List<Long> list1 = new ArrayList();
        list1.add(1000L);
        List list2 = new ArrayList<String>();
        list2.add("AAA");
        list1 = list2;
        return list1;
    }

}
