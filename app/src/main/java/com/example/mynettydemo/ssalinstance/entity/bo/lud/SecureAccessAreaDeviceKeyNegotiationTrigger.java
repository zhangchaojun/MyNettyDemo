package com.example.mynettydemo.ssalinstance.entity.bo.lud;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

import org.apache.commons.lang.StringUtils;


//  k) 安全接入区设备密钥协商触发报文
//        安全接入区设备密钥协商触发报文，由安全接入区设备发起，触发网关与安全接入区设备进行会话密钥协商。
//        请求报文数据长度为0，数据段为空。
//        响应报文数据长度为0，返回信息填写实际错误码，数据段为空。
public class SecureAccessAreaDeviceKeyNegotiationTrigger extends MainLinkUserData {
    public static final String NEGOTIATION_TRIGGER_LUD_HEX = StringUtils.EMPTY;
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        return hexString;
    }
}
