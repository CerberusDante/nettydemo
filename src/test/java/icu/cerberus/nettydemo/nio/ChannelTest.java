package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
public class ChannelTest {

    @Test
    void testTransfer() {
        try {
            FileChannel in =
                    new FileInputStream(new File(Objects.requireNonNull(ChannelTest.class.getClassLoader().getResource("test.txt")).toURI())).getChannel();
            FileChannel out =
                    new FileOutputStream(new File(Objects.requireNonNull(ChannelTest.class.getClassLoader().getResource("to.txt")).toURI())).getChannel();
            long l = in.transferTo(0, in.size(), out);
            System.out.println(l);
        } catch (URISyntaxException | IOException e) {
            log.error("File Does Not Exist!");
            e.printStackTrace();
        }
    }

    @Test
    void testBlockedServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(65222));
        ByteBuffer buffer = ByteBuffer.allocate(128);
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();

        while (true) {
            SocketChannel sc = serverSocketChannel.accept(); // 阻塞方法，线程停止运行，等待连接，需每次等待新的连接进来，上一个连接的处理才会响应
            socketChannels.add(sc);
            System.out.println("########" + socketChannels.size());
            for (SocketChannel channel :
                    socketChannels) {
                channel.read(buffer); // 阻塞方法，线程停止运行，等待客户端读入数据，telnet不输入数据
                buffer.flip();
                BufferUtil.print(buffer);
                channel.write(StandardCharsets.UTF_8.encode("hello"));
                buffer.clear();
            }
        }
    }

    @Test
    void testNonBlockedServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(65222));
        serverSocketChannel.configureBlocking(false); // 设置为非阻塞，默认阻塞
        ByteBuffer buffer = ByteBuffer.allocate(128);
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();

        while (true) {
            // log.info("waiting...");
            SocketChannel sc = serverSocketChannel.accept(); // 非阻塞模式下，未收到连接返回null
            if (sc != null) {
                log.info("connected...{}", sc);
                socketChannels.add(sc);
                sc.configureBlocking(false); // socketChannel 设置为非阻塞，read等方法设置为非阻塞的。
            }
            if (socketChannels.size() > 0) {
                for (SocketChannel channel : socketChannels) {
                    if (channel != null) {
                        channel.read(buffer); // 非阻塞模式下，read返回0
                        buffer.flip();
                        BufferUtil.print(buffer);
                        log.info("message...{}", StandardCharsets.UTF_8.decode(buffer));
                        channel.write(StandardCharsets.UTF_8.encode("hello"));
                        buffer.clear();
                    }
                }
            }
        }
    }
}
