package com.wlx.demo.utils;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * SQL 拼装工具
 *
 * @author weilx
 */
public class SqlFormatOutUtils {
    private final static String INSERT_INTO = "INSERT INTO";
    private final static String VALUES = "VALUES";
    private final static String DELETE_FROM = "DELETE FROM";
    private final static String WHERE = "WHERE";
    private final static String AND = "AND";
    private final static String SYSDATE = "SYSDATE";
    private final static String LEFT_PARENTHESES = "(";
    private final static String RIGHT_PARENTHESES = ")";
    private final static String SEMICOLON = ";";
    private final static String POINT = ".";
    private final static String BLANK = " ";
    private final static String EQUALS = " = ";
    private final static String SINGLE_QUOTATION_MARKS = "'";
    private final static String COMMA = ", ";
    private final static String LINE_BREAK = "\n";
    private final static String END_WITH_ZERO = ".0";
    private static String DEFAULT_SCHEMA = "PROD";

    private final static String DATE_FORMAT_ORACLE = "yyyy-mm-dd hh24:mi:ss";
    private final static SimpleDateFormat FORMAT_YYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String preparedSql(String schema, @NotNull String tableName, @NotNull String[] primaryKeys,
                                     @NotNull String[] columns, @NotNull Map<String, Object> valueMap) throws Exception {
        if (StringUtils.isBlank(schema)) {
            schema = DEFAULT_SCHEMA;
        }
        // DELETE 语句开始
        StringBuilder sql = new StringBuilder(DELETE_FROM).append(BLANK).append(schema).append(POINT).append(tableName)
                .append(BLANK).append(WHERE).append(BLANK);
        StringBuilder where = new StringBuilder();

        // 遍历主键设置 WHERE 条件
        for (String pKey : primaryKeys) {
            if (StringUtils.isBlank(pKey)) {
                continue;
            }
            if (StringUtils.isNotBlank(where.toString())) {
                where.append(BLANK).append(AND).append(BLANK);
            }
            where.append(pKey.toUpperCase());

            if (valueMap.get(pKey) == null) {
                where.append("IS NULL");
            } else {
                String formatValue = getFormatValue(valueMap.get(pKey));

                if ("NULL".equalsIgnoreCase(formatValue)) {
                    where.append("IS NULL");
                } else {
                    where.append(EQUALS).append(formatValue);
                }
            }
        }
        if (StringUtils.isBlank(where.toString())) {
            throw new IllegalArgumentException("primary key is invalid");
        }
        // DELETE 语句结束
        sql.append(where).append(SEMICOLON).append(LINE_BREAK);
        // INSERT 语句开始
        sql.append(INSERT_INTO).append(BLANK).append(schema).append(POINT).append(tableName).append(LEFT_PARENTHESES);
        StringBuilder column = new StringBuilder();
        StringBuilder values = new StringBuilder();

        // 拼接表字段
        for (String key : columns) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            if (StringUtils.isNotBlank(column.toString())) {
                column.append(COMMA);
                values.append(COMMA);
            }
            column.append(key.toUpperCase());
            values.append(SqlFormatOutUtils.getFormatValue(valueMap.get(key)));
        }
        if (StringUtils.isBlank(column.toString())) {
            throw new IllegalArgumentException("parameter column is valid");
        }
        // INSERT 语句结束
        sql.append(column).append(RIGHT_PARENTHESES).append(LINE_BREAK).append(VALUES).append(LEFT_PARENTHESES)
                .append(values).append(RIGHT_PARENTHESES).append(SEMICOLON).append(LINE_BREAK);
        return sql.toString();
    }

    private static String getFormatValue(Object o) throws Exception {
        if (null == o) {
            return "NULL";
        }
        if (o instanceof Date) {
            Date date = (Date) o;
            return "TO_DATE('" + FORMAT_YYYYMMDDHHMMSS.format(date) + "', '" + DATE_FORMAT_ORACLE + "')";
        }
        if (o instanceof String && SYSDATE.equalsIgnoreCase((String) o)) {
            return SYSDATE;
        }
        if (o instanceof Number) {
            // 这一步转化防止使用科学计数法
            String bigDecimalStr = new BigDecimal(o.toString()).toString();

            if (bigDecimalStr.endsWith(END_WITH_ZERO)) {
                return SINGLE_QUOTATION_MARKS +StringUtils.removeEnd(bigDecimalStr, END_WITH_ZERO) + SINGLE_QUOTATION_MARKS;
            }
            return SINGLE_QUOTATION_MARKS + bigDecimalStr + SINGLE_QUOTATION_MARKS;
        }
        return SINGLE_QUOTATION_MARKS + o.toString() + SINGLE_QUOTATION_MARKS;
    }

    public static String getDefaultSchema() {
        return DEFAULT_SCHEMA;
    }

    public static void setDefaultSchema(String schema) {
        DEFAULT_SCHEMA = schema;
    }
}