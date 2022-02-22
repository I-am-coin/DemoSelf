package com.wlx.demo.datastructure.stack;

/**
 * 图像渲染
 * 有一幅以二维整数数组表示的图画，每一个整数表示该图画的像素值大小，数值在 0 到 65535 之间。
 *
 * 给你一个坐标 (sr, sc) 表示图像渲染开始的像素值（行 ，列）和一个新的颜色值 newColor，让你重新上色这幅图像。
 *
 * 为了完成上色工作，从初始坐标开始，记录初始坐标的上下左右四个方向上像素值与初始坐标相同的相连像素点，
 * 接着再记录这四个方向上符合条件的像素点与他们对应四个方向上像素值与初始坐标相同的相连像素点，……，
 * 重复该过程。将所有有记录的像素点的颜色值改为新的颜色值。
 *
 * 最后返回经过上色渲染后的图像
 *
 * image 和 image[0] 的长度在范围 [1, 50] 内。
 * 给出的初始点将满足 0 <= sr < image.length 和 0 <= sc < image[0].length。
 * image[i][j] 和 newColor 表示的颜色值在范围 [0, 65535]内。
 *
 * 作者：力扣 (LeetCode)
 * 链接：https://leetcode-cn.com/leetbook/read/queue-stack/g02cj/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * @author weilx
 */
public class ImageRecolor {
    public static void main(String[] args) {
        int[][] image = {{1, 1, 1}, {1, 1, 0}, {1, 0, 1}};

        for (int[] im : image) {
            for (int i : im) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

        new ImageRecolor().floodFill(image, 1, 1, 2);

        for (int[] im : image) {
            for (int i : im) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        dfs(image, sr, sc, newColor, image[sr][sc]);
        return image;
    }


    public int[][] dfs(int[][] image, int sr, int sc, int newColor, int oldColor) {
        if (image[sr][sc] == newColor) {
            return image;
        }
        // 上
        if (sr + 1 < image.length && image[sr + 1][sc] == oldColor) {
            image[sr][sc] = newColor;
            floodFill(image, sr + 1, sc, newColor);
        }
        // 右
        if (sc + 1 < image[0].length && image[sr][sc + 1] == oldColor) {
            image[sr][sc] = newColor;
            floodFill(image, sr, sc + 1, newColor);
        }
        // 下
        if (sr - 1 > -1  && image[sr - 1][sc] == oldColor) {
            image[sr][sc] = newColor;
            floodFill(image, sr - 1, sc, newColor);
        }
        // 左
        if (sc - 1 > -1 && image[sr][sc - 1] == oldColor) {
            image[sr][sc] = newColor;
            floodFill(image, sr, sc - 1, newColor);
        }
        image[sr][sc] = newColor;
        return image;
    }

}
