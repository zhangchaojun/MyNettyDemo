package com.example.mynettydemo.ssalinstance.entity.bo.lud;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

//i) 终端网关证书更新报文
//        保留，暂未定义。
public class PspGateCertUpdate extends MainLinkUserData {
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        // @TODO parse msg
        return hexString;
    }
}
