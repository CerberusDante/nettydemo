package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class BufferTest {
    /**
     * 测试buffer一次读写中 buffer数据存储的过程
     */
    @Test
    void ByteBufferStructure() {
        String s = "Hello World";
        ByteBuffer buffer = ByteBuffer.allocate(16);
        log.debug("INIT");
        BufferUtil.print(buffer);
        // buffer.flip();
        buffer.put(s.getBytes()); // 写模式，将byte写如Buffer中
        log.debug("PUT");
        BufferUtil.print(buffer);
        buffer.put(" hzm".getBytes()); // 写模式，将byte写如Buffer中
        log.debug("PUT");
        BufferUtil.print(buffer);
        buffer.flip();  // flip() 将写模式切换为读模式，将position 置为 1
        log.debug("FLIP");
        BufferUtil.print(buffer);
        byte b = buffer.get();// get() 使 position + 1，读取一个byte,读取整条buffer需要多次调用get()
        while (true) {
            byte a = buffer.get();
            System.out.println((char) a);
            if (a == 0) break;
        }
        log.debug("GET");
        System.out.println("************" + (char) b);
        BufferUtil.print(buffer);
        // buffer.clear();
        // log.debug("CLEAR");
        // BufferUtil.print(buffer);
        buffer.compact();
        log.debug("COMPACT");
        BufferUtil.print(buffer);
    }

    @Test
    void testStringToBuffer() {
        String s = "Hello World";
        ByteBuffer b1 = StandardCharsets.UTF_8.encode(s);
        BufferUtil.print(b1);
    }

    /**
     * 基本使用
     */
    @Test
    void ByteBufferTest() {
        URL resource = BufferTest.class.getClassLoader().getResource("logback.xml");
        log.debug("{}", resource);
        FileChannel channel = null;
        StringBuilder s = new StringBuilder();
        try {
            if (!Optional.ofNullable(resource).isPresent()) {
                System.out.println("文件不存在");
                return;
            }
            System.out.println("文件路径为" + resource.toURI());
            FileInputStream input = new FileInputStream(new File(resource.toURI()));
            channel = input.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(32);
        System.out.println(System.currentTimeMillis());
        log.debug("{}", LocalDateTime.now().toString());
        try {
            while (true) {
                assert channel != null;
                // 从channel读，写入buffer
                int len = channel.read(buffer);
                if (len == -1) break;
                // buffer读模式 （从buffer中获取数据）
                buffer.flip();
                // BufferUtil.print(buffer);
                while (buffer.hasRemaining()) {
                    s.append((char) buffer.get());
                }
                // buffer 写模式
                buffer.clear();
            }

            log.debug("{}", s);
            log.debug("{}", LocalDateTime.now().toString());
           // log.debug("{}", s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试ByteBuffer编解码字符集
     */
    @Test
    void testEncode() {
        URL resource = BufferTest.class.getClassLoader().getResource("logback.xml");
        log.debug("{}", resource);
        FileChannel channel = null;
        StringBuilder s = new StringBuilder();
        try {
            if (!Optional.ofNullable(resource).isPresent()) {
                System.out.println("文件不存在");
                return;
            }
            System.out.println("文件路径为" + resource.toURI());
            FileInputStream input = new FileInputStream(new File(resource.toURI()));
            channel = input.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(128);
        System.out.println(System.currentTimeMillis());
        log.debug("{}", LocalDateTime.now().toString());
        try {
            while (true) {
                assert channel != null;
                // 从channel读，写入buffer
                int len = channel.read(buffer);
                if (len == -1) break;
                // buffer读模式 （从buffer中获取数据）
                buffer.flip();
                // BufferUtil.print(buffer);
                while (buffer.hasRemaining()) {
                    BufferUtil.print(buffer);
                    // UTF_8解码buffer
                    CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);
                    // 调用decode 自动调用buffer.get();
                    BufferUtil.print(buffer);
                    System.out.println(decode.toString());
                    s.append(decode.toString());
                    // s.append((char) buffer.get());
                }
                // buffer 写模式
                buffer.clear();
            }

            log.debug("{}", s);
            log.debug("{}", LocalDateTime.now().toString());
            // log.debug("{}", s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
