package com.wlx.demo.datastructure.stack;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 计算逆波兰表达式
 */
public class CaculateRPN {
    public static void main(String[] args) {
        System.out.println(new Solution().evalRPN(new String[]{"10","6","9","3","+","-11","*","/","*","17","+","5","+"}));
    }


    static class Solution {
        public int evalRPN(String[] tokens) {
            if (null == tokens || tokens.length < 1) {
                return 0;
            }
            Deque<Integer> stack = new LinkedList<>();

            for (String s : tokens) {
                int n = -1;

                if (!stack.isEmpty()) {
                    switch (s) {
                        case "+" :
                            n = stack.pop() + stack.pop();
                            break;
                        case "-" :
                            int a = stack.pop();
                            int b = stack.pop();
                            n = b - a;
                            break;
                        case "*" :
                            n = stack.pop() * stack.pop();
                            break;
                        case "/" :
                            int c = stack.pop();
                            int d = stack.pop();
                            n = d / c;
                            break;
                        default :
                            n = Integer.parseInt(s);
                    }
                } else {
                    n = Integer.parseInt(s);
                }
                stack.push(n);
            }
            return stack.pop();
        }
    }
}
