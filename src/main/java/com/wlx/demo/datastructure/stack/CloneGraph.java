package com.wlx.demo.datastructure.stack;

import java.util.*;

/**
 * 克隆图
 * 2021-5-20
 * @author weilx
 */
public class CloneGraph {
    public static void main(String[] args) {

    }

    static class Solution {
        public Node cloneGraph(Node node) {
            if (null == node) {
                return null;
            }
            Map<Integer, Node> visited = new HashMap<>();
            Node clone = new Node(node.val);
            visited.put(node.val, clone);
            dfs(node, visited, clone);
            return clone;
        }

        private void dfs(Node cur, Map<Integer, Node> visited, Node clone) {
            if (cur.neighbors != null && cur.neighbors.size() > 0) {
                for (Node next : cur.neighbors) {
                    if (visited.containsKey(next.val)) {
                        clone.neighbors.add(visited.get(next.val));
                    } else {
                        Node nextClone = new Node(next.val);
                        visited.put(next.val, nextClone);
                        clone.neighbors.add(nextClone);
                        dfs(next, visited, nextClone);
                    }
                }
            }
        }
    }

    static class Node {
        private int val;
        private List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

}
