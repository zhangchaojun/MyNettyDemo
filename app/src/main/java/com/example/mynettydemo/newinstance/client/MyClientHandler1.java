package com.example.mynettydemo.newinstance.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class MyClientHandler1 extends ChannelInboundHandlerAdapter {


    public MyClientHandler1() {
        super();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelRegistered: ");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelUnregistered: ");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelActive: ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelInactive: ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("MyClientHandler1 channelRead: ");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelReadComplete: ");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("MyClientHandler1 userEventTriggered: ");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler1 channelWritabilityChanged: ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("MyClientHandler1 exceptionCaught: ");
    }
}
