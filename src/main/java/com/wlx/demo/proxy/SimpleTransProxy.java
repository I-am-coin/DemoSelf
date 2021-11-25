package com.wlx.demo.proxy;


import cn.hutool.core.thread.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 透明传输代理
 *
 * @author https://mp.weixin.qq.com/s/2RT7wbm1iyud21zZ5d0jSA
 */
@Slf4j
public class SimpleTransProxy {

    public static void main(String[] args) throws IOException {
        int port = 8006;
        ServerSocketChannel localServer = ServerSocketChannel.open();
        localServer.bind(new InetSocketAddress(port));
        Reactor reactor = new Reactor();
        // REACTOR线程
        GlobalThreadPool.submit(reactor::run);
        log.info("REACTOR线程装载完成！");

        // WORKER单线程调试
        while (localServer.isOpen()) {
            // 此处阻塞等待连接
            SocketChannel remoteClient = localServer.accept();
            log.info("接收到连接请求。。。");

            // 工作线程
            GlobalThreadPool.submit(() -> {
                // 代理到远程
                ProxyHandler proxyHandler = new ProxyHandler();
                SocketChannel remoteServer = proxyHandler.proxy(remoteClient);

                try {
                    log.info("请求客户端RemoteAddress：{}", remoteClient.getRemoteAddress().toString());
                } catch (Exception e) {
                    log.error("获取请求客户端信息异常：", e);
                }
                log.info("连接完成，获取到的远程连接地址为 {}:{}", proxyHandler.getHost(), proxyHandler.getPort());
                // 透明传输
                reactor.pipe(remoteClient, remoteServer)
                        .pipe(remoteServer, remoteClient);

            });
        }
    }
}

