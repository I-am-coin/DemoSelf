package com.wlx.demo.proxy.ssl;


import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

@Slf4j
public class NioSslServer {

    public static void main(String[] args) throws Exception {
        NioSslServer sslServer = new NioSslServer("127.0.0.1", 8006);
        sslServer.start();
        // 使用 curl -vv -k 'https://localhost:8006' 连接
    }

    private SSLContext context;

    private Selector selector;

    public NioSslServer(String hostAddress, int port) throws Exception {
        // 初始化SSL Context
        context = serverSSLContext();

        // 注册监听器
        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private SSLContext serverSSLContext() throws Exception {
        return SSLContext.getDefault();
    }

    public void start() throws Exception {
        log.info("等待连接中.");
        
        while (true) {
            selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    ((SslSocketChannel)key.attachment()).read(buf->{
                        log.info("服务端收到内容为：{}", new String(buf.array(), 0, buf.position()));});
                    // 直接回应一个OK
                    ((SslSocketChannel)key.attachment()).write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nOK\r\n\r\n");
                    ((SslSocketChannel)key.attachment()).closeConnection();
                }
            }
        }
    }

    private void accept(SelectionKey key) throws Exception {
        log.info("接收新的请求.");

        SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
        socketChannel.configureBlocking(false);

        SslSocketChannel sslSocketChannel = new SslSocketChannel(context, socketChannel, false);
        if (sslSocketChannel.doHandshake()) {
            // 将选择器READ操作注册到sslSocketChannel
            socketChannel.register(selector, SelectionKey.OP_READ, sslSocketChannel);
        } else {
            socketChannel.close();
            log.info("握手失败，关闭连接.");
        }
    }
}
