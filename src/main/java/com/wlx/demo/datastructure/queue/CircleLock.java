package com.wlx.demo.datastructure.queue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *
 * <p>你有一个带有四个圆形拨轮的转盘锁。每个拨轮都有10个数字： '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' 。
 * 每个拨轮可以自由旋转：例如把 '9' 变为  '0'，'0' 变为 '9' 。每次旋转都只能旋转一个拨轮的一位数字。</p>
 * <p>锁的初始数字为 '0000' ，一个代表四个拨轮的数字的字符串。</p>
 * <p>列表 deadends 包含了一组死亡数字，一旦拨轮的数字和列表里的任何一个元素相同，这个锁将会被永久锁定，无法再被旋转。</p>
 * <p>字符串 target 代表可以解锁的数字，你需要给出最小的旋转次数，如果无论如何不能解锁，返回 -1</p>
 * @author weilx
 */
public class CircleLock {

    public static void main(String[] args) {
        String[] deadends = {"0201","0101","0102","1212","2002"};
        String target = "0202";
        System.out.println(new Solution().openLock(deadends, target));
    }

    static class Solution {
        private static final int[][] DIRECTIONS = new int[][]{
                {11, 10, 10, 10}, {9, 10, 10, 10}, {10, 11, 10, 10}, {10, 9, 10, 10},
                {10, 10, 11, 10}, {10, 10, 9, 10},{10, 10, 10, 11}, {10, 10, 10, 9}
        };

        public int openLock(String[] deadends, String target) {
            int[] root = new int[]{0, 0, 0, 0};

            for (String dead : deadends) {
                if (dead.equals(join(root))) {
                    return -1;
                }
            }
            if (join(root).equals(target)) {
                return 0;
            }
            Queue<int[]> queue = new LinkedList<>();
            Set<String> used = new HashSet<>();
            int step = 0;
            queue.offer(root);
            used.add(join(root));

            while (!queue.isEmpty()) {
                int size = queue.size();

                for (int i = 0; i < size; i++) {
                    int[] node = queue.poll();

                    if (join(node).equals(target)) {
                        return step;
                    }
                    int a = node[0];
                    int b = node[1];
                    int c = node[2];
                    int d = node[3];

                    for (int[] t : DIRECTIONS) {
                        int l = (a + t[0]) % 10;
                        int m = (b + t[1]) % 10;
                        int n = (c + t[2]) % 10;
                        int o = (d + t[3]) % 10;
                        String nextStr = join(l, m, n, o);
                        boolean isAdd = true;

                        for (String dead : deadends) {
                            if (dead.equals(nextStr) || used.contains(nextStr)) {
                                isAdd = false;
                                break;
                            }
                        }
                        if (isAdd) {
                            queue.offer(new int[]{l, m, n, o});
                            used.add(nextStr);
                        }
                    }
                }
                step++;
            }
            return -1;
        }

        private String join(int... array) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int a : array) {
                stringBuilder.append(a);
            }
            return stringBuilder.toString();
        }
    }
}
