package com.wlx.demo.datastructure.queue;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * 有 n 个房间，房间按从 0 到 n - 1 编号。最初，除 0 号房间外的其余所有房间都被锁住。你的目标是进入所有的房间。然而，你不能在没有获得钥匙的时候进入锁住的房间。
 *
 * 当你进入一个房间，你可能会在里面找到一套不同的钥匙，每把钥匙上都有对应的房间号，即表示钥匙可以打开的房间。你可以拿上所有钥匙去解锁其他房间。
 *
 * 给你一个数组 rooms 其中 rooms[i] 是你进入 i 号房间可以获得的钥匙集合。如果能进入 所有 房间返回 true，否则返回 false。
 *
 * n == rooms.length
 * 2 <= n <= 1000
 * 0 <= rooms[i].length <= 1000
 * 1 <= sum(rooms[i].length) <= 3000
 * 0 <= rooms[i][j] < n
 * 所有 rooms[i] 的值 互不相同
 *
 * 作者：力扣 (LeetCode)
 * 链接：https://leetcode-cn.com/leetbook/read/queue-stack/gle1r/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * @author weilx
 */
public class ClockAndKey {
    public static void main(String[] args) {
        List rooms = JSON.toJavaObject(JSON.parseArray("[[1,3],[3,0,1],[2],[0]]"), List.class);

        boolean b = new ClockAndKey().canVisitAllRooms(rooms);

        System.out.println("OUT = " + b);
    }

    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        Set<Integer> visited = new HashSet<>(rooms.size());
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);

        while (!queue.isEmpty()) {
            Integer one = queue.poll();

            if (null == one) {
                continue;
            }
            if (!visited.contains(one)) {
                List<Integer> keys = rooms.get(one);

                if (keys != null && keys.size() > 0) {
                    ((LinkedList<Integer>) queue).addAll(0, keys);
                }
            }
            visited.add(one);
        }
        return visited.size() == rooms.size();
    }
}
