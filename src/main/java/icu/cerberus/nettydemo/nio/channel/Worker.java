package icu.cerberus.nettydemo.nio.channel;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Slf4j
public class Worker implements Runnable {
    private final Selector bossSelector;
    private Thread thread;
    private String name;
    private Selector selector;
    private volatile boolean start = false;
    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public Worker(String s, Selector bossSelector) {
        this.name = s;
        this.bossSelector = bossSelector;
    }

    public void register(SocketChannel socketChannel) throws IOException {
        if (!start) {
            thread = new Thread(this, name);
            selector = Selector.open();
            start = true;
            thread.start();
        }
        queue.add(() -> {
            try {
                socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, null);
                log.debug("registered... {} ", socketChannel.getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            while (true) {
                selector.select();
                if (queue.size() != 0) {
                    queue.poll().run();
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                        channel.read(byteBuffer);
                        log.debug("reading...{}", channel);
                        BufferUtil.print(byteBuffer);
                        byteBuffer.flip();
                        String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                        System.out.println(s);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
