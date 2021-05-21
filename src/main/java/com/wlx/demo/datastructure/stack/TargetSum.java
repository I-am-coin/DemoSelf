package com.wlx.demo.datastructure.stack;

/**
 * 目标和
 * @author weilx
 */
public class TargetSum {
    public static void main(String[] args) {
        System.out.println(new Solution().findTargetSumWays(new int[]{1, 1, 1, 2, 1}, 3));
    }

    static class Solution {
        private int num = 0;

        public int findTargetSumWays(int[] nums, int target) {
            if (nums == null || nums.length == 0) {
                return 0;
            }
            dfs(0, nums, target, 0);
            return num;
        }

        private void dfs(int i, int[] nums, int target, int sum) {
            if (i == nums.length - 1) {
                if (sum + nums[i] == target) {
                    num++;
                }
                if (sum - nums[i] == target) {
                    num++;
                }
            } else {
                dfs(i + 1, nums, target, sum + nums[i]);
                dfs(i + 1, nums, target, sum - nums[i]);
            }
        }
    }

    /**
     * 动态规划<br />
     * <p>
     *     1 <= nums.length <= 20;
     *     0 <= nums[i] <= 1000;
     *     0 <= sum(nums[i]) <= 1000;
     *     -1000 <= target <= 100;
     * </p>
     */
    static class DPSolution {
        public int findTargetSumWays(int[] nums, int target) {
            if (nums == null || nums.length == 0) {
                return 0;
            }
            int[] dp = new int[2001];
            dp[nums[0] + 1000] = 1;
            dp[-nums[0] + 1000] += 1;

            for (int num : nums) {
                int[] next = new int[2011];

                for (int sum = -1000; sum <= 1000; sum++) {
                    if (dp[sum + 1000] > 0) {
                        next[sum + 1000 - num] += dp[sum + 1000];
                        next[sum + 1000 + num] += dp[sum + 1000];
                    }
                }
                dp = next;
            }
            return target > 1000 ? 0 : dp[target + 1000];
        }
    }
}
