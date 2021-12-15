package icu.cerberus.nettydemo.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopTest {
    @Test
    void testNioEventLoop() {
        EventLoopGroup group = new NioEventLoopGroup(2);
        group.next().submit(() -> {
            // try {
                // Thread.sleep(1000);
                log.debug("Ok");
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }
        });

        group.next().scheduleAtFixedRate(() -> {
            log.debug("Not Ok");
        }, 0, 1000, TimeUnit.MILLISECONDS);

        log.debug("main");
    }

    @Test
    void testEventLoopServer() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    String s = byteBuf.toString(Charset.defaultCharset());
                                    System.out.println(s);
                                }
                            });
                        }
                    }).bind(8090).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
    }
}
