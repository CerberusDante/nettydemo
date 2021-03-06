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
                            ch.pipeline().addLast(new StringEncoder()); // ???????????????????????????ByteBuf
                        }
                    })
                    .connect(new InetSocketAddress("www.baidu.com", 80))
                    .sync() // ??????????????????????????????????????????????????????sync??????????????????????????????
                    .channel() // ????????????????????????????????????socket??????
                    .writeAndFlush("Hello World!"); // ???????????????????????????addLast???????????????????????????StringEncoder()?????????

        } catch (InterruptedException e) {
            log.error("????????????????????????");
            e.printStackTrace();
        }
    }
}
