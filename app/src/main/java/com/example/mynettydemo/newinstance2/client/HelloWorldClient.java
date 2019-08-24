package com.example.mynettydemo.newinstance2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloWorldClient {
    private int port;
    private String address;

    public HelloWorldClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /*
                             * 这个地方的 必须和服务端对应上。否则无法正常解码和编码
                             */
                            ByteBuf delimiter = Unpooled.copiedBuffer("8".getBytes());
                            ch.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(2048,false, delimiter));
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());

                            // 客户端的逻辑
                            ch.pipeline().addLast("handler", new ClientHandler());
                        }
                    });


            ChannelFuture future = bootstrap.connect(address, port).sync();
            System.out.println("client connected success!! ip=="+address+" port=="+port);
            future.channel().closeFuture().sync();
            System.out.println("client closed success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("client shutdown gracefully!!");
        }

    }

    public static void main(String[] args) {
        HelloWorldClient client = new HelloWorldClient(7788, "127.0.0.1");
        client.start();
    }
}
