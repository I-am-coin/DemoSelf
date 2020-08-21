package com.wlx.test;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

public class DateTest {

    /**
     * 不能使用 java.sql 包下的时间类
     *
     */
    @Test
    public void testSqlTime() throws Exception {
        long sysTime = System.currentTimeMillis();
        Timestamp sqlTime = new Timestamp(sysTime); // 秒和纳秒分开存储
        Date sqlDate = new Date(sysTime);

        System.out.println(sqlTime);
        System.out.println(sqlDate);
        System.out.println(sqlDate.after(sqlTime)); // ERROR
    }
}
