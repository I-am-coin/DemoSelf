package com.wlx.demo.datastructure.queue;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 0和边界代表水，1代表陆地
 * 计算岛屿数量
 *
 * @author weilx
 */
public class CountIsland {
    public static void main(String[] args) {
        String json = "[['1','1','0','0','0'],['1','1','0','0','0'],['0','0','1','0','0'],['0','0','0','1','1']]";
        char[][] grid = JSONObject.parseObject(json, char[][].class);
        int numIslands = new Solution().numIslands(grid);
        System.out.println(numIslands);
    }

    static class Solution {
        private static final char LAND = '1';
        private static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        public int numIslands(char[][] grid) {
            int m = grid.length;
            if (m == 0) {return 0;}
            int n = grid[0].length;
            Set<Node> used = new HashSet<>();
            Queue<Node> queue = new LinkedList<>();
            int number = 0;

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] == LAND) {
                        Node root = new Node(i, j);

                        if (used.contains(root)) {
                            continue;
                        }
                        queue.offer(root);
                        used.add(root);
                        number++;

                        while (!queue.isEmpty()) {
                            Node node = queue.poll();
                            int a = node.getX();
                            int b = node.getY();

                            for (int[] d : DIRECTIONS) {
                                int e = a + d[0];
                                int f = b + d[1];

                                if (e < 0 || e >= m || f < 0 || f >= n || grid[e][f] != LAND ) {
                                    continue;
                                }
                                Node next = new Node(e, f);

                                if (used.contains(next)) {
                                    continue;
                                }
                                queue.offer(next);
                                used.add(next);
                            }
                        }
                    }
                }
            }
            return number;
        }

        class Node {
            private int x;
            private int y;

            public Node(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Node)) return false;
                Node node = (Node) o;
                return getX() == node.getX() &&
                        getY() == node.getY();
            }

            @Override
            public int hashCode() {

                return Objects.hash(getX(), getY());
            }
        }
    }
}
