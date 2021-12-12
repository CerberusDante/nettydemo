package icu.cerberus.nettydemo.discard;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class DiscardClient {
    private int port;

    public DiscardClient() {
    }

    public DiscardClient(int port) {
        this.port = port;
    }

    public void run() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            log.debug("connecting...");
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                                 @Override
                                 protected void initChannel(NioSocketChannel ch) {
                                     ch.pipeline()
                                             .addLast(new StringEncoder())
                                             .addLast(new DiscardClientHandler());
                                 }
                             }
                    );
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(port)).sync();
            future.channel().writeAndFlush("Hello World!");
            System.out.println(future.channel().remoteAddress().toString());
            log.debug("connected...{}", future);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new StringEncoder()); // 将发送的字符串转为ByteBuf
                        }
                    })
                    .connect(new InetSocketAddress("www.baidu.com", 80))
                    .sync() // 阻塞方法，连接建立后才往下处理，调用sync方法后，等待连接建立
                    .channel() // 代表客户端与服务端连接的socket对象
                    .writeAndFlush("Hello World!"); // 方法数据后掉哦那个addLast里的处理方法，调用StringEncoder()方法。

        } catch (InterruptedException e) {
            log.error("与服务器连接终端");
            e.printStackTrace();
        }
    }
}
