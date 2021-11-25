package com.wlx.demo.proxy;

import cn.hutool.core.thread.GlobalThreadPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@Slf4j
@Data
@AllArgsConstructor
public class SocketPipe {

    private Reactor reactor;

    private SocketChannel from;

    private SocketChannel to;

    @SneakyThrows
    public void pipe() {
        // 取消监听
        clearInterestOps();

        GlobalThreadPool.submit(() -> {
            try {
                log.info("数据传输开始。。。");
                int totalBytesRead = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                StringBuilder sb = new StringBuilder();

                while (valid(from) && valid(to)) {
                    byteBuffer.clear();
                    int bytesRead = from.read(byteBuffer);
                    totalBytesRead = totalBytesRead + bytesRead;
                    byteBuffer.flip();
                    to.write(byteBuffer);
                    sb.append(new String(byteBuffer.array(), "UTF-8"));
                    if (bytesRead < byteBuffer.capacity()) {
                        break;
                    }
                }
                log.info("数据传输结束。。。");
                log.info("本次传输数据：{}bytes", totalBytesRead);
                log.info("本次传输内容：\n{}", sb.toString());
                if (totalBytesRead < 0) {
                    reactor.closeChannel(from);
                    reactor.cancel(from);
                } else {
                    // 重置监听
                    resetInterestOps();
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
    }

    protected void clearInterestOps() {
        from.keyFor(reactor.getSelector()).interestOps(0);
        to.keyFor(reactor.getSelector()).interestOps(0);
    }

    protected void resetInterestOps() {
        from.keyFor(reactor.getSelector()).interestOps(SelectionKey.OP_READ);
        to.keyFor(reactor.getSelector()).interestOps(SelectionKey.OP_READ);
    }

    private boolean valid(SocketChannel channel) {
        return channel.isConnected() && channel.isRegistered() && channel.isOpen();
    }

    public Reactor getReactor() {
        return reactor;
    }

    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    public SocketChannel getFrom() {
        return from;
    }

    public void setFrom(SocketChannel from) {
        this.from = from;
    }

    public SocketChannel getTo() {
        return to;
    }

    public void setTo(SocketChannel to) {
        this.to = to;
    }
}
