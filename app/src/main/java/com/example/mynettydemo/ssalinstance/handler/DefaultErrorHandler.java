package com.example.mynettydemo.ssalinstance.handler;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.client.SSALErrorListener;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.SecureAccessAreaDeviceKeyNegotiationTrigger;
import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.ProtocolEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zcj
 * @date 2019/8/3
 */
public class DefaultErrorHandler implements SSALErrorListener {
    private static final String TAG = "cj";
    @Override
    public void onPspDecryptionError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onPspCheckSignFailed(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onPspCheckMacFailed(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onSessionCounterError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onGateDecryptionError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onGateCheckSignFailed(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onGateCheckMacFailed(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onGateKeyUnitIssue(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onLinkEquipmentKeyUnitError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onProtocolVersionError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onEncryptionAlgorithmFlagMismatch(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onDeviceTypeNotFound(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onControlCodeNotFound(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onStartupDirectionError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onLudLengthAbnormalShorterThan4Bytes(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onLudLengthMismatch(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

        //2007数据域长度不匹配
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer("数据域长度不匹配!".getBytes()));
        ctx.fireChannelRead(response);


    }

    @Override
    public void onTargetAddressNotExist(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer("target server ip not exist!".getBytes()));
        ctx.fireChannelRead(response);
    }

    //3002
    @Override
    public void onCommunicateChannelNotCreated(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {
        SSALMessage message = SSALMessage.giveMeOne(SSALConfigInitProperties.targetIp, SSALConfigInitProperties.targetPort, UserContentTypeOctEnum.SECURE_ACCESS_AREA_DEVICE_KEY_NEGOTIATION_TRIGGER);
        message.loadLudHex(SecureAccessAreaDeviceKeyNegotiationTrigger.NEGOTIATION_TRIGGER_LUD_HEX, FCCodeBinEnum.ONE);
        message.initCRC();
        Log.e(TAG, "onCommunicateChannelNotCreated: 触发协商的报文: " + message);
//        ByteBuf ssal = DefaultSSALParser.instance().loadSSAL(message);
        ReferenceCountUtil.retain(message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void onMsgSendError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onChannelError(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }

    @Override
    public void onChannelKeyNegotiationFailed(SSALMessage ssalMessage, ChannelHandlerContext ctx, ProtocolEnum bizProtocol) {

    }
}
