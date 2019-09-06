package com.example.mynettydemo.sequenceinstance.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 模拟测试channelHandler的执行顺序。
 *
 * @author zcj
 * @date 2019/8/22
 */
public class MyClient {


    public static void main(String[] args) {


        sendMessage();


    }

    private static void sendMessage() {

        NioEventLoopGroup group = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("clienthandler1", new MyClientHandler1());
                            ch.pipeline().addLast("clienthandler2", new MyClientHandler2());
                            ch.pipeline().addLast("clienthandler3", new MyClientHandler3());
                            ch.pipeline().addLast("clientouthandler1", new ClientOutHandler1());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8090).sync();
            System.out.println( "cilent 连接成功");
            channelFuture.channel().closeFuture().sync();
            System.out.println( "cilent 关闭成功");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("client  shutdownGracefully");
        }

    }
}
