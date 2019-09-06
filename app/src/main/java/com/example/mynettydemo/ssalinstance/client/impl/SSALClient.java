package com.example.mynettydemo.ssalinstance.client.impl;

import com.example.mynettydemo.ssalinstance.client.CallbackListener;
import com.example.mynettydemo.ssalinstance.client.SSALHTTPClient;
import com.example.mynettydemo.ssalinstance.client.model.SSALServerProperties;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.encrypt.EncryptHandler;
import com.example.mynettydemo.ssalinstance.handler.DefaultErrorHandler;
import com.example.mynettydemo.ssalinstance.handler.HttpOverSSALClientHandler;
import com.example.mynettydemo.ssalinstance.handler.SSALContentBizAdapterHandler;
import com.example.mynettydemo.ssalinstance.handler.SSALErrorHandler;
import com.example.mynettydemo.ssalinstance.protocol.SSALDecoder;
import com.example.mynettydemo.ssalinstance.protocol.SSALEncoder;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class SSALClient implements SSALHTTPClient {

    /**
     * [客户端通过SSAL协议发起http请求的功能接口]
     *
     * @param ssalServerProperties SSAL Server信息, 不允许为空
     * @param request              完整的请求报文的对象封装, 不允许为空
     * @param listener             对http响应的处理回调, 允许为空，表示不做处理
     * @throws IllegalArgumentException 当参数ssalServerProperties为空是会抛出非法参数异常
     */
    @Override
    public void sendHttpRequest(SSALServerProperties ssalServerProperties, final FullHttpRequest request, final CallbackListener<FullHttpResponse> listener) throws Exception {
        if (ssalServerProperties == null)
            throw new IllegalArgumentException("[ssalServerProperties] cannot be null!");
        if (request == null) throw new IllegalArgumentException("[request] cannot be null!");
        // config Nio eventLoopGroup for client
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            ByteBuf delimiter = Unpooled.copiedBuffer(SSALConst.SSAL_FRAME_TAIL_BYTES);
                            int maxSSALFrameLength = SSALConfigInitProperties.maxFrameLength;
                            pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new DelimiterBasedFrameDecoder(maxSSALFrameLength * 2, delimiter));
                            pipeline.addLast("ssalEncoder", new SSALEncoder());
                            pipeline.addLast("ssalDecoder", new SSALDecoder(new EncryptHandler()));
                            pipeline.addLast("ssalErrorHandler", new SSALErrorHandler(new DefaultErrorHandler()));
                            pipeline.addLast("ssal-content-biz-adapter", new SSALContentBizAdapterHandler());
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            pipeline.addLast("http-decoder", new HttpResponseDecoder());
                            pipeline.addLast("http-aggregate", new HttpObjectAggregator(Integer.MAX_VALUE));
                            pipeline.addLast(new HttpOverSSALClientHandler(request, listener, new EncryptHandler()));
                        }
                    });

            // lunch connect action
            ChannelFuture channelFuture = b.connect(SSALConfigInitProperties.serverIp, SSALConfigInitProperties.serverPort).sync();
            // wait channel to close
            channelFuture.channel().closeFuture().sync();
        } finally {
            // gracefully shutdown
            group.shutdownGracefully();
        }
    }

}
