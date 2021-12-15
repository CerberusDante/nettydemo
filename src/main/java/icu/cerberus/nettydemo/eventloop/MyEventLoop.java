package icu.cerberus.nettydemo.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class MyEventLoop {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup();
        try {
            Channel channel = new ServerBootstrap()
                    // boss仅会占用一个线程
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("NioEventLoopGroup Handler1", new ChannelInboundHandlerAdapter() {
                                @Override
                                // channelRead 调用ctx.fireChannelRead
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    String s = byteBuf.toString(Charset.defaultCharset());
                                    log.debug("{}", s);
                                    // 这段代码将msg传递给下一个handler
                                    ctx.fireChannelRead(msg);
                                }
                            }).addLast(defaultEventLoopGroup, "DefaultELG Handler2", new ChannelInboundHandlerAdapter() {
                                // 使用另一个线程单独处理非Nio处理事件
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    String s = byteBuf.toString(Charset.defaultCharset());
                                    log.debug("{}", s);
                                }
                            });
                        };
                    })
                    .bind(19090)
                    .sync()
                    .channel();
            channel.writeAndFlush("String String");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
