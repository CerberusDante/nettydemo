package icu.cerberus.nettydemo.nio;

import icu.cerberus.nettydemo.nio.buffer.uitl.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
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
        // byte b = buffer.get();// get() 使 position + 1，读取一个byte,读取整条buffer需要多次调用get()
        // while (true) {
        //     byte a = buffer.get();
        //     System.out.println((char) a);
        //     if (a == 0) break;
        // }
        // byte[] b = new byte[16];
        byte b = buffer.get();
        log.debug("GET");
        // System.out.println("************" + Arrays.toString(b));
        // System.out.println("************" + new String(b, StandardCharsets.UTF_8));

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

    /**
     * 分散读测试
     * 其中某个字段定长，可作用于定长报文接收。
     */
    @Test
    void testScatterRead() {

        String[] strArr = new String[3];
        try {
            RandomAccessFile file = new RandomAccessFile(new File(Objects.requireNonNull(BufferTest.class.getClassLoader().getResource("test.txt")).toURI()), "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer bigBuffer = ByteBuffer.allocate(16); // 16位账号，不足右补空格
            ByteBuffer middleBuffer = ByteBuffer.allocate(15); // 15位姓名，不足右补空格
            ByteBuffer smallBuffer = ByteBuffer.allocate(12); // 12位日期，不足右补空格
            ByteBuffer[] bufferArr = {bigBuffer, middleBuffer, smallBuffer};
            // log.debug("*** INIT ***");
            // for (ByteBuffer byteBuffer : bufferArr) {
            //     BufferUtil.print(byteBuffer);
            // }
            channel.read(bufferArr);
            // log.debug("*** PUT ***");
            // for (ByteBuffer byteBuffer : bufferArr) {
            //     // byteBuffer.flip();
            //     BufferUtil.print(byteBuffer);
            // }
            log.debug("*** READ ***");
            for (int i = 0; i < bufferArr.length; i++) {
                // log.debug("*** FLIP ***");
                ByteBuffer byteBuffer = bufferArr[i];
                byteBuffer.flip();
                // BufferUtil.print(byteBuffer);
                // log.debug("*** GET ***");

                // BufferUtil.print(byteBuffer.get(bytes));
                // log.debug("{}", Arrays.toString(bytes));
                // log.debug("{}", new String(bytes, StandardCharsets.UTF_8));
                String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                log.debug("{}", s); // decode 默认调用get方法，读取bytebuffer
                strArr[i] = s;
                BufferUtil.print(byteBuffer);
            }

        } catch (URISyntaxException | IOException e) {
            if (e instanceof URISyntaxException) {
                log.error("test.txt is not Exist");
            }
            e.printStackTrace();
        }
        Arrays.stream(strArr).forEach(System.out::println);
    }

    /**
     * 测试集中写
     */
    @Test
    void testGatherWrite() {
        ByteBuffer[] bufArr = new ByteBuffer[3];
        String[] str = {"12345678        ", "黄子铭      ", "20201111    "};
        for (int i = 0; i < bufArr.length; i++) {
            bufArr[i] = StandardCharsets.UTF_8.encode(str[i]);
            System.out.println(str[i]);
            BufferUtil.print(bufArr[i]);
        }

        // ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("12345678        ");
        // ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("黄子铭      ");
        // ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("20201111    ");
        // ByteBuffer[] bufArr= {buffer1, buffer2, buffer3};

        try {
            File file = new File(Objects.requireNonNull(BufferTest.class.getClassLoader().getResource("test.txt")).toURI());
            System.out.println(file);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            channel.write(bufArr);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试粘包、半包处理方式
     */
    @Test
    void testSticky() {

        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Hello World\nI'm huangziming\nHo".getBytes());
        split(buffer, '\n');
        buffer.put("w are you?\n".getBytes());
    }

    private ByteBuffer split(ByteBuffer buffer, char mark) {
        buffer.flip();
        ByteBuffer target = null;
        int count = 0;
        int count2 = 0;
        for (int i = 0; i < buffer.limit(); i++) {
            count++;
            BufferUtil.print(buffer);
            // buffer.get(int index) 不自增position
            if (buffer.get(i) == mark) {
                System.out.println(buffer.position());
                int length = i - buffer.position() + 1;
                target = ByteBuffer.allocate(length);
                System.out.println(buffer.position());
                for (int j = 0; j < length; j++) {
                    target.put(buffer.get());
                    count2++;
                }
                BufferUtil.print(target);
            }
        }
        buffer.compact();
        System.out.println(count + count2);
        return target;
    }
}
