package com.example.mynettydemo.ssalinstance.handler;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.client.CallbackListener;
import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.ProtocolEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;

public class HttpOverSSALClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(HttpOverSSALClientHandler.class);
    private FullHttpRequest request;
    private CallbackListener<FullHttpResponse> responseCallbackListener;
    private EncryptListener encryptListener;
    private static final String TAG = "ssal";


    public HttpOverSSALClientHandler(FullHttpRequest request, CallbackListener<FullHttpResponse> responseCallbackListener, EncryptListener encryptListener) {
        this.request = request;
        this.responseCallbackListener = responseCallbackListener;
        this.encryptListener = encryptListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为channel打一个应用层网络协议的标记，用于SSAL错误无法处理时返回错误信息用
        Attribute<String> protocolAttr = ctx.channel().attr(SSALConst.PROTOCOL_TAG_KEY);
        protocolAttr.setIfAbsent(ProtocolEnum.HTTP.name());
        byte[] requestBytes = this.prepareRequestBytesArr();
        ByteBuf plainByteBuf = Unpooled.wrappedBuffer(requestBytes);
        int businessIdInt = RandomUtils.nextInt(65535);
        String businessIdHex = StringUtils.leftPad(ByteTools.int2HexStr(businessIdInt), 4, '0');
        Log.e(TAG, "channelActive: 业务码== " + businessIdHex);
        List<SSALMessage> ssalMessageList = DefaultSSALParser.instance().parseReverse(plainByteBuf, SSALConfigInitProperties.targetIp, SSALConfigInitProperties.targetPort, SSALConfigInitProperties.pspMACAddress, businessIdHex, UserContentTypeOctEnum.APPLICATION_DATA, FCCodeBinEnum.ONE, encryptListener);
        for (SSALMessage message : ssalMessageList) {
            ctx.writeAndFlush(message);
        }
    }

    /**
     * 将FullHttpRequest转字节数组
     *
     * @return 整个http请求的byte数组
     */
    private byte[] prepareRequestBytesArr() {
        String requestLine = request.method().name() + " " + request.uri() + " " + request.protocolVersion().text() + "\r\n";
        StringBuilder headerLines = new StringBuilder();
        HttpHeaders headers = request.headers();
        Set<String> headerNameSet = headers.names();
        for (String headerName : headerNameSet) {
            headerLines.append(headerName).append(": ").append(headers.get(headerName)).append("\r\n");
        }
        headerLines.append("\r\n");
        byte[] byteMerger0 = ByteTools.byteMerger(requestLine.getBytes(), headerLines.toString().getBytes());
        ByteBuf content = request.content();
        int readableBytes = content.readableBytes();
        byte[] contentBytes = new byte[readableBytes];
        content.readBytes(contentBytes);
        request.release();
        return ByteTools.byteMerger(byteMerger0, contentBytes);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        if (response.status().code() == 999) return;

        if (responseCallbackListener != null) responseCallbackListener.onMessage(response);
        response.release();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {


//        FullHttpResponse
//        if (responseCallbackListener != null) responseCallbackListener.onMessage(response);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            IdleState state = idleStateEvent.state();
            if (state == IdleState.READER_IDLE) {
                Log.e(TAG, "userEventTriggered: 读超时");
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_TIMEOUT, Unpooled.wrappedBuffer("request time out!".getBytes()));
                if (responseCallbackListener != null) responseCallbackListener.onMessage(response);
                ctx.close();
            }
        }
    }
}
