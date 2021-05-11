package com.wlx.demo.datastructure.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 给你一个整数n，返回和为n的完全平方数的最少数量
 * @author weilx
 */
public class CountMinSquares {
    public static void main(String[] args) {
        System.out.println(new Solution().numSquares2(7184));
    }

    static class Solution {
        /**
         * dp
         * @param n
         * @return
         */
        public int numSquares(int n) {
            int dp[] = new int[n + 1];
            Arrays.fill(dp, Integer.MAX_VALUE);
            // bottom case
            dp[0] = 0;

            // pre-calculate the square numbers.
            int maxSquareIndex = (int) Math.sqrt(n) + 1;
            int squareNums[] = new int[maxSquareIndex];
            for (int i = 1; i < maxSquareIndex; ++i) {
                squareNums[i] = i * i;
            }

            for (int i = 1; i <= n; ++i) {
                for (int s = 1; s < maxSquareIndex; ++s) {
                    if (i < squareNums[s])
                        break;
                    dp[i] = Math.min(dp[i], dp[i - squareNums[s]] + 1);
                }
            }
            return dp[n];
        }

        /**
         * 拉格朗日法计算
         * @param n 一个正整数
         * @return n的完全平方数的最少数量
         */
        public int numSquares2 (int n) {
            //一，先判断由1个平方数组成的
            //如果n是平方数，直接返回1即可，表示n由
            //1个平方数组成
            if (isSquare(n)) {
                return 1;
            }
            //如果n是4的倍数，就除以4，因为4是2的平方，
            //如果n可以由m个完全平方数组成，那么4n也
            //可以由m个完全平方数组成
            while ((n & 3) == 0) { // n % 4 == 0
                n >>= 2;
            }
            //二，在判断由4个平方数组成的
            //如果n是4的倍数，在上面代码的执行中就会一直除以4，
            //直到不是4的倍数为止，所以这里只需要判断n=(8b+7)
            //即可
            if ((n & 7) == 7) { // n % 8 == 7
                return 4;
            }
            int sqrtN = (int) (Math.sqrt(n));
            //三，接着判断由2个平方数组成的
            //下面判断是否能由2个平方数组成
            for (int i = 1; i <= sqrtN; i++) {
                if (isSquare(n - i * i)) {
                    return 2;
                }
            }
            //四，剩下的只能由3个平方数组成了
            //如果上面都不成立，根据拉格朗日四平方和定理
            //只能由3个平方数组成了
            return 3;
        }

        //判断n是否是平方数
        private boolean isSquare(int n) {
            int sqrtN = (int) (Math.sqrt(n));
            return sqrtN * sqrtN == n;
        }

        /**
         * bfs
         * @param n
         * @return
         */
        public int numSquares3(int n) {
            ArrayList<Integer> squareNums = new ArrayList<Integer>();
            for (int i = 1; i * i <= n; ++i) {
                squareNums.add(i * i);
            }

            Set<Integer> queue = new HashSet<Integer>();
            queue.add(n);

            int level = 0;
            while (queue.size() > 0) {
                level += 1;
                Set<Integer> next_queue = new HashSet<Integer>();

                for (Integer remainder : queue) {
                    for (Integer square : squareNums) {
                        if (remainder.equals(square)) {
                            return level;
                        } else if (remainder < square) {
                            break;
                        } else {
                            next_queue.add(remainder - square);
                        }
                    }
                }
                queue = next_queue;
            }
            return level;
        }
    }
}
