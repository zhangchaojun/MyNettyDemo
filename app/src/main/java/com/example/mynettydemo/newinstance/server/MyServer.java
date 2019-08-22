package com.example.mynettydemo.newinstance.server;

import android.util.Log;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class MyServer {

    private static final String TAG = "MyServer";

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast("handler1", new MyServerHandler1());
                        ch.pipeline().addLast("handler2", new MyServerHandler2());
                        ch.pipeline().addLast("handler3", new MyServerHandler3());
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        System.out.println("server started...");
        channelFuture.channel().closeFuture().sync();
        System.out.println("server ended...");


    }
}
