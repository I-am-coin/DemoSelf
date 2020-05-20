package com.wlx.demo.test.idlehero;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class RollDice {
    private static final Random random = new Random();

    public static void main(String[] args) throws Exception {
        final int totalCount = 78;
        final int tableSize = 20;
        final int[] points = new int[]{3, 10, 17};
        int[] starScore = new int[]{3, 3, 3};
        final int maxScore = 5;
        int starNum = 0;
        int totalStep = 0;
        int tmp = tableSize;
        int stepIndex = -1;

        for (int i=0; i<totalCount; i++) { // 掷骰子次数
            int step = nextStep();
            totalStep += step;
            stepIndex += step;
            tmp -= step;

            if (stepIndex >= tableSize) { // 一轮
                stepIndex -= tableSize;
            }
            System.out.println("第" + (i+1) + "次掷骰子，点数：" + step + "，到达位置：" + (stepIndex + 1));
            for (int j=0; j<points.length; j++) { // 如果掷到星星则升级
                if (points[j] == stepIndex && starScore[j] < maxScore) {
                    starScore[j] += 1;
                    System.out.println(">> 命中星星屋(" + (j+1) + ")，升级星星数：" + starScore[j]);
                    break;
                }
            }
            if (tmp <= 0) { // 每轮结束计算星星数量
                int tmpStarCount = countAll(starScore);
                System.out.println(">>> 一轮结束，计算骰子数：" + tmpStarCount);
                starNum += tmpStarCount;
                tmp += tableSize; // 重置转盘数
            }
        }

        System.out.println("星星数：" + starNum);
        System.out.println("总步数：" + totalStep);
    }

    private static int nextStep() throws Exception {
        return random.nextInt(6) + 1;
    }

    private static int countAll(int[] a) throws Exception {
        int c = 0;

        for (int b : a) {
            c += b;
        }
        return c;
    }
}
