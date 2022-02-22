package com.wlx.test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;

public class MapTest {

    @Test
    public void testMapRemove() throws Exception {
        Map<String, String> map = Maps.asMap(Sets.newHashSet("KEY1", "KEY2", "KEY3"), key -> key.replace("KEY", "VALUE"));
        out.println("init map is " + map);

        // 不行 ConcurrentModificationException
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            out.println("" + entry.getKey() + ":" + entry.getValue());
//
//            if ("KEY2".equals(entry.getKey())) {
//                map.remove(entry.getKey());
//            }
//        }

        // 不行 ConcurrentModificationException
//        for (String key : map.keySet()) {
//            out.println("" + key + ":" + map.get(key));
//
//            if ("KEY2".equals(key)) {
//                map.remove(key);
//            }
//        }

        // 不行 ConcurrentModificationException
//        Set<String> keySet = map.keySet();
//
//        for (String key : keySet) {
//            out.println("" + key + ":" + map.get(key));
//
//            if ("KEY2".equals(key)) {
//                map.remove(key);
//            }
//        }

        // 不行 ConcurrentModificationException
//        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            Map.Entry<String, String> entry = iterator.next();
//            out.println("" + entry.getKey() + ":" + entry.getValue());
//
//            if ("KEY2".equals(entry.getKey())) {
//                map.remove(entry.getKey());
//            }
//        }

        // 可以
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            out.println("" + entry.getKey() + ":" + entry.getValue());

            if ("KEY2".equals(entry.getKey())) {
                iterator.remove();
            }
        }
        out.println("result map is " + map);
    }
}
