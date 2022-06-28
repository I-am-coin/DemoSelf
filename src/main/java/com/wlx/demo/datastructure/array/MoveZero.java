package com.wlx.demo.datastructure.array;

import com.wlx.demo.utils.StringUtils;

/**
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 *
 * 请注意 ，必须在不复制数组的情况下原地对数组进行操作。
 *
 * @author weilx
 * @date 2022-6-28
 */
public class MoveZero {

    public static void main(String[] args) {
        int[] nums = new int[]{0, 1, 0, 2, 0, 0, 3, 2};
        System.out.println("Before: " + StringUtils.join(nums, ','));
        moveZeroes(nums);
        System.out.println("After: " + StringUtils.join(nums, ','));
    }

    private static void moveZeroes(int[] nums) {
        if (nums == null) return;
        int length = nums.length;
        if (length == 1) return;;
        int t = 0;

        for (int i = 0; i < length; i++) {
            if (nums[i] != 0) {
                nums[t] = nums[i];
                nums[i] = 0;
                t++;
            }
        }
    }
}
