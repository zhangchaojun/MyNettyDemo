package com.example.mynettydemo.ssalinstance.entity.bo.lud;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;

//  d) 会话申请报文
//        报文的数据长度为数据域部分的实际长度，为可变长度；数据域部分采用长度+内容的方式组合表示
public class PspSessionPrompt extends MainLinkUserData {

    public PspSessionPrompt() {
    }

    public PspSessionPrompt(String cert) {
        this.cert = cert;
        String certLen = ByteTools.int2HexStr(cert.length() / 2);
        certLenHex = certLen.substring(2, 4) + certLen.substring(0, 2);
        ludHex = certLenHex + cert;
        ludLengthOct = ludHex.length() / 2;
        ludLengthHex = ByteTools.int2HexStr(ludLengthOct);
        ludLengthHexRevrs = ludLengthHex.substring(2, 4) + ludLengthHex.substring(0, 2);
        mainLinkUserDataHex = ludLengthHexRevrs + ludHex;
    }

    // 2字节
    private String certLenHex;
    // 移动终端的设备证书
    private String cert;

    public String getCertLenHex() {
        return certLenHex;
    }

    public void setCertLenHex(String certLenHex) {
        this.certLenHex = certLenHex;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    @Override
    public String toString() {
        return "PspSessionPrompt{" +
                "certLenHex='" + certLenHex + '\'' +
                ", cert='" + cert + '\'' +
                ", mainLinkUserDataHex='" + mainLinkUserDataHex + '\'' +
                ", ludLengthHexRevrs='" + ludLengthHexRevrs + '\'' +
                ", ludLengthHex='" + ludLengthHex + '\'' +
                ", ludLengthOct=" + ludLengthOct +
                ", backCodeHexRevrs='" + backCodeHexRevrs + '\'' +
                ", backCodeHex='" + backCodeHex + '\'' +
                ", backCodeOct=" + backCodeOct +
                ", backCode=" + backCode +
                ", ludHex='" + ludHex + '\'' +
//                ", ludPlain='" + ludPlain + '\'' +
                '}';
    }

    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        return hexString;
    }
}
