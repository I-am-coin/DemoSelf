package com.wlx.demo.proxy.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class SslSocketChannel {

    /**
     * 握手加解密需要的四个存储
     */
    protected ByteBuffer myAppData; // 明文
    protected ByteBuffer myNetData; // 密文
    protected ByteBuffer peerAppData; // 明文
    protected ByteBuffer peerNetData; // 密文

    /**
     * 握手加解密过程中用到的异步执行器
     */
    protected ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 原NIO 的 CHANNEL
     */
    protected SocketChannel socketChannel;

    /**
     * SSL 引擎
     */
    protected SSLEngine engine;

    public SslSocketChannel(SSLContext context, SocketChannel socketChannel, boolean clientMode) throws Exception {
        // 原始的NIO SOCKET
        this.socketChannel = socketChannel;

        // 初始化BUFFER
        SSLSession dummySession = context.createSSLEngine().getSession();
        myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        dummySession.invalidate();

        engine = context.createSSLEngine();
        engine.setUseClientMode(clientMode);
        engine.beginHandshake();
    }

    /**
     * 参考 https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
     * 实现的 SSL 的握手协议
     */
    protected boolean doHandshake() throws IOException {
        SSLEngineResult result;
        SSLEngineResult.HandshakeStatus handshakeStatus = engine.getHandshakeStatus();

        while (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            switch (handshakeStatus) {
                case NEED_UNWRAP:
                    // 解密 出栈 net -> app
                    peerNetData.clear();
                    if (socketChannel.read(peerNetData) < 0) {
                        if (engine.isInboundDone() && engine.isOutboundDone()) {
                            return false;
                        }
                        try {
                            engine.closeInbound();
                        } catch (SSLException e) {
                            log.info("收到END OF STREAM，关闭连接.", e);
                        }
                        engine.closeOutbound();
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    peerNetData.flip();
                    try {
                        result = engine.unwrap(peerNetData, peerAppData);
                        log.info("NEED_UNWRAP content :{}", new String(peerAppData.array(), 0, peerAppData.position()));
                        peerNetData.compact();
                        handshakeStatus = result.getHandshakeStatus();
                    } catch (SSLException sslException) {
                        log.error("NEED_UNWRAP unwrap error : ", sslException);
                        engine.closeOutbound();
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    switch (result.getStatus()) {
                        case OK:
                            break;
                        case BUFFER_OVERFLOW:
                            // application buffer size > peerAppData size
                            peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            // packet buffer size > peerAppData size
                            peerNetData = handleBufferUnderflow(engine, peerNetData);
                            break;
                        case CLOSED:
                            if (engine.isOutboundDone()) {
                                return false;
                            } else {
                                engine.closeOutbound();
                                handshakeStatus = engine.getHandshakeStatus();
                                break;
                            }
                        default:
                            throw new IllegalStateException("无效的握手状态: " + result.getStatus());
                    }
                    break;
                case NEED_WRAP:
                    // 加密 入栈 app -> net
                    myNetData.clear();
                    try {
                        result = engine.wrap(myAppData, myNetData);
                        handshakeStatus = result.getHandshakeStatus();
                        log.info("NEED_WRAP content:{}", new String(myNetData.array(), 0, myNetData.position()));
                    } catch (SSLException sslException) {
                        log.error("NEED_WRAP wrap error : ", sslException);
                        engine.closeOutbound();
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    switch (result.getStatus()) {
                        case OK:
                            myNetData.flip();
                            while (myNetData.hasRemaining()) {
                                socketChannel.write(myNetData);
                            }
                            break;
                        case BUFFER_OVERFLOW:
                            // packet buffer size > myNetData size
                            myNetData = enlargePacketBuffer(engine, myNetData);
                            break;
                        case BUFFER_UNDERFLOW:
                            // application buffer size > myNetData size
                            throw new SSLException("加密后消息内容为空，报错");
                        case CLOSED:
                            try {
                                myNetData.flip();
                                while (myNetData.hasRemaining()) {
                                    socketChannel.write(myNetData);
                                }
                                peerNetData.clear();
                            } catch (Exception e) {
                                log.error("NEED_WRAP CLOSED error : ", e);
                                handshakeStatus = engine.getHandshakeStatus();
                            }
                            break;
                        default:
                            throw new IllegalStateException("无效的握手状态: " + result.getStatus());
                    }
                    break;
                case NEED_TASK:
                    Runnable task;
                    while ((task = engine.getDelegatedTask()) != null) {
                        executor.execute(task);
                    }
                    handshakeStatus = engine.getHandshakeStatus();
                    break;
                default:
                    throw new IllegalStateException("无效的握手状态: " + handshakeStatus);
            }
        }
        return true;
    }

    /**
     * 参考 https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
     * 实现的 SSL 的传输读取协议
     */
    public void read(Consumer<ByteBuffer> consumer) throws IOException {
        // BUFFER初始化
        peerNetData.clear();
        int bytesRead = socketChannel.read(peerNetData);
        if (bytesRead > 0) {
            peerNetData.flip();
            while (peerNetData.hasRemaining()) {
                peerAppData.clear();
                SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
                switch (result.getStatus()) {
                    case OK:
                        log.info("收到远程的返回结果消息为：" + new String(peerAppData.array(), 0, peerAppData.position()));
                        consumer.accept(peerAppData);
                        peerAppData.flip();
                        break;
                    case BUFFER_OVERFLOW:
                        peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                        break;
                    case BUFFER_UNDERFLOW:
                        peerNetData = handleBufferUnderflow(engine, peerNetData);
                        break;
                    case CLOSED:
                        log.info("收到远程连接关闭消息.");
                        closeConnection();
                        return;
                    default:
                        throw new IllegalStateException("无效的握手状态: " + result.getStatus());
                }
            }
        } else if (bytesRead < 0) {
            log.info("收到END OF STREAM，关闭连接.");
            handleEndOfStream();
        }
    }

    public void write(String message) throws IOException {
        write(ByteBuffer.wrap(message.getBytes()));
    }

    /**
     * 参考 https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
     * 实现的 SSL 的传输写入协议
     */
    public void write(ByteBuffer message) throws IOException {
        myAppData.clear();
        myAppData.put(message);
        myAppData.flip();
        while (myAppData.hasRemaining()) {
            myNetData.clear();
            SSLEngineResult result = engine.wrap(myAppData, myNetData);
            switch (result.getStatus()) {
                case OK:
                    myNetData.flip();
                    while (myNetData.hasRemaining()) {
                        socketChannel.write(myNetData);
                    }
                    log.info("写入远程的消息为: {}", message);
                    break;
                case BUFFER_OVERFLOW:
                    myNetData = enlargePacketBuffer(engine, myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("加密后消息内容为空.");
                case CLOSED:
                    closeConnection();
                    return;
                default:
                    throw new IllegalStateException("无效的握手状态: " + result.getStatus());
            }
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnection() throws IOException {
        engine.closeOutbound();
        doHandshake();
        socketChannel.close();
        executor.shutdown();
    }

    /**
     * END OF STREAM(-1)默认是关闭连接
     */
    protected void handleEndOfStream() throws IOException {
        try {
            engine.closeInbound();
        } catch (Exception e) {
            log.error("END OF STREAM 关闭失败.", e);
        }
        closeConnection();
    }

    /**
     * BUFFER_UNDERFLOW
     * 缩小 net buffer
     */
    private ByteBuffer handleBufferUnderflow(SSLEngine engine, ByteBuffer peerNetData) {
        int netSize = engine.getSession().getPacketBufferSize();
        ByteBuffer b = ByteBuffer.allocate(netSize);
        peerNetData.flip();
        b.put(peerNetData);
        peerNetData = b;
        return peerNetData;
    }

    /**
     * BUFFER_OVERFLOW
     * 扩大 app buffer
     */
    private ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer peerAppData) {
        int appSize = engine.getSession().getApplicationBufferSize();
        ByteBuffer b = ByteBuffer.allocate(appSize + peerAppData.position());
        peerAppData.flip();
        b.put(peerAppData);
        peerAppData = b;
        return peerAppData;
    }

    /**
     * BUFFER_OVERFLOW
     * 扩大 net buffer
     */
    private ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer peerNetData) {
        int netSize = engine.getSession().getPacketBufferSize();
        ByteBuffer b = ByteBuffer.allocate(netSize + peerNetData.position());
        peerNetData.flip();
        b.put(peerNetData);
        peerNetData = b;
        return peerNetData;
    }
}
