package com.wlx.test;

import com.wlx.demo.io.Excel2SqlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IOTest {
    private transient final static Logger log = LogManager.getLogger(IOTest.class);

    @Test
    public void testExcel2Sql() throws Exception {
        Excel2SqlParser.parseSql("E:\\AppData\\TortoiseSVN\\04-解决方案\\2020年\\2020-111\\配置\\集团配置.xlsx", "CRM_BJ_REQ_20201027_0019@weilx");
    }

    @Test
    public void testDouble() throws Exception {
        log.info("BigDecimal.valueOf(654321234567890.0) = " + BigDecimal.valueOf(654321234567890.0));
        log.info("new BigDecimal(654321234567890.0) = " + new BigDecimal(654321234567890.0));
        log.info("new BigDecimal(\"654321234567890.0\") = " + new BigDecimal("654321234567890.0"));
        log.info("new BigDecimal(((Double)654321234567890.0).toString()) = " + new BigDecimal(((Double)654321234567890.0).toString()));

        log.info("\n");

        log.info("BigDecimal.valueOf(654321234567891.0) = " + BigDecimal.valueOf(654321234567891.0));
        log.info("new BigDecimal(654321234567891.0) = " + new BigDecimal(654321234567891.0));
        log.info("new BigDecimal(\"654321234567891.0\") = " + new BigDecimal("654321234567891.0"));
        log.info("new BigDecimal(((Double)654321234567891.0).toString()) = " + new BigDecimal(((Double)654321234567891.0).toString()));
    }

    @Test
    public void testNumberFormat() throws Exception {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setGroupingUsed(false);

        log.info(numberFormat.format(654321234567890.0));
        log.info(numberFormat.format(654321234567891.0));
        log.info(numberFormat.format(654321234567891.1));

        log.info(numberFormat.format((Double)654321234567890.0));
        log.info(numberFormat.format((Double)654321234567891.0));
        log.info(numberFormat.format((Double) 654321234567891.1));


        log.info(numberFormat.format((Object)654321234567890.0));
        log.info(numberFormat.format((Object)654321234567891.0));
        log.info(numberFormat.format((Object) 654321234567891.1));
    }

    @Test
    public void testString() throws Exception {
        String str = "2021-00-01";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp timestamp = new Timestamp(format.parse(str).getTime());

        System.out.println(timestamp);
    }
}
