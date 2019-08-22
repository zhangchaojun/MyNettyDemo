package com.example.myclient.handler;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class ClientInHandler1 extends ChannelInboundHandlerAdapter {

    private static final String TAG = "cj";

    public ClientInHandler1() {
        super();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelRegistered: ");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelUnregistered: ");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelActive: ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelInactive: ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelRead: ");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelReadComplete: ");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Log.e(TAG, "ClientInHandler1 userEventTriggered: ");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "ClientInHandler1 channelWritabilityChanged: ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e(TAG, "ClientInHandler1 exceptionCaught: ");
    }
}
