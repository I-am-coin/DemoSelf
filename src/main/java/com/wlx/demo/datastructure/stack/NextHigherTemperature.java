package com.wlx.demo.datastructure.stack;

import com.alibaba.fastjson.JSON;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 下一个更高气温
 * @author weilx
 */
public class NextHigherTemperature {
    public static void main(String[] args) {
        System.out.print(JSON.toJSONString(new Solution().dailyTemperatures(new int[]{73,74,75,71,69,72,76,73})));
    }

    static class Solution {
        public int[] dailyTemperatures(int[] temperatures) {
            int len = temperatures.length;
            if (len == 0) {
                return new int[len];
            }
            int[] nextHigherDays = new int[len];
            Deque<Integer> stack = new LinkedList<>();

            for (int i = 0; i < len; i++) {
                if (!stack.isEmpty()) {
                    Integer top = stack.peek();
                    while (null != top && temperatures[i] > temperatures[top]) {
                        nextHigherDays[top] = (i - top);
                        stack.pop();
                        top = stack.peek();
                    }
                }
                stack.push(i);
            }
            return nextHigherDays;
        }
    }
}
