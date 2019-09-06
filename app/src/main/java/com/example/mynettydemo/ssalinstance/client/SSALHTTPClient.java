package com.example.mynettydemo.ssalinstance.client;


import com.example.mynettydemo.ssalinstance.client.model.SSALServerProperties;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;


public interface SSALHTTPClient {

    /**
     * [客户端通过SSAL协议发起http请求的功能接口]
     *
     * @param ssalServerProperties SSAL Server信息, 不允许为空
     * @param request              完整的请求报文的对象封装, 不允许为空
     * @param listener             对http响应的处理回调, 允许为空，表示不做处理
     * @throws IllegalArgumentException 当参数ssalServerProperties为空是会抛出非法参数异常
     */
    void sendHttpRequest(
            SSALServerProperties ssalServerProperties,
            FullHttpRequest request,
            CallbackListener<FullHttpResponse> listener) throws Exception;
}
