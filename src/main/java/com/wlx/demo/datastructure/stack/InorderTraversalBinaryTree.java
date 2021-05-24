package com.wlx.demo.datastructure.stack;

import java.util.*;

/**
 * 中序遍历二叉树
 *
 * @author weilx
 */
public class InorderTraversalBinaryTree {

    class Solution {
        public List<Integer> inorderTraversal(TreeNode root) {
            Deque<TreeNode> stack = new ArrayDeque<TreeNode>();
            List<Integer> res = new ArrayList<>();
            while (root != null || !stack.isEmpty()) {
                while (root != null) {       // 不断地向左结点深入，直至叶子结点
                    stack.push(root);
                    root = root.left;
                }
                TreeNode top = stack.pop();
                res.add(top.val);
                root = top.right;
            }
            return res;
        }
    }

    class RCallSolution {
        public List<Integer> inorderTraversal(TreeNode root) {
            List<Integer> res = new ArrayList<>();
            dfs(root, res);
            return res;
        }

        private void dfs(TreeNode cur, List<Integer> res) {
            if (null == cur) {
                return;
            }
            dfs(cur.left, res);
            res.add(cur.val);
            dfs(cur.right, res);
        }
    }

    private class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
