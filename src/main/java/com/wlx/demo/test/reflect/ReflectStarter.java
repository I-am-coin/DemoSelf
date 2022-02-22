package com.wlx.demo.test.reflect;

import com.wlx.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReflectStarter {

    public static void main(String[] args) {
        try {
            // 1. 直接调用
//            new ReflectDemo().testException(ArrayIndexOutOfBoundsException.class);

            // 2. 反射
            String className = "com.wlx.demo.test.reflect.ReflectDemo";
            String strMethod = "testException";
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(strMethod, Class.class);
            method.invoke(clazz.newInstance(), ArrayIndexOutOfBoundsException.class);
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error("处理前堆栈信息：\r\n{}", stackTrace);

            if (StringUtils.isNotBlank(stackTrace)) {
                String[] stackTraces = StringUtils.split(stackTrace, "\r\n");
                List<String> stackTraceList = new ArrayList<>();
                String headStr = "\tat ";
                String projectHead = "com.wlx.";
                String blankLine = "\t...";

                for (String st: stackTraces) {
                    if (StringUtils.isBlank(st)) {
                        continue;
                    }
                    if (st.startsWith(headStr)) {
                        if (st.startsWith(headStr + projectHead)) {
                            stackTraceList.add(st);
                        } else {
                            int size = stackTraceList.size();

                            if (size > 0 && blankLine.equals(stackTraceList.get(size - 1))) {
                                continue;
                            }
                            stackTraceList.add(blankLine);
                        }
                    } else {
                        stackTraceList.add(st);
                    }
                }
                stackTrace = StringUtils.join(stackTraceList.toArray(new String[0]), "\r\n");
            }
            log.error("处理后堆栈信息：\r\n{}", stackTrace);
        }
    }
}
