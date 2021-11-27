package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class BufferTest {

    @Test
    void ByteBufferStructure() {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        BufferUtil.print(buffer);
    }

    @Test
    void ByteBufferTest() throws URISyntaxException {
        URL resource = BufferTest.class.getClassLoader().getResource("logback.xml");
        log.debug("{}", resource);
        FileChannel channel = null;
        StringBuilder s = new StringBuilder();
        try {
//            assert resource != null;
//            RandomAccessFile rw = new RandomAccessFile(new File(resource.toURI()), "rw");
//            FileChannel channel1 = rw.getChannel();

            FileInputStream input = new FileInputStream(new File(resource.toURI()));
            channel = input.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(32);
        try {
            while (true) {
                assert channel != null;
                // 从channel读，写入buffer
                int len = channel.read(buffer);
                if (len == -1) break;
                // buffer读模式 （从buffer中获取数据）
                buffer.flip();

                while (buffer.hasRemaining()) {
                    s.append((char) buffer.get());
                }
                // buffer 写模式
                buffer.clear();
            }
            log.info("{}", s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
