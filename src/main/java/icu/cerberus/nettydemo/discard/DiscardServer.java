package icu.cerberus.nettydemo.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class DiscardServer {
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public DiscardServer() {
    }

    public void run() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder())
                                    .addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void start(String[] args) {
        // 1. 启动器，组装net天涯组件、启动服务器
        new ServerBootstrap()
                // 2. 加入事件轮组，处理事件
                .group(new NioEventLoopGroup()) //监听accept、read等事件
                // 3. 使用Nio的ServerSocketChannel实现,netty封装的SSC
                .channel(NioServerSocketChannel.class)
                // 4. child处理读写，childhandler执行的操作
                .childHandler(
                        // 初始化与客户端进行读写的通道
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            // accept事件处理后，连接建立调用initChannel方法
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 添加具体handler处理器， handler类似回调，发生事件后调用方法
                                nioSocketChannel.pipeline()
                                        .addLast(new StringDecoder()) // 将buffer转换成字符串
                                        .addLast(new ChannelInboundHandlerAdapter() {

                                        });
                            }
                        }).bind(65444);
        System.out.println("Server Begin");
    }

}
