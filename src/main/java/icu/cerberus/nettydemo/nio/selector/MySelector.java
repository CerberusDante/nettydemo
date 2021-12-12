package icu.cerberus.nettydemo.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class MySelector {
    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false)
                    .register(selector, SelectionKey.OP_ACCEPT, null);
            serverSocketChannel.bind(new InetSocketAddress(8280));
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false).register(selector, SelectionKey.OP_READ, null);
                        System.out.println("registered...");
                    }
                    if (key.isReadable()) {
                        System.out.println("reading");
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                        int read = sc.read(byteBuffer);
                        if (read == -1) {
                            System.out.println("cancelling");
                            key.cancel();
                        }
                        byteBuffer.flip();
                        String s = Charset.defaultCharset().decode(byteBuffer).toString();
                        System.out.println(",,,,,,,,,,,," + s);
                    }
/*                    if (key.isWritable()) {
                        System.out.println("writing...");
                        SocketChannel sc = (SocketChannel) key.channel();
                        sc.write(StandardCharsets.UTF_8.encode("Hello World"));
                        key.cancel();
 */
                }
                // }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
