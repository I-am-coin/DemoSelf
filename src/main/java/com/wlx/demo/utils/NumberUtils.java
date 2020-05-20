package com.wlx.demo.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NumberUtils {
    public final static String UNIT = "十百千万亿";
    public final static String ZH_NUMBER = "零一二三四五六七八九";

    public static int zhNumberFormat(String zhNumber) throws Exception {
        int number = 0;

        if (StringUtils.isNotBlank(zhNumber)) {
            zhNumber = StringUtils.trim(zhNumber);
            List<int[]> iNumbers = new ArrayList<int[]>();
            int j = 0;
            int[] num = new int[2];

            for (int i = (zhNumber.length() - 1); i >= 0; i--) { // 倒着取
                String c = zhNumber.charAt(i) + "";

                if (UNIT.contains(c)) { // 放单位
                    if (num[1] > 0) { // 多单位：百万
                        num[1] = num[1] * (int) Math.pow(10, UNIT.indexOf(c) + 1);
                    } else {
                        num[1] = (int) Math.pow(10, UNIT.indexOf(c) + 1);
                    }
                    if (zhNumber.startsWith(c)) { // 单位开头：十一
                        num[0] = 1;
                        j++;
                        iNumbers.add(num);
                        num = new int[2];
                    }
                } else if (ZH_NUMBER.contains(c)) { // 放数字
                    if (num[1] <= 0) { // 先放的单位
                        if (j > 0) {
                            num[1] = 10 * iNumbers.get(j - 1)[1];
                        } else {
                            num[1] = 1;
                        }
                    }
                    num[0] = ZH_NUMBER.indexOf(c);
                    j++;
                    iNumbers.add(num);
                    num = new int[2];
                }
            }

            // 计算
            for (int i=0; i<iNumbers.size(); i++) {
                num = iNumbers.get(i);
                number += (num[0] * num[1]);
            }
        }

        return number;
    }

    public static void main(String[] args) throws Exception {
        NumberUtils.zhNumberFormat("百万");
    }
}
