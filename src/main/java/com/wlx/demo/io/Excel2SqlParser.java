package com.wlx.demo.io;

import com.alibaba.fastjson.JSONObject;
import com.sun.istack.internal.NotNull;
import com.wlx.demo.utils.SqlFormatOutUtils;
import com.wlx.demo.utils.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

/**
 * Excel文件格式解析为DI SQL
 * @author weilx
 */
public class Excel2SqlParser {
    private transient final static Logger LOG = LogManager.getLogger(Excel2SqlParser.class);

    public static void parseSql(@NotNull String filePath, String remark) throws Exception {
        parseSql(filePath, SqlFormatOutUtils.getDefaultSchema(), remark);
    }

    @SuppressWarnings("unchecked")
    public static void parseSql(@NotNull String filePath, @NotNull String schema, String remark) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        File f = new File(filePath);

        if (!(f.exists() && f.isFile() && f.getName().endsWith(".xlsx"))) {
            return;
        }
        Map<String, Object> excelMap = null;
        List<String> tableNameList = null;
        Map<String, List<String>> primaryKeyMap = null;
        Map<String, List<String>> columnMap = null;
        Map<String, List<Map<String, Object>>> tableMap = null;

        try(InputStream inputStream = new FileInputStream(f)) {
            // 修改默认库名
            SqlFormatOutUtils.setDefaultSchema(schema);
            excelMap = prepareExcelContentX(inputStream);

            if (MapUtils.isEmpty(excelMap)) {
                throw new Exception("解析Excel数据为空！");
            }
            tableNameList = (List<String>)excelMap.get("TABLE_NAME");
            primaryKeyMap = (Map<String, List<String>>)excelMap.get("PRIMARY_KEY");
            columnMap = (Map<String, List<String>>)excelMap.get("COLUMNS");
            tableMap = (Map<String, List<Map<String, Object>>>) excelMap.get("TABLE_DATA");
        } catch (Exception e) {
            LOG.error("解析EXCEL出错：", e);
        }
        if (null == tableNameList || tableNameList.size() < 1 || MapUtils.isEmpty(primaryKeyMap)
                || MapUtils.isEmpty(columnMap) || MapUtils.isEmpty(tableMap)) {
            LOG.error("Excel文件解析数据为空，程序结束");;
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(JSONObject.toJSONString(tableMap));
        }
        print2File(StringUtils.removeEnd(filePath, ".xlsx") + "-SQL.sql",
                tableNameList, schema, primaryKeyMap, columnMap, tableMap, remark);
    }

    private static void print2File(String filePath, List<String> tableNameList, String schema,
                                   Map<String, List<String>> primaryKeyMap, Map<String, List<String>> columnMap,
                                   Map<String, List<Map<String, Object>>> tableMap, String remark) {
        File outF = new File(filePath);
        int record = 1;
        Map<String, List<String>> formatSqlMap = new HashMap<>(tableNameList.size());
        Map<String, List<String>> checkSqlMap = new HashMap<>(tableNameList.size());
        List<String> checkSqlList = new ArrayList<>();
        List<String> formatSqlList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        String lastTableName = StringUtils.EMPTY;

        try (OutputStream outputStream = new FileOutputStream(outF);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"), 2048)) {
            // 遍历各个表
            for (String fullTableName : tableNameList) {
                if (StringUtils.isBlank(fullTableName)) {
                    LOG.error("表名为空！");
                    continue;
                }
                String newSchema = schema;
                String tableName = fullTableName;

                if (fullTableName.contains(".")) {
                    newSchema = fullTableName.substring(0, fullTableName.indexOf("."));
                    tableName = fullTableName.substring(fullTableName.indexOf(".") + 1, fullTableName.length());
                }
                // 一个表的数据
                List<Map<String, Object>> tableList = tableMap.get(fullTableName);
                // 主键
                List<String> primaryKeys = primaryKeyMap.get(fullTableName);
                // 列
                List<String> columns = columnMap.get(fullTableName);

                if (null == tableList || tableList.size() <= 0) {
                    LOG.error(tableName + " 表数据为空！");
                    continue;
                }
                if (null == primaryKeys || primaryKeys.size() <= 0) {
                    LOG.error(tableName + " 未设置主键！");
                    continue;
                }
                if (null == columns || columns.size() <= 0) {
                    LOG.error(tableName + " 未获取到列！");
                    continue;
                }
                bw.write("-- " + newSchema + "."+ tableName);
                bw.newLine();
                // 遍历保存表数据
                for (Map<String, Object> tableData : tableList) {
                    // 拼接SQL
                    String sql = SqlFormatOutUtils.format2DeleteInsertSql(newSchema, tableName,
                            primaryKeys.toArray(new String[0]), columns.toArray(new String[0]), tableData);

                    // 如果 sql 包含 DELETE FROM 语句，则生成检查语句，否则 record++
                    if (sql.contains(SqlFormatOutUtils.DELETE_FROM)) {
                        if (checkSqlList.size() > 0) {
                            // 从 checkSqlList 中取出最后一条记录，替换 CHECK_NUMBER_STRING 为真实值
                            String lastSql = checkSqlList.get(checkSqlList.size() - 1);
                            checkSqlList.remove(checkSqlList.size() - 1);
                            checkSqlList.add(lastSql.replaceAll("#\\{NUMBER}",
                                    String.valueOf(record)));
                            formatSqlList.add(sqlBuilder.toString());
                            sqlBuilder.delete(0, sqlBuilder.length());
                            record = 1;
                        }
                        // 如果是新的一个表，则将 checkSqlList 放入
                        if (StringUtils.isNotBlank(lastTableName) && !StringUtils.equals(lastTableName, newSchema + SqlFormatOutUtils.POINT + tableName)) {
                            checkSqlMap.put(lastTableName, checkSqlList);
                            formatSqlMap.put(lastTableName, formatSqlList);
                            formatSqlList = new ArrayList<>();
                            checkSqlList = new ArrayList<>();
                        }
                        // 生成检查脚本
                        String checkSql = SqlFormatOutUtils.formatCheckSql(newSchema, tableName,
                                primaryKeys.toArray(new String[0]), tableData, remark);
                        checkSqlList.add(StringUtils.removeEnd(checkSql, SqlFormatOutUtils.SEMICOLON)
                                + SqlFormatOutUtils.BLANK + SqlFormatOutUtils.UNION
                                + SqlFormatOutUtils.BLANK + SqlFormatOutUtils.ALL);
                    } else {
                        record++;
                    }
                    sqlBuilder.append(sql).append(SqlFormatOutUtils.LINE_BREAK);
                    lastTableName = newSchema + SqlFormatOutUtils.POINT + tableName;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(sql);
                    }
                    if (StringUtils.isNotBlank(sql)) {
                        // 保存
                        bw.write(sql);
                        bw.newLine();
                    }
                }
                bw.flush();
            }
            // 最后一个
            String lastSql = checkSqlList.get(checkSqlList.size() - 1);
            checkSqlList.remove(checkSqlList.size() - 1);
            checkSqlList.add(lastSql.replaceAll("#\\{NUMBER}",
                    String.valueOf(record)));
            formatSqlList.add(sqlBuilder.toString());
            checkSqlMap.put(lastTableName, checkSqlList);
            formatSqlMap.put(lastTableName, formatSqlList);
        } catch (Exception e) {
            LOG.error("数据格式化异常：", e);
        }

        // 生产检查脚本文件
        try {
            LOG.error("检查脚本生成开始……");
            if (LOG.isDebugEnabled()) {
                LOG.debug("CHECK_SQL_MAP: " + JSONObject.toJSONString(checkSqlMap));
                LOG.debug("FORMAT_SQL_MAP: " + JSONObject.toJSONString(formatSqlMap));
            }
            printCheckSql2Excel(StringUtils.removeEnd(filePath, "SQL.sql") + "CHECK.xlsx",
                    formatSqlMap, checkSqlMap);
        } catch (Exception e) {
            LOG.error("检查脚本文件生成失败：", e);
        }
        LOG.error("检查脚本生成结束……");
    }

    private static void printCheckSql2Excel(String filePath, Map<String, List<String>> formatSqlMap,
                                            Map<String, List<String>> checkSqlMap) throws Exception {
        XSSFWorkbook checkExcelWorkBook = new XSSFWorkbook();
        // 创建一个 Sheet 页，名字为 CHECK_SQL
        XSSFSheet checkSqlSheet = checkExcelWorkBook.createSheet(WorkbookUtil.createSafeSheetName("CHECK_SQL"));
        // 设置列宽
        checkSqlSheet.setColumnWidth(0, 21 * 256);
        checkSqlSheet.setColumnWidth(1, 90 * 256);
        checkSqlSheet.setColumnWidth(2, 90 * 256);
        XSSFCreationHelper creationHelper = checkExcelWorkBook.getCreationHelper();
        // 第一行标题
        XSSFRow titleRow = checkSqlSheet.createRow(0);
        titleRow.setHeightInPoints(15);
        // 标题样式
        XSSFCellStyle titleStyle = checkExcelWorkBook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont titleFont = checkExcelWorkBook.createFont();
        titleFont.setBold(true);
        titleFont.setFontName("微软雅黑");
        titleFont.setFontHeightInPoints((short)10);

        titleStyle.setFont(titleFont);
        // 设置标题
        XSSFCell titleTableNameCell = titleRow.createCell(0, CellType.STRING);
        titleTableNameCell.setCellValue("TABLE_NAME");
        titleTableNameCell.setCellStyle(titleStyle);
        XSSFCell titleSqlCell = titleRow.createCell(1, CellType.STRING);
        titleSqlCell.setCellValue("FORMAT_SQL");
        titleSqlCell.setCellStyle(titleStyle);
        XSSFCell titleCheckSqlCell = titleRow.createCell(2, CellType.STRING);
        titleCheckSqlCell.setCellValue("CHECK_SQL");
        titleCheckSqlCell.setCellStyle(titleStyle);
        // 内容样式
        XSSFCellStyle contentStyle = checkExcelWorkBook.createCellStyle();
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setWrapText(true);
        XSSFFont contentFont = checkExcelWorkBook.createFont();
        contentFont.setFontName("微软雅黑");
        contentFont.setFontHeightInPoints((short)10);
        contentStyle.setFont(contentFont);
        int k = 1;

        for (String tableName : checkSqlMap.keySet()) {
            if (null == checkSqlMap.get(tableName)) {
                LOG.error("检查脚本表数据为空：" + tableName);
                continue;
            }
            for (int j = 0; j < checkSqlMap.get(tableName).size(); j++) {
                // 填充每一行的内容
                XSSFRow row = checkSqlSheet.createRow(k++);
                row.setHeightInPoints(15);
                XSSFCell cell1 = row.createCell(0);
                cell1.setCellStyle(contentStyle);
                cell1.setCellValue(tableName);
                XSSFCell cell2 = row.createCell(1);
                cell2.setCellStyle(contentStyle);
                cell2.setCellValue(creationHelper.createRichTextString(formatSqlMap.get(tableName).get(j)));
                XSSFCell cell3 = row.createCell(2);
                cell3.setCellStyle(contentStyle);
                cell3.setCellValue(creationHelper.createRichTextString(checkSqlMap.get(tableName).get(j)));
            }
        }
        // excel 内容拼装完成， 保存
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            checkExcelWorkBook.write(outputStream);
        } catch (Exception e) {
            LOG.error("保存检查脚本异常：", e);
        }
    }

    /**
     * 解析Excel数据
     * @param inStream excel文件输入流
     * @return Map&lt;String, Object&gt;
     * @throws Exception 异常信息
     */
    private static Map<String, Object> prepareExcelContentX(InputStream inStream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(inStream);
        Iterator<Sheet> sheetIterator = workbook.iterator();
        // 包含表数据，表名，列名，主键
        Map<String, Object> workMap = new HashMap<>();
        // 表数据，结构：TABLE_NAME->List<COLUMN_NAME->VALUE>
        Map<String, List<Map<String, Object>>> tableMap = new HashMap<>(16);
        // 表名
        List<String> tableNameList = new ArrayList<>(16);
        // 主键，结构：TABLE_NAME->List<VALUE>
        Map<String, List<String>> primaryKeyMap = new HashMap<>(16);
        // 列名，结构：TABLE_NAME->List<VALUE>
        Map<String, List<String>> columnMap = new HashMap<>(16);

        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            List<Map<String, Object>> sheetList = new ArrayList<>();
            int rowNum = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;

            // 除了标题啥都没有
            if (rowNum <= 1) {
                LOG.error("sheet:" + sheet.getSheetName() + ", 行数 <= 1");
                continue;
            }
            // 标题（列名）
            List<String> columnList = null;
            List<String> primaryKeyList = null;
            // 标题行的偏移量
            short offset = 0;

            // 遍历sheet的每一行
            for (int i = sheet.getFirstRowNum(); i < rowNum; i++) {
                Row row = sheet.getRow(i);

                // 第一行 title
                if (sheet.getFirstRowNum() == i) {
                    offset = row.getFirstCellNum();
                    int cellNum = row.getLastCellNum() - offset;

                    // 不可能哪个表只有1列吧？？？
                    if (cellNum <= 1) {
                        LOG.error("sheet:" + sheet.getSheetName() + ", title异常，数据过滤");
                        break;
                    }
                    columnList = new ArrayList<>(cellNum);
                    primaryKeyList = new ArrayList<>(2);

                    // 遍历第一行处理列名
                    for (int j = offset; j < cellNum; j++) {
                        String column = String.valueOf(getCellValue(row.getCell(j))).toUpperCase();

                        if (StringUtils.isBlank(column) || column.startsWith("$")) {
                            LOG.error("sheet:" + sheet.getSheetName() + ", title数据错误，请检查");
                            columnList = null;
                            break;
                        }
                        // 添加主键
                        if (column.startsWith("#")) {
                            column = column.substring(1, column.length());
                            primaryKeyList.add(column);
                        }
                        columnList.add(column);
                    }
                } else {
                    if (null == columnList || columnList.size() <= 0 || primaryKeyList.size() <= 0) {
                        LOG.error("sheet:" + sheet.getSheetName() + ", 未获取到列名/主键，过滤当前sheet数据，请检查");
                        break;
                    }
                    // 数据
                    Map<String, Object> rowMap = new HashMap<>(rowNum);
                    // 偏移 offset 个单元格
                    int length = columnList.size() + offset;
                    int size = length;

                    for (int j = offset; j < length; j++) {
                        if (null == row.getCell(j)) {
                            continue;
                        }
                        Object object = getCellValue(row.getCell(j));

                        if (null == object || (object instanceof String && (StringUtils.isBlank((String)object) || "$NA".equals(object)))) {
                            size --;
                        }
                        rowMap.put(columnList.get(j), object);
                    }
                    if (size == 0) {
                        LOG.error("sheet:" + sheet.getSheetName() + ", " + rowNum + "行数据为空，结束后续处理");
                        break;
                    }
                    sheetList.add(rowMap);
                }
            }
            if (sheetList.size() > 0) {
                columnMap.put(sheet.getSheetName(), columnList);
                primaryKeyMap.put(sheet.getSheetName(), primaryKeyList);
                tableNameList.add(sheet.getSheetName());
                tableMap.put(sheet.getSheetName().toUpperCase(), sheetList);
                if (LOG.isInfoEnabled()) {
                    LOG.info("------------------------------------------------------------------");
                    LOG.info(sheet.getSheetName() + "获取主键数：" + primaryKeyList.size());
                    LOG.info(sheet.getSheetName() + "获取列数：" + columnList.size());
                    LOG.info(sheet.getSheetName() + "获取数据数：" + sheetList.size());
                    LOG.info("------------------------------------------------------------------");
                }
            }
        }
        if (MapUtils.isNotEmpty(tableMap)) {
            workMap.put("TABLE_DATA", tableMap);
            workMap.put("TABLE_NAME", tableNameList);
            workMap.put("PRIMARY_KEY", primaryKeyMap);
            workMap.put("COLUMNS", columnMap);
        }

        return workMap;
    }

    private static Object getCellValue(Cell cell) throws Exception {
        Object cellValue = null;

        switch (cell.getCellType()) {
            // 数字 | 日期
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue();
                } else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            // 字符串
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            // 空白
            case BLANK:
                cellValue = "";
                break;
            // 公式
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            // bool
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            // 错误格式
            case ERROR:
                cellValue = "$ERROR";
                break;
            // 未知格式
            default:
                cellValue = "$NA";
                break;
        }

        return cellValue;
    }
}
