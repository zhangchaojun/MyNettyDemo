package com.example.mynettydemo.newinstance.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 模拟并发。以及线程池释放，当客户端shutdownGracefully执行的时候，线程池就会释放。
 *
 * @author zcj
 * @date 2019/8/22
 */
public class MyClient {

    static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            executorService.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            sendMessage();
                        }
                    });
        }

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
//                        ch.pipeline().addLast(new ReadTimeoutHandler(5));
                            System.out.println("channel线程==" + Thread.currentThread().getName());
                            if (!Thread.currentThread().getName().equals("nioEventLoopGroup-2-1")) {
                                ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            }
                            ch.pipeline().addLast("clienthandler1", new MyClientHandler1());
                            ch.pipeline().addLast("clienthandler2", new MyClientHandler2());
                            ch.pipeline().addLast("clienthandler3", new MyClientHandler3());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8090).sync();
            System.out.println(Thread.currentThread().getName() + "cilent 连接成功"+System.currentTimeMillis());
            channelFuture.channel().closeFuture().sync();
            System.out.println(Thread.currentThread().getName() + "cilent 关闭成功");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println(Thread.currentThread().getName() + "shutdownGracefully");
        }

    }
}
