package com.wlx.demo.datastructure.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 请你判断一个 9 x 9 的数独是否有效。只需要 根据以下规则 ，验证已经填入的数字是否有效即可。
 *
 * 数字 1-9 在每一行只能出现一次。
 * 数字 1-9 在每一列只能出现一次。
 * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。（请参考示例图）
 *  
 *
 * 注意：
 *
 * 一个有效的数独（部分已被填充）不一定是可解的。
 * 只需要根据以上规则，验证已经填入的数字是否有效即可。
 * 空白格用 '.' 表示。
 *
 * @author weilx
 * @date 2022-6-28
 */
public class ValidSudoku {
    public static void main(String[] args) {
        char[][] board =
                {{'5','3','.','.','7','.','.','.','.'}
                ,{'6','.','.','1','9','5','.','.','.'}
                ,{'.','9','8','.','.','.','.','6','.'}
                ,{'8','.','.','.','6','.','.','.','3'}
                ,{'4','.','.','8','.','3','.','.','1'}
                ,{'7','.','.','.','2','.','.','.','6'}
                ,{'.','6','.','.','.','.','2','8','.'}
                ,{'.','.','.','4','1','9','.','.','5'}
                ,{'.','.','.','.','8','.','.','7','9'}};

        boolean validSudoku = isValidSudoku(board);
        System.out.println(validSudoku);
    }

    private static boolean isValidSudoku(char[][] board) {
        int x = board.length;
        int y = board[0].length;
        Set<Character>[] xSets = new Set[x];
        Set<Character>[] ySets = new Set[y];
        Set<Character>[] xySets = new Set[x * y / 9];
        int num = 0;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (board[i][j] == '.') {
                    continue;
                }
                num++;
                // 一行
                if (xSets[i] == null) {
                    xSets[i] = new HashSet<>();
                }
                xSets[i].add(board[i][j]);

                // 一列
                if (ySets[j] == null) {
                    ySets[j] = new HashSet<>();
                }
                ySets[j].add(board[i][j]);

                // 一个方阵
                int m = 3 * (i / 3) + j / 3;

                if (xySets[m] == null) {
                    xySets[m] = new HashSet<>();
                }
                xySets[m].add(board[i][j]);
            }
        }
        // 计算
        int num2 = 0;

        for (Set<Character> xSet : xSets) {
            if (xSet == null) {
                continue;
            }
            num2 += xSet.size();
        }
        for (Set<Character> ySet : ySets) {
            if (ySet == null) {
                continue;
            }
            num2 += ySet.size();
        }
        for (Set<Character> xySet : xySets) {
            if (xySet == null) {
                continue;
            }
            num2 += xySet.size();
        }
        return num2 == num * 3;
    }
}
