package com.wlx.demo.test.demo;

import com.wlx.demo.io.CSVReader;

import java.util.HashMap;
import java.util.Map;

public class CountDuplicateValue {

    public static void main(String[] args) throws Exception {
        String filePath = "";
        String[][] datas = CSVReader.parseCSVFile(filePath);
        Map<String, Integer> countMap = new HashMap<>();

        for (String[] row : datas) {
            Integer count = countMap.get(row[0]);

            if (count != null) {
                countMap.put(row[0], ++count);
            } else {
                countMap.put(row[0], 1);
            }
        }

        System.out.println("统计完成----");

        for (String key : countMap.keySet()) {
            System.out.println(key.replaceAll("\"", "") + "|" + countMap.get(key));
        }
    }


}
