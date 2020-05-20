package com.wlx.demo.io;

import com.wlx.demo.utils.NumberUtils;
import com.wlx.demo.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public class FileChapterSpliter {
    private transient static final Log log = LogFactory.getLog(FileChapterSpliter.class);


    public static void main(String[] args) {
        final String basePath = "F:\\ALL\\";
        final String baseFilePath = basePath + "all.txt";
        final String chapterSplitReg1 = "第[零一二三四五六七八九十百千万亿]+章?";
        final String chapterSplitReg2 = "第?[零一二三四五六七八九十百千万亿]+章";
        final String chapterSplitReg3 = "[零一二三四五六七八九十百千万亿]+";
        final String[] filters = new String[]{"全文字更新，TXT下载，尽在 小说骑士 http://www\\.xs74\\.com/", "http://www\\.23wr\\.com"};
        final String[] fileNameFilters = new String[] {"^\\$", "/", "\\*", "\\|", "<", ">", "\\:", "\"", "\\?"};

        InputStream inputStream = null;
        BufferedReader br = null;

        log.error("[START]");
        try {
            inputStream = new FileInputStream(new File(baseFilePath));
            br = new BufferedReader(new InputStreamReader(inputStream, "GBK"), 256);
            String line = br.readLine();
            BufferedWriter bw = null;

            while (null != line) {
                if (StringUtils.isNotBlank(line)) {
                    for (String filter : filters) {
                        line = line.replaceAll(filter, "");
                    }
                }
                if (StringUtils.isNotBlank(line) && (StringUtils.startWith(line, chapterSplitReg1) || StringUtils.startWith(line, chapterSplitReg2))) {
                    if (null != bw) { // 关闭上一个文件流
                        try {
                            bw.flush();
                            bw.close();
                        } catch (Exception e) {
                            log.error("关闭上一个文件流出错：", e);
                        } finally {
                            bw = null;
                        }
                    }
                    for (String fileNameFilter : fileNameFilters) {
                        line = line.replaceAll(fileNameFilter, "");
                    }
                    String[] strings = StringUtils.subString(line, chapterSplitReg3);
                    String sub1 = line.startsWith("第") ? line.substring(1 + strings[0].length()) : line.substring(strings[0].length());
                    String sub2 = sub1.startsWith("章") ? sub1.substring(1) : sub1;
                    String subFileName = "第" + NumberUtils.zhNumberFormat(strings[0]) + "章" + sub2;
                    log.error("当前使用文件为:" + subFileName);
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(basePath + "a\\" + subFileName + ".txt")), "UTF-8"), 256);
                }
                if (null != bw) { // 写入文件
                    bw.write(line);
                    bw.write("\n");
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            log.error("ERROR: ", e);
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("关闭输入流出错：", e);
            }
        }
        log.error("[END]");
    }
}
