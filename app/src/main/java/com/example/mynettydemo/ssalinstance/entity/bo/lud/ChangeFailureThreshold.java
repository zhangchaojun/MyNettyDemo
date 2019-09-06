package com.example.mynettydemo.ssalinstance.entity.bo.lud;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

//更改终端会话密钥时效门限的报文
//保留，暂未定义。
public class ChangeFailureThreshold extends MainLinkUserData {
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        // @TODO parse msg
        return hexString;
    }
}
