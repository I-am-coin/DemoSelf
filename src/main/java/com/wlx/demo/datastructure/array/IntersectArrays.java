package com.wlx.demo.datastructure.array;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * 给你两个整数数组 nums1 和 nums2 ，请你以数组形式返回两数组的交集。
 * 返回结果中每个元素出现的次数，应与元素在两个数组中都出现的次数一致（如果出现次数不一致，则考虑取较小值）。
 * 可以不考虑输出结果的顺序。
 *
 * 1 <= nums1.length, nums2.length <= 1000
 * 0 <= nums1[i], nums2[i] <= 1000
 *
 * 进阶：
 *
 * 如果给定的数组已经排好序呢？你将如何优化你的算法？
 * 如果 nums1 的大小比 nums2 小，哪种方法更优？
 * 如果 nums2 的元素存储在磁盘上，内存是有限的，并且你不能一次加载所有的元素到内存中，你该怎么办？
 *
 * @author weilx
 */
public class IntersectArrays {
    public static void main(String[] args) {
        int[] nums1 = {1, 3, 4, 4, 7, 6};
        int[] nums2 = {4, 2, 1, 4};

        int[] intersect = new IntersectArrays().intersect(nums1, nums2);
        System.out.println(JSON.toJSONString(intersect));
    }

        /**
         * 暴力破解
         */
    public int[] intersect(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length, k = 0;
        int[] inter = new int[Math.min(m, n)];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (nums1[i] == nums2[j]) {
                    inter[k++] = nums1[i];
                    nums1[i] = -1;
                    nums2[j] = -2;
                    break;
                }
            }
        }
        final int[] subarray = new int[k];
        System.arraycopy(inter, 0, subarray, 0, k);
        return subarray;
    }

    /**
     * 哈希表
     */
    public int[] intersectWithMap(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return intersect(nums2, nums1);
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int num : nums1) {
            int count = map.getOrDefault(num, 0) + 1;
            map.put(num, count);
        }
        int[] intersection = new int[nums1.length];
        int index = 0;
        for (int num : nums2) {
            int count = map.getOrDefault(num, 0);
            if (count > 0) {
                intersection[index++] = num;
                count--;
                if (count > 0) {
                    map.put(num, count);
                } else {
                    map.remove(num);
                }
            }
        }
        return Arrays.copyOfRange(intersection, 0, index);
    }
}
