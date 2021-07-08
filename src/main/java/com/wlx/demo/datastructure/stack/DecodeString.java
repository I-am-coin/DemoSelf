package com.wlx.demo.datastructure.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 字符串解码
 * <p>
 *     将形如k1[str1]的字符串翻译成正常的字符串，表示为str1重复k1次
 * </p>
 * @author weilx
 */
public class DecodeString {

    public static void main(String[] args) {
        System.out.println(new StackSolution().decodeString("3[a]2[bc]"));
    }

    static class RegexSolution {
        private static final String REGEX = "\\[[a-zA-Z]+]";

        public String decodeString(String s) {
            if (s == null || "".equals(s)) {
                return "";
            }
            if (!s.contains("[")) {
                return s;
            }
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(REGEX);
            java.util.regex.Matcher m = p.matcher(s);
            List<int[]> tmp = new ArrayList<>();

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                String sCount = "";

                for (int i = start - 1; i >= 0; i--) {
                    char c = s.charAt(i);

                    if (c < '0' || c > '9') {
                        break;
                    }
                    sCount = c + sCount;
                }
                if ("".equals(sCount)) {
                    sCount = "1";
                }
                int[] ints = new int[]{start, end, Integer.parseInt(sCount)};
                tmp.add(ints);
            }
            if (tmp.size() > 0) {
                String newS = s;

                for (int[] i : tmp) {
                    int a = newS.length() - s.length();
                    String head = newS.substring(0, i[0] - String.valueOf(i[2]).length() + a);
                    String end = newS.substring(i[1] + a);
                    String mid = newS.substring(i[0] + a + 1, i[1] + a - 1);
                    StringBuilder sb = new StringBuilder(head);

                    for (int j = 0; j < i[2]; j++) {
                        sb.append(mid);
                    }
                    sb.append(end);
                    newS = sb.toString();
                }
                return decodeString(newS);
            } else {
                return decodeString(s);
            }
        }
    }

    static class StackSolution {
        public String decodeString(String s) {
            if (s == null || "".equals(s)) {
                return "";
            }
            if (!s.contains("[")) {
                return s;
            }
            Stack<Object[]> stack = new Stack<>();
            StringBuilder tmp = new StringBuilder();
            int times = -1;

            for (char c : s.toCharArray()) {
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                    tmp.append(c);
                } else if (c >= '0' && c <= '9') {
                    if (times > -1) {
                        times = times * 10 + (c - '0');
                    } else {
                        times = c - '0';
                    }
                } else if (c == '[') {
                    stack.push(new Object[] {times, tmp.toString()});
                    tmp.delete(0, tmp.length());
                    times = -1;
                } else if (c == ']') {
                    Object[] pop = stack.pop();
                    String tmpStr = tmp.toString();
                    tmp.delete(0, tmp.length());
                    tmp.append(pop[1]);

                    for (int i = 0; i < (int)pop[0]; i++) {
                        tmp.append(tmpStr);
                    }
                }
            }
            return tmp.toString();
        }
    }
}
