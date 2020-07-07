package com.wlx.test;

import org.junit.Test;

import java.util.*;

public class POJOTest {
    /**
     * 对于Integer var = ? 在-128至127范围内的赋值，Integer对象是在 IntegerCache.cache产生，
     * 会复用已有对象，这个区间内的Integer值可以直接使用==进行判断，
     * 但是这个区间之外的所有数据，都会在堆上产生，并不会复用已有对象，这是一个大坑，
     * 推荐使用equals方法进行判断
     */
    @Test
    public void baseObjectTest() throws Exception {
        Integer integer1 = 100000;
        Integer integer2 = 100000;
        Integer integer3 = 100;
        Integer integer4 = 100;

        if (integer1 == integer2) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }
        if (integer3 == integer4) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }

        float f1 = 1e-6f;

        System.out.println(f1);
    }

    @Test
    public void collectionTest() throws Exception {
        // 1.
        List<Object> objects = Collections.emptyList();

        // Immutable object is modified
//        objects.add(120); // java.lang.UnsupportedOperationException

        // 2.
        List<String> list = new ArrayList<String>();
        list.add("ADS");
        list.add("BOS");
        String[] strings = list.toArray(new String[0]); // 推荐0

        // 3. asList的返回对象是一个Arrays内部类，并没有实现集合的修改方法。Arrays.asList体现的是适配器模式，只是转换接口，后台的数据仍是数组。
        String[] ss1 = new String[]{"A", "C"};
        List<String> sList1 = Arrays.asList(ss1);
//        sList1.add("D"); // 异常 java.lang.UnsupportedOperationException
        System.out.println(sList1.get(1)); // C
        ss1[1] = "B";
        System.out.println(sList1.get(1)); // B

        // 4. 集合初始化指定大小
        Map<String, Object> map = new HashMap<>(16);
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.forEach((x, y) -> {
            System.out.println("KEY=" + x + ", VALUE=" + y);
        });
    }

    // ? 是 Number 的子类， Number 是下界
    private static Number getClassName(List<? extends Number> list) throws Exception {
        return list.get(0);
    }

    // ? 是 Number 的父类， Number 是下界
    private static void addOne(List<? super Number> list) throws Exception {
        list.add(1);
        list.add((Number)12);
//        list.add((Object)123); error
    }

    @Test
    public void switchTest() throws Exception {
        POJOTest.method(null); // java.lang.NullPointerException
    }

    private static void method(String param) {
        switch (param) {
            // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里
            default:
                System.out.println("default");
        }
    }
}
