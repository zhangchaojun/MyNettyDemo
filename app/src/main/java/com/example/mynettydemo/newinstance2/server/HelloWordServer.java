package com.example.mynettydemo.newinstance2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloWordServer {
    private int port;

    public HelloWordServer(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap().group(bossGroup,workGroup)
                                    .channel(NioServerSocketChannel.class)
                                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                        @Override
                                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                                            ChannelPipeline pipeline = socketChannel.pipeline();

                                            ByteBuf delimiter = Unpooled.copiedBuffer("我".getBytes());
                                            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(2048,false,delimiter));
                                            // 字符串解码 和 编码
                                            pipeline.addLast("decoder", new StringDecoder());
                                            pipeline.addLast("encoder", new StringEncoder());

                                            // 自己的逻辑Handler
                                            pipeline.addLast("handler", new ServerHandler());
                                        }
                                    });

        try {
            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        HelloWordServer server = new HelloWordServer(7788);
        server.start();
    }
}
