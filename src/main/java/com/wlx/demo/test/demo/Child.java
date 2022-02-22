package com.wlx.demo.test.demo;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class Child extends Parent {
    @Override
    public int addValue(int a, int b) {
        Hashtable hashTable = new Hashtable();
        hashTable.put(null, null);

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        concurrentHashMap.put(null, null);

        TreeMap treeMap = new TreeMap();
        treeMap.put(null, null);

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(null, null);

        int Ab = 1;

        return super.addValue(a, b);
    }
}
