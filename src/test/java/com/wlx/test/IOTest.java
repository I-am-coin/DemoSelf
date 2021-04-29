package com.wlx.test;

import com.wlx.demo.io.Excel2SqlParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class IOTest {
    private transient final static Logger log = LogManager.getLogger(IOTest.class);

    @Test
    public void testExcel2Sql() throws Exception {
        Excel2SqlParser.parseSql("E:\\AppData\\TortoiseSVN\\04-解决方案\\2021年\\03-1\\配置\\PCE配置.xlsx",
                "CRM_BJ_REQ_20200623_0022@weilx");
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
        String str = "50017030004:BROADBAND;50017030005:BAND_UNIT;50017030006:IP_NUMBER;000174001:TOTAL_FEE;000178501:ONCE_FEE;\n";

        String[] split = StringUtils.split(str, ";");


        for (String s : split) {
            System.out.println("0 : " + StringUtils.split(s, ":")[0]);
            System.out.println("1 : " + StringUtils.split(s, ":")[1]);
        }
    }

    @Test
    public void testRandom() throws Exception {

        for (int i=1; i<=10000; i++) {
//            int math = (int)Math.round(Math.random() * 9);
            int random = new Random().nextInt(10);

            System.out.println(String.format("第%05d次随机，值 = %d", i, random));
        }
    }
}
