package icu.cerberus.nettydemo.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class MyEventLoopClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup group1 = new NioEventLoopGroup();
        ChannelFuture localhost = new Bootstrap()
                .group(group1)
                // 调用工厂方法，指定Channel 或者 ChannelFactory类。
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringEncoder());
                    }
                })
                // 异步非阻塞调用connect，使用NioEventLoopGroup的线程连接。
                .connect(new InetSocketAddress("localhost", 19090));
        // connect异步非阻塞的处理方法1, 使用sync同步处理
        /*try {
            // 若不调用，主线程不阻塞，往下运行，此时还未建立channel。
            // 使用sync方法阻塞主线程，等待Nio的连接线程返回结果
            localhost.sync();
            Channel channel = localhost.channel();
            channel.writeAndFlush("Hello World");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // 使用回调函数 方法异步处理结果，执行回调函数的是NioEventLoop的线程
        localhost.addListener((ChannelFutureListener) channelFuture -> {
            Channel channel = channelFuture.channel();
            ChannelFuture helloWorld = channel.writeAndFlush("Hello World");
            log.debug("{}", channel);
            group.next().execute(() -> {
                Scanner scanner = new Scanner(System.in);
                for (; ; ) {
                    String s = scanner.nextLine();
                    channel.writeAndFlush(s);
                    if ("quit".equals(s) || "exit".equals(s)) {
                        channel.close();
                        break;
                    }
                    // channel.writeAndFlush(s);
                }
            });
            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.addListener((ChannelFutureListener) future -> {
                log.debug("关闭线程 {}", channel);
                group.shutdownGracefully();
                group1.shutdownGracefully();
            });
        });
        log.info("Async Programming");
    }
}
