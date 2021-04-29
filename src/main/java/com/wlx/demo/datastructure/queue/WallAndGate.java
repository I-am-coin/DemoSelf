package com.wlx.demo.datastructure.queue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Integer.MAX_VALUE 代表空房间，-1代表墙，0代表，门
 * 寻找每个空房间到门的距离
 * @author weilx
 */
public class WallAndGate {
    public static void main(String[] args) throws Exception {
        String json = "[[2147483647,-1,0,2147483647],[2147483647,2147483647,2147483647,-1],[2147483647,-1,2147483647,-1],[0,-1,2147483647,2147483647]]";
        int[][] rooms = JSONObject.parseObject(json, int[][].class);
        new WallAndGate.Solution().wallsAndGates(rooms);
        System.out.println(JSON.toJSONString(rooms));
    }

    static class Solution {
        private static final int EMPTY = Integer.MAX_VALUE;
        private static final int GATE = 0;
        private static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        public void wallsAndGates(int[][] rooms) {
            int m = rooms.length;
            if (m == 0) return;
            int n = rooms[0].length;
            Queue<int[]> queue = new LinkedList<>();

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (rooms[i][j] == GATE) {
                        queue.offer(new int[]{i, j});
                    }
                }
            }
            while (!queue.isEmpty()) {
                int[] node = queue.poll();
                int a = node[0];
                int b = node[1];

                for (int[] d : DIRECTIONS) {
                    int e = a + d[0];
                    int f = b + d[1];

                    if (e < 0 || e >= m || f < 0 || f >= n || rooms[e][f] != EMPTY) {
                        continue;
                    }
                    rooms[e][f] = rooms[a][b] + 1;
                    queue.offer(new int[]{e,f});
                }
            }
        }
    }
}
