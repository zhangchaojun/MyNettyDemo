package com.example.mynettydemo.newinstance2.server;

import java.util.Arrays;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("有一个客户端连接上了，他的地址是 ip=="+ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端断开连接了，他的地址是 ip=="+ctx.channel().remoteAddress());

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("server receive order : " + body + ";the counter is: " + ++counter);

//        byte[] bytes = "我爸Gnst8".getBytes();
//
//        ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("server exception Caught: "+cause.getMessage());
        ctx.close();
    }
}
