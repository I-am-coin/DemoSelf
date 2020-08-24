package com.wlx.test;

import com.wlx.demo.io.Excel2SqlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class IOTest {
    private transient final static Logger log = LogManager.getLogger(IOTest.class);

    @Test
    public void testExcel2Sql() throws Exception {
        Excel2SqlParser.prepareProdSql("E:\\AppData\\TortoiseSVN\\04-解决方案\\2020年\\2020-083\\全网配置.xlsx");
    }

    @Test
    public void testDouble() throws Exception {
        System.out.println("BigDecimal.valueOf(654321234567890.0) = " + BigDecimal.valueOf(654321234567890.0));
        System.out.println("new BigDecimal(654321234567890.0) = " + new BigDecimal(654321234567890.0));
        System.out.println("new BigDecimal(\"654321234567890.0\") = " + new BigDecimal("654321234567890.0"));
        System.out.println("new BigDecimal(((Double)654321234567890.0).toString()) = " + new BigDecimal(((Double)654321234567890.0).toString()));

        System.out.println("\n");

        System.out.println("BigDecimal.valueOf(654321234567891.0) = " + BigDecimal.valueOf(654321234567891.0));
        System.out.println("new BigDecimal(654321234567891.0) = " + new BigDecimal(654321234567891.0));
        System.out.println("new BigDecimal(\"654321234567891.0\") = " + new BigDecimal("654321234567891.0"));
        System.out.println("new BigDecimal(((Double)654321234567891.0).toString()) = " + new BigDecimal(((Double)654321234567891.0).toString()));
    }

    @Test
    public void testNumberFormat() throws Exception {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setGroupingUsed(false);

        System.out.println(numberFormat.format(654321234567890.0));
        System.out.println(numberFormat.format(654321234567891.0));
        System.out.println(numberFormat.format(654321234567891.1));

        System.out.println(numberFormat.format((Double)654321234567890.0));
        System.out.println(numberFormat.format((Double)654321234567891.0));
        System.out.println(numberFormat.format((Double) 654321234567891.1));


        System.out.println(numberFormat.format((Object)654321234567890.0));
        System.out.println(numberFormat.format((Object)654321234567891.0));
        System.out.println(numberFormat.format((Object) 654321234567891.1));
    }
}
