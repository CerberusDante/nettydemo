package icu.cerberus.nettydemo.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


public class EchoHandler extends ChannelHandlerAdapter {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        try {
//            ByteBuf in = (ByteBuf) msg;
//            System.out.println(in.toString(CharsetUtil.US_ASCII));
//            while (in.isReadable(12)) {
//                ctx.writeAndFlush(msg);
//            }
//            // 输出获得内容
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
}
