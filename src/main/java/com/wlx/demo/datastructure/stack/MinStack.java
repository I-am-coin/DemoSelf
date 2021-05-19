package com.wlx.demo.datastructure.stack;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 最小栈
 * @author weilx
 */
public class MinStack {

    public static void main(String[] args) {
        MinStacks minStacks = new MinStacks();
        minStacks.push(1);
        minStacks.push(-50);
        minStacks.push(100);
        minStacks.push(Integer.MIN_VALUE);
        System.out.println(minStacks.top());
        System.out.println(minStacks.getMin());
        minStacks.pop();
        System.out.println(minStacks.top());
        System.out.println(minStacks.getMin());
        minStacks.pop();
        minStacks.pop();
        System.out.println(minStacks.top());
        System.out.println(minStacks.getMin());
        minStacks.push(-200);
        System.out.println(minStacks.top());
        System.out.println(minStacks.getMin());
    }

    static class MinStacks {
        private Deque<Integer> dataStack;
        private Deque<Integer> minStack;

        public MinStacks() {
            dataStack = new LinkedList<>();
            minStack = new LinkedList<>();
            minStack.push(Integer.MAX_VALUE);
        }

        public void push(int val) {
            dataStack.push(val);
            minStack.push(Math.min(minStack.peek(), val));
        }

        public void pop() {
            dataStack.pop();
            minStack.pop();
        }

        public int top() {
            return dataStack.peek();
        }

        public int getMin() {
            return minStack.peek();
        }
    }
}
