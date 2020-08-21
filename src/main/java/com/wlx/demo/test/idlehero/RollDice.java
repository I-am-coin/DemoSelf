package com.wlx.demo.test.idlehero;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class RollDice {
    private transient final static Logger log = LogManager.getLogger(RollDice.class);
    private static final Random random = new Random();
    private static final int maxScore = 5; // 星星屋最大价值
    private static final int pttzIndex = 4; // 普通骰子下标
    private static final int xytzIndex = 19; // 幸运骰子下标
    private static final int magicIndex = 9; // 魔法小屋下标
    private static final int zxxwIndex = 14; // 正邪小屋下标
    private static final int tableSize = 20; // 转盘格子数
    private static final int[] points = new int[]{3, 10, 17}; // 星星屋下标
    private static int[] starScore = new int[]{3, 3, 3}; // 星星屋价值

    public int run(int totalCount) throws Exception {
//        int totalCount = 78; // 总共的骰子数
        int xytzNumber = 0; // 幸运骰子数
        int starNum = 0;
        int totalStep = 0;
        int tmp = tableSize;
        int stepIndex = -1;
        int magicType = -1;
        boolean isDouble = false;
        boolean isBack = false;

        for (int i=0; i < (totalCount + xytzNumber); i++) { // 掷骰子次数
            int step = nextStep();

            if (xytzNumber > 0 && (totalCount - i) <= 0) { // 普通骰子用完了
                log.info("<<< 普通骰子为0，使用幸运骰子掷6");
                step = 6;
            }

            // 处理 MAGIC
            switch (magicType) {
                case 3: // 点数*2
                    step = step * 2;
                    if (xytzNumber > 0) { // 有幸运骰子
                        log.info(">>> 双倍牌，使用幸运骰子掷10步！");
                        step = 5 * 2;
                        xytzNumber--;
                        totalCount++;
                    }
                    break;
                case 4: // 倒退
                    step = -step;
                    isBack = true;
                    break;
                case 5: // 掷2枚
                    step += nextStep();
                    if (xytzNumber > 0) {
                        log.info(">>> 双倍牌，使用幸运骰子掷10步！");
                        step = 5 * 2;
                        xytzNumber--;
                        totalCount++;
                    }
                    break;
                case 7: // 星星2倍
                    isDouble = true;
                    break;
                case 8: // 回起点
                    step = 0;
                    stepIndex = -1;
                    break;
                case 10: // 奇数回退
                    if (step % 2 == 1) {
                        step = -step;
                        isBack = true;
                    }
                    break;
                default:
                    break;
            }
            // 处理幸运骰子, 如果接近幸运小屋，则使用
            if ((stepIndex == 13 || (stepIndex > 14 && stepIndex < 19)) && xytzNumber > 0) {
                step = 19 - stepIndex;
                xytzNumber--;
                totalCount++;
                log.info(">> 临近幸运屋，使用幸运骰子掷" + step + "步！");
            }
            magicType = -1;
            totalStep += step;
            stepIndex += step;
            tmp -= step;

            if (stepIndex >= tableSize) { // 一轮
                stepIndex -= tableSize;
            }
            log.info("第" + (i+1) + "次掷骰子，点数：" + step + "，到达位置：" + (stepIndex + 1));
            if (isBack) { // 回退没有任何效果
                log.info(">> 回退没有任何效果");
                isBack = false;
                continue;
            }
            for (int j=0; j<points.length; j++) { // 如果掷到星星则升级
                if (points[j] == stepIndex && starScore[j] < maxScore) {
                    starScore[j] += 1;
                    log.info(">> 命中星星屋(" + (j+1) + ")，升级星星数：" + starScore[j]);
                    break;
                }
                // 计算星星数
                if (stepIndex >= points[j] && (stepIndex - step) < points[j]) { // 在该范围
                    if (isDouble) {
                        starNum += (starScore[j] * 2);
                        log.info(">>> 双倍经过星星屋(" + (j+1) + ")，获得星星数：" + starScore[j] * 2);
                        isDouble = false;
                    } else {
                        starNum += starScore[j];
                        log.info(">>> 经过星星屋(" + (j+1) + ")，获得星星数：" + starScore[j]);
                    }
                }
            }
            // 处理4个特殊点
            switch (stepIndex) {
                case pttzIndex:
                    totalCount = dealPttz(totalCount);
                    break;
                case magicIndex:
                    magicType = dealMagic();
                    break;
                case zxxwIndex:
                    magicType = dealZxxw();
                    break;
                case xytzIndex:
                    xytzNumber = dealXytz(xytzNumber);
                    break;
                default:
                    break;
            }
            if (tmp <= 0) {
                tmp += tableSize; // 重置转盘数
            }
        }

        log.info("星星数：" + starNum);
        log.info("总步数：" + totalStep);
        log.info("总骰子：" + (totalCount + xytzNumber));

        return starNum;
    }

    private int nextStep() throws Exception {
        return random.nextInt(6) + 1;
    }

    private int countAll(int[] a) throws Exception {
        int c = 0;

        for (int b : a) {
            c += b;
        }
        return c;
    }

    private int dealPttz(int n) throws Exception {
        log.info(">> 命中普通骰子屋，普通骰子数+1！");
        return ++n;
    }

    private int dealXytz(int n) throws Exception {
        log.info(">> 命中幸运骰子屋，幸运骰子数+1！");
        return ++n;
    }

    private int dealZxxw() throws Exception {
        log.info(">> 命中正邪屋，下次奇数回退！");
        return 10; // 下次骰子为奇数则回退
    }

    private int dealMagic() throws Exception {
        final double magicRate = 1 / 9.0; // 随机概率
        int magicType = random.nextInt(9) + 1;

        switch (magicType) {
            case 1:
                log.info(">> 命中魔法小屋，翻牌[能量牌]获得资源自选箱！");
                break;
            case 2:
                log.info(">> 命中魔法小屋，翻牌[乌云牌]下次资源没收！");
                break;
            case 3:
                log.info(">> 命中魔法小屋，翻牌[幸运牌]下次骰子点数*2！");
                break;
            case 4:
                log.info(">> 命中魔法小屋，翻牌[捣蛋牌]下次步数倒退！");
                break;
            case 5:
                log.info(">> 命中魔法小屋，翻牌[复制牌]下次会投出两枚骰子！");
                break;
            case 6:
                log.info(">> 命中魔法小屋，翻牌[虚弱牌]随机工坊等级-1！");
                int index = ArrayUtils.indexOf(points, getRadomIndex());

                if (index != -1 && starScore[index] > 3) {
                    log.info(">>> 星星屋(" + (index + 1) + ")等级-1");
                    starScore[index]--;
                };
                break;
            case 7:
                log.info(">> 命中魔法小屋，翻牌[蘑菇牌]下次获取的星星数*2！");
                break;
            case 8:
                log.info(">> 命中魔法小屋，翻牌[重生牌]回到起点！");
                break;
            case 9:
                log.info(">> 命中魔法小屋，翻牌[力量牌]随机工坊等级+1！");
                int indexUp = ArrayUtils.indexOf(points, getRadomIndex());

                if (indexUp != -1 && starScore[indexUp] < maxScore) {
                    log.info(">>> 星星屋(" + (indexUp + 1) + ")等级+1");
                    starScore[indexUp]--;
                };
                break;
            default:
        }

        return magicType;
    }

    private int getRadomIndex() throws Exception {
        final int[] index = new int[]{0, 1, 2, 3, 5, 6, 7, 8, 10, 11, 12, 13, 15, 16, 17, 18};
        return index[random.nextInt(index.length)];
    }
}
