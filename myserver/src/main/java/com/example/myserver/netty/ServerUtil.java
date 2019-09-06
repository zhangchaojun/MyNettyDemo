package com.example.myserver.netty;

import android.util.Log;

import com.example.myserver.handler.ServerInHandler1;
import com.example.myserver.handler.ServerInHandler2;
import com.example.myserver.handler.ServerInHandler3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class ServerUtil {

    private static final String TAG = "cj";


    public static void start() {

        Thread thread = new Thread(runnable, "serverThread");
        thread.start();

    }

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {

            NioEventLoopGroup parentGroup = new NioEventLoopGroup();
            NioEventLoopGroup childGroup = new NioEventLoopGroup();
            try {


                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(parentGroup, childGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();

                                pipeline.addLast("handler1", new ServerInHandler1());
                                pipeline.addLast("handler2", new ServerInHandler2());
                                pipeline.addLast("handler3", new ServerInHandler3());
                            }
                        });

                ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
                Log.e(TAG, "server started...");
                channelFuture.channel().closeFuture().sync();
                Log.e(TAG, "server ended...");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                parentGroup.shutdownGracefully();
                childGroup.shutdownGracefully();
                Log.e(TAG, "shutdownGracefully");
            }

        }
    };
}
