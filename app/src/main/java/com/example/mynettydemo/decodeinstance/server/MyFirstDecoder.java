package com.example.mynettydemo.decodeinstance.server;

import com.example.mynettydemo.utils.ByteTools;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author zcj
 * @date 2019/8/25
 */
public class MyFirstDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        byte b = in.readByte();
        byte[] bytes = ByteTools.hexStr2ByteArr("98");
        if (b == bytes[0]) {
            System.out.println("检查到帧头 == " + b);
            short length = in.readShort();
            short realLength = Short.reverseBytes(length);
            System.out.println("检查到长度域 == " + realLength);
            in.resetReaderIndex();
            if (in.readableBytes() >= realLength + 1) {
                ByteBuf byteBuf = in.readBytes(3 + realLength + 1);
                out.add(byteBuf);
            }
        }else{
            in.resetReaderIndex();
        }
    }


}
