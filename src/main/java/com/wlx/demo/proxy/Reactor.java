package com.wlx.demo.proxy;


import lombok.Data;
import lombok.SneakyThrows;

import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;

@Data
public class Reactor {

    private Selector selector;

    private volatile boolean finish = false;

    @SneakyThrows
    public Reactor() {
        selector = Selector.open();
    }

    @SneakyThrows
    public Reactor pipe(SocketChannel from, SocketChannel to) {
        from.configureBlocking(false);
        from.register(selector, SelectionKey.OP_READ, new SocketPipe(this, from, to));
        return this;
    }

    @SneakyThrows
    public void run() {
        try {
            while (!finish) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = it.next();
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
                            ((SocketPipe) selectionKey.attachment()).pipe();
                        }
                        it.remove();
                    }
                }
            }
        } finally {
            close();
        }
    }

    @SneakyThrows
    public synchronized void close() {
        if (finish) {
            return;
        }
        finish = true;
        if (!selector.isOpen()) {
            return;
        }
        for (SelectionKey key : selector.keys()) {
            closeChannel(key.channel());
            key.cancel();
        }
        if (selector != null) {
            selector.close();
        }
    }

    public void cancel(SelectableChannel channel) {
        SelectionKey key = channel.keyFor(selector);
        if (Objects.isNull(key)) {
            return;
        }
        key.cancel();
    }

    @SneakyThrows()
    public void closeChannel(Channel channel) {
        SocketChannel socketChannel = (SocketChannel) channel;
        if (socketChannel.isConnected() && socketChannel.isOpen()) {
            socketChannel.shutdownOutput();
            socketChannel.shutdownInput();
        }
        socketChannel.close();
    }

    public Reactor(Selector selector) {
        this.selector = selector;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }
}

