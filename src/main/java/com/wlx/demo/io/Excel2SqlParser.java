package com.wlx.demo.io;

import com.alibaba.fastjson.JSONObject;
import com.sun.istack.internal.NotNull;
import com.wlx.demo.utils.SqlFormatOutUtils;
import com.wlx.demo.utils.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Excel文件格式解析为DI SQL
 * @author weilx
 */
public class Excel2SqlParser {
    private transient final static Logger log = LogManager.getLogger(Excel2SqlParser.class);

    public static void prepareProdSql(@NotNull String filePath) throws Exception {
        prepareProdSql(filePath, SqlFormatOutUtils.getDefaultSchema());
    }

    @SuppressWarnings("unchecked")
    public static void prepareProdSql(@NotNull String filePath, @NotNull String schema) throws Exception {
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
            log.error("解析EXCEL出错：", e);
        }
        if (null == tableNameList || tableNameList.size() < 1 || MapUtils.isEmpty(primaryKeyMap)
                || MapUtils.isEmpty(columnMap) || MapUtils.isEmpty(tableMap)) {
            log.error("Excel文件解析数据为空，程序结束");;
            return;
        }

        log.error(JSONObject.toJSONString(tableMap));
        File outF = new File(filePath.substring(0, filePath.lastIndexOf("\\")) + File.separator
                + "ExcelExport_" + StringUtils.removeEnd(f.getName(), ".xlsx") +".sql");

        try (OutputStream outputStream = new FileOutputStream(outF);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"), 512)) {
            // 遍历各个表
            for (String fullTableName : tableNameList) {
                if (StringUtils.isBlank(fullTableName)) {
                    log.error("表名为空！");
                    continue;
                }
                String tableName = fullTableName;

                if (fullTableName.contains(".")) {
                    schema = fullTableName.substring(0, fullTableName.indexOf("."));
                    tableName = fullTableName.substring(fullTableName.indexOf(".") + 1, fullTableName.length());
                }
                // 一个表的数据
                List<Map<String, Object>> tableList = tableMap.get(fullTableName);
                // 主键
                List<String> primaryKeys = primaryKeyMap.get(fullTableName);
                // 列
                List<String> columns = columnMap.get(fullTableName);

                if (null == tableList || tableList.size() <= 0) {
                    log.error(tableName + " 表数据为空！");
                    continue;
                }
                if (null == primaryKeys || primaryKeys.size() <= 0) {
                    log.error(tableName + " 未设置主键！");
                    continue;
                }
                if (null == columns || columns.size() <= 0) {
                    log.error(tableName + " 未获取到列！");
                    continue;
                }
                bw.write("-- " + schema + "."+ tableName);
                bw.newLine();
                // 遍历保存表数据
                for (Map<String, Object> tableData : tableList) {
                    // 拼接SQL
                    String sql = SqlFormatOutUtils.preparedSql(schema, tableName,
                            primaryKeys.toArray(new String[0]), columns.toArray(new String[0]), tableData);
                    log.error(sql);
                    // 保存
                    bw.write(sql);
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception e) {
            log.error(e);
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
                log.error("sheet:" + sheet.getSheetName() + ", 行数 <= 1");
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
                        log.error("sheet:" + sheet.getSheetName() + ", title异常，数据过滤");
                        break;
                    }
                    columnList = new ArrayList<>(cellNum);
                    primaryKeyList = new ArrayList<>(2);

                    // 遍历第一行处理列名
                    for (int j = offset; j < cellNum; j++) {
                        String column = String.valueOf(getCellValue(row.getCell(j))).toUpperCase();

                        if (StringUtils.isBlank(column) || column.startsWith("$")) {
                            log.error("sheet:" + sheet.getSheetName() + ", title数据错误，请检查");
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
                        log.error("sheet:" + sheet.getSheetName() + ", 未获取到列名/主键，过滤当前sheet数据，请检查");
                        continue;
                    }
                    // 数据
                    Map<String, Object> rowMap = new HashMap<>(rowNum);
                    // 偏移 offset 个单元格
                    int length = columnList.size() + offset;

                    for (int j = offset; j < length; j++) {
                        if (null == row.getCell(j)) {
                            continue;
                        }
                        rowMap.put(columnList.get(j), getCellValue(row.getCell(j)));
                    }
                    sheetList.add(rowMap);
                }
            }
            if (sheetList.size() > 0) {
                columnMap.put(sheet.getSheetName(), columnList);
                primaryKeyMap.put(sheet.getSheetName(), primaryKeyList);
                tableNameList.add(sheet.getSheetName());
                tableMap.put(sheet.getSheetName().toUpperCase(), sheetList);
                if (log.isInfoEnabled()) {
                    log.info("------------------------------------------------------------------");
                    log.info(sheet.getSheetName() + "获取主键数：" + primaryKeyList.size());
                    log.info(sheet.getSheetName() + "获取列数：" + columnList.size());
                    log.info(sheet.getSheetName() + "获取数据数：" + sheetList.size());
                    log.info("------------------------------------------------------------------");
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
