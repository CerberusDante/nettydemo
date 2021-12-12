package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
public class testSelector {

    @Test
    void testWrite() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SelectionKey selectionKey = serverSocketChannel.bind(new InetSocketAddress(65333))
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT, null);
        log.debug("ssc...{}", serverSocketChannel);
        log.debug("Selection Key... {}", selectionKey);
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        ArrayList<SocketChannel> socketChannels = new ArrayList<>();
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false)
                            .register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, byteBuffer);
                }
                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // if (key.isReadable()) {
                    //     int read = socketChannel.read((ByteBuffer) key.attachment());
                    //     if (read == -1)
                    //         key.cancel();
                    //     byteBuffer.flip();
                    //     String str = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                    //     System.out.println(str);
                    // }
                    String response = "HTTP/1.1 404 Not Found" + "\n" + "Connection: Keep-Alive" + "\n" + "Keep-Alive: timeout=5, max=999";
                    System.out.println(response);
                    int write = socketChannel.write(StandardCharsets.UTF_8.encode(response));
                    if (write == response.length()) {
                        key.cancel();
                    }
                    socketChannel.finishConnect();
                }
            }
        }

    }

    @Test
    void testMySelector() throws IOException {
        Selector selector = Selector.open();
        ByteBuffer buffer = ByteBuffer.allocate(128);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SelectionKey selectionKey = serverSocketChannel.bind(new InetSocketAddress(65223))
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT, null);
        log.debug("ssc1...{}", serverSocketChannel);

        // 关注ACCEPT事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key...{}", selectionKey);
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            selector.select(); // 开始监听事件，有事件以后才会线程开始运行，没有事件时线程暂停
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            System.out.println("Size: " + selector.selectedKeys().size());
            while (iterator.hasNext()) {
                // 建立连接
                SelectionKey key = iterator.next();
                log.debug("key...  {}", key);
                // 处理完一个key（事件后) 需要自己删除这个key
                iterator.remove();
                if (key.isAcceptable()) {
                    log.debug("key.isAcceptable()...{}", key);
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    log.debug("ssc2 {}", channel);
                    SocketChannel socketChannel = channel.accept();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = socketChannel.configureBlocking(false)
                            .register(selector, SelectionKey.OP_READ, byteBuffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("sc...{}", socketChannel);
                    log.debug("scKey...{}", scKey);
                }
                if (key.isReadable()) {
                    log.debug("key.isReadable...{}", key);
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    log.debug("sc... {}", socketChannel);
                    // 生成channel，开始读写
                    // 一个channel 使用一个buffer，不然导致多个连接使用同一块内存，导致内容紊乱
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    int read = socketChannel.read(byteBuffer);
                    // 关闭客户端添加读事件，不判断会导致有读事件未处理完毕
                    if (read == -1) {
                        key.cancel();
                    }
                    BufferUtil.print(byteBuffer);
                    ArrayList<ByteBuffer> byteBuffers = split(byteBuffer, '\n');
                    if (byteBuffers.size() == 0) {
                        ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                        byteBuffer.flip();
                        newBuffer.put(byteBuffer);
                        key.attach(newBuffer);
                    } else {
                        System.out.println("!!!");
                        for (ByteBuffer byteBuf : byteBuffers) {
                            BufferUtil.print(byteBuf);
                            byteBuf.flip();
                            String str = StandardCharsets.UTF_8.decode(byteBuf).toString();
                            stringBuilder.append(str);
                        }
                    }
                }
                System.out.println(stringBuilder);
            }
        }
    }

    @Test
    void name() {
        byte b = 0x0a;
        System.out.println(b);
        char a = (char) b;
        char c = '\n';
        byte d = (byte) c;
        System.out.println(d);
        System.out.println(a);
    }

    private ArrayList<ByteBuffer> split(ByteBuffer buffer, char mark) {
        System.out.println("##########");
        ArrayList<ByteBuffer> byteBuffers = new ArrayList<>();
        buffer.flip();
        ByteBuffer target = null;
        for (int i = 0; i < buffer.limit(); i++) {
            // buffer.get(int index) 不自增position
            if (buffer.get(i) == (byte) mark) {
                int length = i - buffer.position() + 1;
                target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(buffer.get());
                }
                BufferUtil.print(target);
                byteBuffers.add(target);
            }
        }
        buffer.compact();
        System.out.println(byteBuffers.size());
        return byteBuffers;
    }

}

