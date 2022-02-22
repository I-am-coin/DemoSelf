package com.wlx.demo.httpcomponents.test;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.*;

import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.out;

/**
 * 只能测试http请求，不支持https
 */
public class HttpResponseTest {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);

        UriHttpRequestHandlerMapper mapper = new UriHttpRequestHandlerMapper();
        mapper.register("*", (request, response, context) -> {
            out.println("request uri : " + request.getRequestLine().getUri());

//            response.setEntity(new StringEntity("response string info", ContentType.create("text/plain", Consts.UTF_8)));
            response.setEntity(
                    MultipartEntityBuilder.create()
                            .addPart("STRING1", new StringBody("{\"RESPONSE_STR\":\"this is response string!\"}", ContentType.create("text/plain", Consts.UTF_8)))
                            .addPart("STRING2", new StringBody("{\"NAME\":\"WEILX\"}", ContentType.create("text/plain", Consts.UTF_8)))
                            .build());
            response.setStatusCode(200);
        });

        final HttpProcessor inhttpproc = new ImmutableHttpProcessor(
                new ResponseDate(),
                new ResponseServer("Http/1.1"),
                new ResponseContent(),
                new ResponseConnControl());
        final int bufsize = 8 * 1024;

        while (true) {
            final Socket insocket = serverSocket.accept();
            final DefaultBHttpServerConnection inconn = new DefaultBHttpServerConnection(bufsize);
            inconn.bind(insocket);
            new HttpService(inhttpproc, mapper).handleRequest(inconn, new BasicHttpContext(null));
        }
    }
}
