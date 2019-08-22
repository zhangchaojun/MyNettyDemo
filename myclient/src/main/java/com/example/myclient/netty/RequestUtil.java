package com.example.myclient.netty;

import android.util.Log;

import com.example.myclient.handler.ClientInHandler1;
import com.example.myclient.handler.ClientInHandler2;
import com.example.myclient.handler.ClientInHandler3;
import com.example.myclient.utils.ThreadPoolUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class RequestUtil {

    private static final String TAG = "cj";


    public static void post(final String ip, final int port) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                NioEventLoopGroup group = new NioEventLoopGroup();

                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast("clienthandler1", new ClientInHandler1());
                                    ch.pipeline().addLast("clienthandler2", new ClientInHandler2());
                                    ch.pipeline().addLast("clienthandler3", new ClientInHandler3());
                                }
                            });
                    ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
                    Log.e(TAG, "cilent 连接成功");
                    channelFuture.channel().closeFuture().sync();
                    Log.e(TAG, "cilent 关闭成功");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }

            }
        };

        ThreadPoolUtil.getFixedThreadPool().execute(runnable);

    }
}
