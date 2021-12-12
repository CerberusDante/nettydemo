package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.channel.Worker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class TestMutiThreadSelector {

    @Test
    void testBossThread() throws IOException {
        Thread.currentThread().setName("Boss");
        Selector bossSelector = Selector.open();
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) ServerSocketChannel.open()
                .bind(new InetSocketAddress(65444))
                .configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(bossSelector, SelectionKey.OP_ACCEPT, null);
        Worker worker = new Worker("worker-0", bossSelector);
        while (true) {
            bossSelector.select();
            Iterator<SelectionKey> bossIter = bossSelector.selectedKeys().iterator();
            while (bossIter.hasNext()){
                SelectionKey key = bossIter.next();
                bossIter.remove();
                log.debug("connecting...{}", key);
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    log.debug("connected...{}", socketChannel);
                    socketChannel.configureBlocking(false);
                    log.debug("registering... {} ", socketChannel.getRemoteAddress());
                    worker.register(socketChannel);
                }
            }
        }
    }
}
