package icu.cerberus.nettydemo.discard;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiscardClientHandler extends SimpleChannelInboundHandler {
    private ChannelHandlerContext ctx;
    private String data;

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {

        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess()) {
                generateTraffic();
            } else {
                channelFuture.cause().printStackTrace();
                channelFuture.channel().close();
            }
        }
    };

    private void generateTraffic() throws Exception {
        ctx.writeAndFlush(data).addListener(trafficGenerator);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel registered...{}", ctx);
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active...{}", ctx);
        this.ctx = ctx;
        this.data = "hello world";
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

    }
}
