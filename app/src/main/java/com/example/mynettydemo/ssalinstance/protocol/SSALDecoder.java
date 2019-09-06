package com.example.mynettydemo.ssalinstance.protocol;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.SSALParser;
import com.example.mynettydemo.ssalinstance.entity.dto.TmpMessageStore;
import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.util.ByteTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class SSALDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final String TAG = "cj4";
    private static final Logger logger = LoggerFactory.getLogger(SSALDecoder.class);
    private EncryptListener encryptListener;

    public SSALDecoder(EncryptListener encryptListener) {
        this.encryptListener = encryptListener;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//        logger.error("received msg: {}", msg.toString(Charset.defaultCharset()));
//        LogUtil.e("decode==" + msg.toString(Charset.defaultCharset()));
        byte[] bytesArr2Analyze = TmpMessageStore.getBytesArr2Analyze(ctx, msg);
        SSALParser ssalParser = DefaultSSALParser.instance();
        SSALMessage ssalMessage = null;
        try {
            ssalMessage = ssalParser.parse(ByteTools.byteArr2HexStr(bytesArr2Analyze), encryptListener);
        } catch (StringIndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            // ignore
        }
        if (ssalMessage != null && ssalMessage.decodeResult()) {

            Log.e(TAG, "[DECODE SUCCESS]业务码== "+ssalMessage.getFrameSequenceHex());
            TmpMessageStore.rmByteMessage(ctx.channel().id());
            out.add(ssalMessage);
        } else {
//            logger.error("[DECODE FAILED]: ssal frame not complete yet!");
//            Log.e(TAG, "decode:[DECODE FAILED]: ssal frame not complete yet! " );
            TmpMessageStore.putByteMessage(ctx.channel().id(), bytesArr2Analyze);
        }
    }
}
