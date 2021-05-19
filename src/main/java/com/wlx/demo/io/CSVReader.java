package com.wlx.demo.io;

import com.wlx.demo.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 文件读取
 * @author weilx
 */
public class CSVReader {
    private transient final static Logger LOG = LogManager.getLogger(CSVReader.class);

    public static String[][] parseCSVFile(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.endsWith(".csv")) {
            LOG.error("该方法只支持.csv文件解析！");
            throw new Exception("该方法只支持.csv文件解析！");
        }
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            LOG.error("文件不存在！");
            throw new Exception("文件不存在！");
        }
        List<String[]> formatList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = br.readLine();

            while (StringUtils.isNotBlank(line)) {
                String[] cols = StringUtils.split(line, ',');
                formatList.add(cols);
                line = br.readLine();
            }
        } catch (IOException e) {
            LOG.error("读取文件异常：", e);
        }
        return formatList.toArray(new String[0][]);
    }
}
