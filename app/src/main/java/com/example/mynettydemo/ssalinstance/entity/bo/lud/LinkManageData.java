package com.example.mynettydemo.ssalinstance.entity.bo.lud;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

/// *终端的链路报文，包括：登录报文、心跳报文等，报文格式符合终端的业务报文协议要求。*/
//    a) 链路管理报文
//        链路管理报文包括：登录报文、心跳报文
public class LinkManageData extends MainLinkUserData {
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        // @TODO parse msg
        return hexString;
    }
}
