package com.wlx.test;

import com.wlx.demo.io.Excel2SqlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;

public class IOTest {
    private transient final static Logger log = LogManager.getLogger(IOTest.class);

    @Test
    public void testExcel2Sql() throws Exception {
        Excel2SqlParser.prepareProdSql("E:\\AppData\\TortoiseSVN\\04-解决方案\\2020年\\2020-083\\PCE配置.xlsx");
    }

    @Test
    public void testDouble() throws Exception {
        Double d = 1920001001001.0;

        System.out.println(d);
        System.out.println(String.valueOf(d));
        System.out.println(Double.toString(d));
        System.out.println(d.doubleValue());
        System.out.println(String.valueOf(d.doubleValue()));

        System.out.println(new BigDecimal(d.toString()));
    }
}
