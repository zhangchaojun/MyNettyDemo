package com.example.mynettydemo.ssalinstance.entity.bo.lud;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

//  e) 会话确认报文
//        报文的数据长度为数据域部分的实际长度，为可变长度；数据域部分采用长度+内容的方式组合表示
public class SessionNegotiationComplete extends MainLinkUserData {
    // 2字节
    private String m3LenHex;
    // 会话密钥对R4加密的密文，加密的算法通过
    // SSAL协议版本字段中的加密算法字段和设备地址类型字段中设备类型字段共同决定。
    private String m3;

    // 2字节
    private String s3LenHex;
    // 对R4密文的签名值。
    private String s3;

    public SessionNegotiationComplete() {
    }

    public String getM3LenHex() {
        return m3LenHex;
    }

    public void setM3LenHex(String m3LenHex) {
        this.m3LenHex = m3LenHex;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }

    public String getS3LenHex() {
        return s3LenHex;
    }

    public void setS3LenHex(String s3LenHex) {
        this.s3LenHex = s3LenHex;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    @Override
    public String toString() {
        return "SessionNegotiationComplete{" +
                "m3LenHex='" + m3LenHex + '\'' +
                ", m3='" + m3 + '\'' +
                ", s3LenHex='" + s3LenHex + '\'' +
                ", s3='" + s3 + '\'' +
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
        String ludHex = this.ludHex;
        m3LenHex = ludHex.substring(0, 4);
        int m3Len = Integer.parseInt(m3LenHex.substring(2, 4) + m3LenHex.substring(0, 2), 16);
        m3 = ludHex.substring(4, 4 + m3Len * 2);
        ludHex = ludHex.substring(4 + m3Len * 2);

        s3LenHex = ludHex.substring(0, 4);
        int s3Len = Integer.parseInt(s3LenHex.substring(2, 4) + s3LenHex.substring(0, 2), 16);
        s3 = ludHex.substring(4, 4 + s3Len * 2);

        return hexString;
    }
}
