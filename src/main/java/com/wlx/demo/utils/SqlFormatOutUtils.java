package com.wlx.demo.utils;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * SQL 拼装工具
 *
 * @author weilx
 */
public class SqlFormatOutUtils {
    public final static String INSERT_INTO = "INSERT INTO";
    private final static String VALUES = "VALUES";
    public final static String DELETE_FROM = "DELETE FROM";
    private final static String WHERE = "WHERE";
    private final static String AND = "AND";
    private final static String SYSDATE = "SYSDATE";
    private final static String DUAL = "DUAL";
    private final static String SELECT = "SELECT";
    private final static String FROM = "FROM";
    private final static String COUNT = "COUNT";
    private final static String AS = "AS";
    public final static String UNION = "UNION";
    public final static String ALL = "ALL";
    private final static String DECODE = "DECODE";
    public final static String ASTERISK = "*";
    public final static String LEFT_PARENTHESES = "(";
    public final static String RIGHT_PARENTHESES = ")";
    public final static String SEMICOLON = ";";
    public final static String POINT = ".";
    public final static String BLANK = " ";
    public final static String EQUALS = " = ";
    public final static String SINGLE_QUOTATION_MARKS = "'";
    public final static String COMMA = ", ";
    public final static String LINE_BREAK = "\n";
    private final static String END_WITH_ZERO = ".0";

    private final static String DATE_FORMAT_ORACLE = "yyyy-mm-dd hh24:mi:ss";
    private final static SimpleDateFormat FORMAT_YYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.getDefault());

    private static String DEFAULT_SCHEMA = "PRODUCT";
    private static String LAST_WHERE_SQL = "";
    private static String LAST_SCHEMA = "";
    public static final String CHECK_NUMBER_STRING = "#{NUMBER}";

    /**
     * 拼装检查SQL
     * @param schema 用户名
     * @param tableName 表名
     * @param primaryKeys 主键列表
     * @param valueMap 值Map
     * @param remark 备注
     * @return 返回固定格式SQL语句：<br/>
     * <p><code>
     *     SELECT DECODE((SELECT COUNT(*) FROM <i>schema.tableName</i> WHERE <i>primaryKe</i> = <i>value</i>),
     *     <i>count</i>, '正确', '错误') AS 稽核结果, <i>where</i> AS 条件, <i>remark</i> AS 需求编码 FROM DUAL;
     * </code></p>
     * @throws Exception 未定义异常
     */
    public static String formatCheckSql(String schema, @NotNull String tableName, @NotNull String[] primaryKeys,
                                        @NotNull Map<String, Object> valueMap, String remark) throws Exception {
        if (StringUtils.isBlank(schema)) {
            schema = DEFAULT_SCHEMA;
        }
        StringBuilder sql = new StringBuilder(SELECT).append(BLANK)
                // 稽核结果
                .append(DECODE).append(LEFT_PARENTHESES).append(LEFT_PARENTHESES)
                .append(SELECT).append(BLANK)
                .append(COUNT).append(LEFT_PARENTHESES).append(ASTERISK).append(RIGHT_PARENTHESES)
                .append(BLANK).append(FROM).append(BLANK).append(schema).append(POINT).append(tableName)
                .append(BLANK).append(WHERE).append(BLANK);
        StringBuilder where = getWhereSql(primaryKeys, valueMap);

        if (StringUtils.isBlank(where.toString())) {
            throw new IllegalArgumentException("primary key is invalid");
        }
        // 处理SQL单引号
        String convertWhere = where.toString().replaceAll(SINGLE_QUOTATION_MARKS, "''");
        sql.append(where).append(RIGHT_PARENTHESES).append(COMMA).append(CHECK_NUMBER_STRING).append(COMMA)
                .append("'正确'").append(COMMA).append("'错误'").append(RIGHT_PARENTHESES).append(BLANK)
                .append(AS).append(BLANK).append("稽核结果").append(COMMA)
                // 表名
                .append(SINGLE_QUOTATION_MARKS).append(schema).append(POINT).append(tableName).append(SINGLE_QUOTATION_MARKS)
                .append(BLANK).append(AS).append(BLANK).append("表").append(COMMA)
                // 条件
                .append(SINGLE_QUOTATION_MARKS).append(convertWhere).append(SINGLE_QUOTATION_MARKS)
                .append(BLANK).append(AS).append(BLANK).append("条件").append(COMMA)
                // 需求编码
                .append(SINGLE_QUOTATION_MARKS).append(remark).append(SINGLE_QUOTATION_MARKS)
                .append(BLANK).append(AS).append(BLANK).append("需求编码")
                .append(BLANK).append(FROM).append(BLANK).append(DUAL).append(SEMICOLON);

        return sql.toString();
    }

    /**
     * 拼装DELETE&INSERT语句
     * @param schema 用户名
     * @param tableName 表名
     * @param primaryKeys 主键列表
     * @param columns 列
     * @param valueMap 值Map
     * @return
     * <p><code>
     *     DELETE FROM <i>schema.tableName</i> WHERE <i>primaryKey</i> = <i>value</i>;<br />
     *     INSERT INTO <i>schema.tableName</i>(<i>columns</i>) VALUES(<i>values</i>);
     * </code></p>
     * @throws Exception 未定义异常
     */
    public static String format2DeleteInsertSql(String schema, @NotNull String tableName, @NotNull String[] primaryKeys,
                                     @NotNull String[] columns, @NotNull Map<String, Object> valueMap) throws Exception {
        if (StringUtils.isBlank(schema)) {
            schema = DEFAULT_SCHEMA;
        }
        // DELETE 语句开始
        StringBuilder sql = new StringBuilder(DELETE_FROM).append(BLANK).append(schema).append(POINT).append(tableName)
                .append(BLANK).append(WHERE).append(BLANK);
        StringBuilder where = getWhereSql(primaryKeys, valueMap);

        if (StringUtils.isBlank(where.toString())) {
            throw new IllegalArgumentException("primary key is invalid");
        }
        // 判断WHERE条件是否与上一条相同，是则忽略本条DELETE语句, 添加schema
        if (LAST_WHERE_SQL.equals(where.toString()) && LAST_SCHEMA.equals(schema)) {
            sql = new StringBuilder();
        } else {
            // DELETE 语句结束
            sql.append(where).append(SEMICOLON).append(LINE_BREAK);
        }
        LAST_WHERE_SQL = where.toString();
        LAST_SCHEMA = schema;
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

    private static StringBuilder getWhereSql(String[] primaryKeys, Map<String, Object> valueMap) throws Exception {
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
                where.append(" IS NULL");
            } else {
                String formatValue = getFormatValue(valueMap.get(pKey));

                if ("NULL".equalsIgnoreCase(formatValue)) {
                    where.append(" IS NULL");
                } else {
                    where.append(EQUALS).append(formatValue);
                }
            }
        }

        return where;
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
            return SINGLE_QUOTATION_MARKS + NUMBER_FORMAT.format(o) + SINGLE_QUOTATION_MARKS;
        }
        if (StringUtils.isBlank(o.toString())) {
            return "NULL";
        }
        String sValue = SINGLE_QUOTATION_MARKS + o.toString() + SINGLE_QUOTATION_MARKS;

        // 部分符号特殊处理
        if (sValue.contains("&")) {
            sValue = sValue.replaceAll("&", "'||'&'||'");
        }

        return sValue;
    }

    public static String getDefaultSchema() {
        return DEFAULT_SCHEMA;
    }

    public static void setDefaultSchema(String schema) {
        DEFAULT_SCHEMA = schema;
    }

    static {
        NUMBER_FORMAT.setGroupingUsed(false);
    }
}
