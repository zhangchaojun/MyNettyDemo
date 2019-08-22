package com.example.mynettydemo.newinstance.server;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class MyServerHandler2 extends ChannelInboundHandlerAdapter {


    public MyServerHandler2() {
        super();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelRegistered: ");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelUnregistered: ");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelActive: ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelInactive: ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("MyServerHandler2 channelRead: ");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelReadComplete: ");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("MyServerHandler2 userEventTriggered: ");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyServerHandler2 channelWritabilityChanged: ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("MyServerHandler2 exceptionCaught: ");
    }
}
