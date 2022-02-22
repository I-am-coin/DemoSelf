package com.wlx.demo.test.reflect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectDemo {

    public <T extends Exception> void testException(Class<T> exceptionClass) throws Exception {
        if (exceptionClass == null) {
            throw new NullPointerException("exceptionClass is null!");
        }
        try {
            Exception exception = exceptionClass.newInstance();
            log.error("异常测试内部异常：", exception);
            throw exception;
        } catch (Exception e) {
            log.error("异常测试抛出异常：", e);
            throw new Exception(e);
        }
    }
}
