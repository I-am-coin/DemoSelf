package com.wlx.demo.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HeaderElement;

public class ElementUtils {

    public static String toString(HeaderElement[] elements) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (ArrayUtils.isNotEmpty(elements)) {
            for (HeaderElement element : elements) {
                sb.append("[NAME:").append(element.getName()).append(", VALUE:").append(element.getValue()).append("]\n");
            }
        }
        return sb.toString();
    }

    public static void printHeaderElements(HeaderElement[] elements) throws Exception {
        System.out.println(ElementUtils.toString(elements));
    }
}
