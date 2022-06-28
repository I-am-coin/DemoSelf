package com.wlx.demo.datastructure.array;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;

/**
 * 给定一个由 整数 组成的 非空 数组所表示的非负整数，在该数的基础上加一。
 *
 * 最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
 *
 * 你可以假设除了整数 0 之外，这个整数不会以零开头。
 *
 * 1 <= digits.length <= 100
 * 0 <= digits[i] <= 9
 */
public class PlusOne {
    public static void main(String[] args) {
        int[] nums1 = {9};

        int[] intersect = new PlusOne().plusOne(nums1);
        System.out.println(JSON.toJSONString(intersect));
    }

    public int[] plusOne(int[] digits) {
        int m = digits.length;
        int[] d = new int[m + 1];
        d[m] = ++digits[m - 1];

        for (int i = m -1; i >= 0; i--) {
            if (digits[i] == 10) {
                digits[i] = 0;
                d[i + 1] = 0;
                if (i == 0) {
                    d[0] = 1;
                } else {
                    d[i] = ++digits[i-1];
                }
            } else {
                d[i + 1] = digits[i];
            }
        }
        return d[0] == 1 ? d : Arrays.copyOfRange(d, 1, m + 1);
    }
}
