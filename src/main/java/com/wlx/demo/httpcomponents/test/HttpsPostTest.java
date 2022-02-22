package com.wlx.demo.httpcomponents.test;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import static java.lang.System.out;


public class HttpsPostTest {
    public static void main(String[] args) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost("http://localhost:8080/post");

            StringEntity requestEntity = new StringEntity("request string info", ContentType.create("text/plain", Consts.UTF_8));
            httppost.setEntity(requestEntity);

            try (CloseableHttpResponse httpResponse = httpclient.execute(httppost)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                HttpEntity responseEntity = httpResponse.getEntity();

                out.println("响应状态：" + statusCode);
                out.println("响应内容：" + EntityUtils.toString(responseEntity));
            }
        }
    }
}
