package com.wlx.demo.datastructure.array;

import com.alibaba.fastjson.JSON;

/**
 * 给你一个数组，将数组中的元素向右轮转 k 个位置，其中 k 是非负数。
 *
 * 1 <= nums.length <= 105
 * -231 <= nums[i] <= 231 - 1
 * 0 <= k <= 105
 *
 * @author weilx
 */
public class RotatingArray {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6};

        System.out.println("IN :" + JSON.toJSONString(nums));
        new RotatingArray().rotate(nums, 3);
        System.out.println("OUT :" + JSON.toJSONString(nums));
    }

    public void rotate(int[] nums, int k) {
        int m = nums.length;
        k %= m;

        if (m <= 1 || k <= 0) {
            return;
        }
        int[] extra = new int[k];

        for (int i = k + m - 1; i >= 0; i--) {
            if (i < k) {
                nums[i] = extra[i];
            } else if (i < m) { // k <= i < m
                nums[i] = nums[i - k];
            } else { // i >= m
                extra[i - m] = nums[i - k];
            }
        }
    }
}
