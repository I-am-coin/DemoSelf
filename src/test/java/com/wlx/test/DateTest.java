package com.wlx.test;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void testRegex() {
        String regex = "\\[[a-zA-Z]+]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher("anb1[adad]ada[ge]omd[gawe[awg]]");

        while (matcher.find()) {

            System.out.println(matcher.group());
        }
    }
}
