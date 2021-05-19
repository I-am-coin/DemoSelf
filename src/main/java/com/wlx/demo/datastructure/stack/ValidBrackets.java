package com.wlx.demo.datastructure.stack;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 括号匹配
 * @author weilx
 */
public class ValidBrackets {

    public static void main(String[] args) {
        System.out.print(new Solution().isValid("([)]"));
    }

    static class Solution {
        private static final List<Character> LEFT_BRACKETS = Arrays.asList('(', '[', '{');
        private static final List<Character> RIGHT_BRACKETS = Arrays.asList(')', ']', '}');

        public boolean isValid(String s) {
            if (null == s || s.length() <= 1) {
                return false;
            }
            Deque<Character> stack = new LinkedList<>();

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);

                if (stack.isEmpty()) {
                    stack.push(c);
                    continue;
                }
                Character top = stack.peek();

                if (LEFT_BRACKETS.contains(top)
                        && (RIGHT_BRACKETS.get(LEFT_BRACKETS.indexOf(top)) == c)) {
                    // 栈顶是左括号，且栈顶对应右括号与当前字符相等
                    stack.pop();
                } else {
                    stack.push(c);
                }
            }
            return stack.isEmpty();
        }
    }
}
