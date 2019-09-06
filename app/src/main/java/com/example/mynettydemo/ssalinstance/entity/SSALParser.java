package com.example.mynettydemo.ssalinstance.entity;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;

import io.netty.buffer.ByteBuf;

import java.util.List;

public interface SSALParser {

    /**
     * [Decode使用]
    *把SSAL格式[HEXString]的网络报文解析成一份普通的易操作的SSALMessage
     * @throws StringIndexOutOfBoundsException
    * */
    SSALMessage parse(String hexString, EncryptListener encryptListener) throws StringIndexOutOfBoundsException;

    /**
     * [配合Decode使用]
     * 把SSALMessage中的用户数据取出，转回为普通网络数据
     * */
    ByteBuf removeSSAL(SSALMessage ssalMessage);

    /**
     * [Encode使用]
     *把普通（http/ws/mqtt/...等基于tcp协议传输的）报文转化为一个易操作的SSALMessage集合
     * */
    List<SSALMessage> parseReverse(ByteBuf plainByteBuf, String targetIp, int targetPort, String da, String businessId, UserContentTypeOctEnum userContentType, FCCodeBinEnum startupSymbol, EncryptListener encryptListener);

    /**
     *  [配合Encode使用]
     * 把SSALMessage对象转化为SSAL格式的网络数据
     * */
    ByteBuf loadSSAL(SSALMessage ssalMessage);

    /**
     *  [配合Decode使用]
     * 把SSALMessage对象转化为SSAL格式的网络数据
     * */
    ByteBuf removeSSAL(List<SSALMessage> ssalMessageList);
}
