package com.wlx.demo.datastructure.array;

/**
 * 给定一个数组 prices ，其中 prices[i] 表示股票第 i 天的价格。
 *
 * 在每一天，你可能会决定购买和/或出售股票。你在任何时候 最多 只能持有 一股 股票。你也可以购买它，然后在 同一天 出售。
 * 返回 你能获得的 最大 利润 。
 *
 * 1 <= prices.length <= 3 * 104
 * 0 <= prices[i] <= 104
 *
 * 作者：力扣 (LeetCode)
 * 链接：https://leetcode-cn.com/leetbook/read/top-interview-questions-easy/x2zsx1/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * @author weilx
 */
public class BuyingAndSelling {

    public static void main(String[] args) {

        int i = new BuyingAndSelling().maxProfit(new int[]{1, 1, 0});

        System.out.println(i);
    }

    public int maxProfit(int[] prices) {
        if (prices.length == 1) {
            return 0;
        }
        int tempMin = prices[0];
        int tempMax = -1;
        int total = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < tempMin) {
                tempMin = prices[i];
            } else if (prices[i] > tempMax) {
                tempMax = prices[i];
            }

            if ((i == prices.length - 1 || prices[i + 1] < prices[i]) && tempMax >= tempMin) {
                total += tempMax - tempMin;
                tempMax = 0;
                tempMin = prices[i];
            }
        }

        return total;
    }
}
