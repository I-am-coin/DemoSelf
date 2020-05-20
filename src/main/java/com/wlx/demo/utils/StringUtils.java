package com.wlx.demo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String[] subString(String str, String regex) throws Exception {
        if (StringUtils.isNotBlank(str) && StringUtils.isNotBlank(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            List<String> subStrList = new ArrayList<String>();

            while (matcher.find()) {
                subStrList.add(matcher.group());
            }

            return subStrList.toArray(new String[0]);
        }
        return new String[0];
    }

    public static boolean startWith(String str, String regex) throws Exception {
        if (StringUtils.isNotBlank(str) && StringUtils.isNotBlank(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            String s = null;

            if (matcher.find()) {
                s = matcher.group();
            }

            return (null != s) && str.startsWith(s);
        }
        return false;
    }
}
