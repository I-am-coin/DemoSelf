package com.wlx.demo.datastructure.queue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 使用队列实现栈
 * @author weilx
 */
public class StackWithQueue {
    class MyStack {
        Queue<Integer> mainQueue;
        Queue<Integer> temQueue;

        /** Initialize your data structure here. */
        public MyStack() {
            mainQueue = new LinkedList<>();
            temQueue = new LinkedList<>();
        }

        /** Push element x onto stack. */
        public void push(int x) {
            while (!mainQueue.isEmpty()) {
                temQueue.offer(mainQueue.poll());
            }
            mainQueue.offer(x);
            while (!temQueue.isEmpty()) {
                mainQueue.offer(temQueue.poll());
            }
        }

        /** Removes the element on top of the stack and returns that element. */
        public int pop() {
            return mainQueue.poll();
        }

        /** Get the top element. */
        public int top() {
            return mainQueue.peek();
        }

        /** Returns whether the stack is empty. */
        public boolean empty() {
            return mainQueue.isEmpty() && temQueue.isEmpty();
        }
    }
}
