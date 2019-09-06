package com.example.mynettydemo.ssalinstance.protocol;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;

public class SSALEncoder extends MessageToMessageEncoder<SSALMessage> {
    private static final String TAG = "cj";
    private static final Logger logger = LoggerFactory.getLogger(SSALEncoder.class);

    protected void encode(ChannelHandlerContext ctx, SSALMessage msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = DefaultSSALParser.instance().loadSSAL(msg);

        ReferenceCountUtil.retain(byteBuf);
        out.add(byteBuf);
        ctx.flush();
    }
}
