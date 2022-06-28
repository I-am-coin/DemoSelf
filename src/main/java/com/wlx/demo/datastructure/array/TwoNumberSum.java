package com.wlx.demo.datastructure.array;

import com.wlx.demo.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案。
 *
 * @author weilx
 * @date 2022-6-28
 */
public class TwoNumberSum {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 0, 2, 2};
        System.out.println("Before: " + StringUtils.join(nums, ','));
        int[] sum = twoSum2(nums, 4);
        System.out.println("After: " + StringUtils.join(sum, ','));
    }

    private static int[] twoSum(int[] nums, int target) {
        if (nums == null) return null;
        int length = nums.length;

        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static int[] twoSum2(int[] nums, int target) {
        if (nums == null) return null;
        int length = nums.length;
        Map<Integer, Integer> map = new HashMap<>(length);

        for (int i = 0; i < length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return null;
    }
}
