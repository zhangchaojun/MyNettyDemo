package com.example.mynettydemo.ssalinstance.handler;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.client.SSALErrorListener;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.ObtainPspBasicInfo;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.PspSessionNegotiation;
import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.ProtocolEnum;
import com.example.mynettydemo.ssalinstance.enums.SSALErrorHexEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.Attribute;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class SSALErrorHandler extends SimpleChannelInboundHandler<SSALMessage> {
    private static final String TAG = "cj";

	private static final Logger logger = LoggerFactory.getLogger(SSALErrorHandler.class);
	private SSALErrorListener ssalErrorListener;

    public SSALErrorHandler(SSALErrorListener ssalErrorListener) {
        this.ssalErrorListener = ssalErrorListener;
    }

    @Override
	protected void channelRead0(ChannelHandlerContext ctx, SSALMessage ssalMessage) throws Exception {
        logger.error("received ssal msg: {}", ssalMessage);
        SSALErrorHexEnum backCode = ssalMessage.getLinkUserData().getBackCode();
        UserContentTypeOctEnum userContentType = ssalMessage.getControlData().getFunctionCode().getUserContentTypeOct();
        if(UserContentTypeOctEnum.APPLICATION_DATA.equals(ssalMessage.getControlData().getFunctionCode().getUserContentTypeOct())
				&& (backCode == null || SSALErrorHexEnum.SUCCESS.equals(backCode))) {
            logger.trace("received {} msg.", backCode != null ?  "success downStream" : "upStream");
            ReferenceCountUtil.retain(ssalMessage);
            ctx.fireChannelRead(ssalMessage);
        }else {
            if (UserContentTypeOctEnum.OBTAIN_PSP_BASIC_INFO.equals(userContentType)) {
                SSALMessage message = SSALMessage.giveMeOne(SSALConfigInitProperties.targetIp, SSALConfigInitProperties.targetPort, UserContentTypeOctEnum.OBTAIN_PSP_BASIC_INFO);
//                ObtainPspBasicInfo obtainPspBasicInfo = KeyNegotiation.getKeyNegotiation().getSafeUnitMessage3();
//                message.loadLudHex(obtainPspBasicInfo.getLudHex(), FCCodeBinEnum.ONE);
//                message.initCRC();
                ReferenceCountUtil.retain(message);
                ctx.writeAndFlush(message);
                return;
            }else if (UserContentTypeOctEnum.PSP_SESSION_NEGOTIATION.equals(userContentType)){
//                PspSessionNegotiation pspSessionNegotiation = (PspSessionNegotiation) ssalMessage.getLinkUserData();
//                String m1Hex = pspSessionNegotiation.getM1Hex();
//                String s1Hex = pspSessionNegotiation.getS1Hex();
//                String pspSessionNegotiationLud = KeyNegotiation.getKeyNegotiation().getPspSessionNegotiationLud(m1Hex, s1Hex);
//                if (pspSessionNegotiationLud.length() > 4) {
//                    SSALMessage pspSessionNegotiationMessage = SSALMessage.giveMeOne(SSALConfigInitProperties.targetIp, SSALConfigInitProperties.targetPort, UserContentTypeOctEnum.PSP_SESSION_NEGOTIATION);
//                    pspSessionNegotiationMessage.loadLudHex(pspSessionNegotiationLud, FCCodeBinEnum.ONE);
//                    pspSessionNegotiationMessage.initCRC();
////                    ByteBuf ssal = DefaultSSALParser.instance().loadSSAL(pspSessionNegotiationMessage);
//                    ReferenceCountUtil.retain(pspSessionNegotiationMessage);
//                    ctx.writeAndFlush(pspSessionNegotiationMessage);
//                }else {
//                    Log.e(TAG, "channelRead0: 协商过程中获取安全单元M2失败，错误码：" + pspSessionNegotiationLud);
//                    Log.e(TAG, "channelRead0: 用错误码重新获取安全单元信息");
//                    SSALMessage message = SSALMessage.giveMeOne(SSALConfigInitProperties.targetIp, SSALConfigInitProperties.targetPort, UserContentTypeOctEnum.OBTAIN_PSP_BASIC_INFO);
//                    ObtainPspBasicInfo obtainPspBasicInfo = KeyNegotiation.getKeyNegotiation().getSafeUnitMessage4();
//                    Log.e(TAG, "channelRead0: 安全单元信息Hex： " + obtainPspBasicInfo.getLudHex());
//                    message.loadLudHex(obtainPspBasicInfo.getLudHex(), FCCodeBinEnum.ZERO, pspSessionNegotiationLud);
//                    message.initCRC();
//                    Log.e(TAG, "channelRead0: 发送ssal数据: " + message);
//                    ctx.writeAndFlush(message);
////                    ByteBuf ssal = DefaultSSALParser.instance().loadSSAL(message);
//                }
                return;
            }else if (UserContentTypeOctEnum.SECURE_ACCESS_AREA_DEVICE_KEY_NEGOTIATION_TRIGGER.equals(userContentType) && SSALErrorHexEnum.SUCCESS.equals(backCode)){
                logger.info("会话协商成功，请继续进行业务操作！");
                DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.wrappedBuffer("会话协商成功，请继续进行业务操作！".getBytes()));
                ctx.fireChannelRead(httpResponse);
                return;
            }
            logger.error("received error msg from iGate, errorCode: {}", backCode);
			this.handlerSSALError(ctx, ssalMessage);
		}
	}

	/**
	 * SSAL错误的处理方法，所有的安全网关返回的错误信息，在这里做出自动处理
	 * */
	private void handlerSSALError(ChannelHandlerContext ctx, SSALMessage ssalMessage) {
        Attribute<String> protocolAttr = ctx.channel().attr(SSALConst.PROTOCOL_TAG_KEY);
        ProtocolEnum bizProtocol = ProtocolEnum.valueOf(protocolAttr.get());
        SSALErrorHexEnum errorCode = ssalMessage.getLinkUserData().getBackCode();
		switch (errorCode) {
            case PSP_DECRYPTION_ERROR:
                ssalErrorListener.onPspDecryptionError(ssalMessage, ctx, bizProtocol);
                break;
            case PSP_CHECK_SIGN_FAILED:
                ssalErrorListener.onPspCheckSignFailed(ssalMessage, ctx, bizProtocol);
                break;
            case PSP_CHECK_MAC_FAILED:
                ssalErrorListener.onPspCheckMacFailed(ssalMessage, ctx, bizProtocol);
                break;
            case SESSION_COUNTER_ERROR:
                ssalErrorListener.onSessionCounterError(ssalMessage, ctx, bizProtocol);
                break;
            case GATE_DECRYPTION_ERROR:
                ssalErrorListener.onGateDecryptionError(ssalMessage, ctx, bizProtocol);
                break;
            case GATE_CHECK_SIGN_FAILED:
                ssalErrorListener.onGateCheckSignFailed(ssalMessage, ctx, bizProtocol);
                break;
            case GATE_CHECK_MAC_FAILED:
                ssalErrorListener.onGateCheckMacFailed(ssalMessage, ctx, bizProtocol);
                break;
            case GATE_KEY_UNIT_ISSUE:
                ssalErrorListener.onGateKeyUnitIssue(ssalMessage, ctx, bizProtocol);
                break;
            case LINK_EQUIPMENT_KEY_UNIT_ERROR:
                ssalErrorListener.onLinkEquipmentKeyUnitError(ssalMessage, ctx, bizProtocol);
                break;

            case PROTOCOL_VERSION_ERROR:
                ssalErrorListener.onProtocolVersionError(ssalMessage, ctx, bizProtocol);
                break;
            case ENCRYPTION_ALGORITHM_FLAG_MISMATCH:
                ssalErrorListener.onEncryptionAlgorithmFlagMismatch(ssalMessage, ctx, bizProtocol);
                break;
            case DEVICE_TYPE_NOT_FOUND:
                ssalErrorListener.onDeviceTypeNotFound(ssalMessage, ctx, bizProtocol);
                break;
            case CONTROL_CODE_NOT_FOUND:
                ssalErrorListener.onControlCodeNotFound(ssalMessage, ctx, bizProtocol);
                break;
            case STARTUP_DIRECTION_ERROR:
                ssalErrorListener.onStartupDirectionError(ssalMessage, ctx, bizProtocol);
                break;
            case LUD_LENGTH_ABNORMAL_SHORTER_THAN_4_BYTES:
                ssalErrorListener.onLudLengthAbnormalShorterThan4Bytes(ssalMessage, ctx, bizProtocol);
                break;
            case LUD_LENGTH_MISMATCH:
                ssalErrorListener.onLudLengthMismatch(ssalMessage, ctx, bizProtocol);
                break;

            case TARGET_ADDRESS_NOT_EXIST:
                ssalErrorListener.onTargetAddressNotExist(ssalMessage, ctx, bizProtocol);
                break;
            case COMMUNICATE_CHANNEL_NOT_CREATED:
                ssalErrorListener.onCommunicateChannelNotCreated(ssalMessage, ctx, bizProtocol);
                break;
            case MSG_SEND_ERROR:
                ssalErrorListener.onMsgSendError(ssalMessage, ctx, bizProtocol);
                break;
            case CHANNEL_ERROR:
                ssalErrorListener.onChannelError(ssalMessage, ctx, bizProtocol);
                break;
            case CHANNEL_KEY_NEGOTIATION_FAILED:
                ssalErrorListener.onChannelKeyNegotiationFailed(ssalMessage, ctx, bizProtocol);
                break;

                default:
                    ctx.fireChannelRead(DefaultSSALParser.instance().removeSSAL(ssalMessage));
		}
	}

}
