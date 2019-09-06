package com.example.mynettydemo.ssalinstance.entity.bo.lud;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;

import org.apache.commons.lang.StringUtils;

//    网关与终端之间进行身份认证和会话密钥协商的报文，包括请求报文和响应报文。
//    具体协商报文定义与终端的类型相关，具体参见不同类型终端的协商机制描述。
//     c) 会话密钥协商报文
//   请求报文的数据长度为数据域部分的实际长度，为可变长度；数据域部分采用长度+内容的方式组合表示
public class PspSessionNegotiation extends MainLinkUserData {

    // 请求报文, 触发报文发出，收到触发成功的返回信息后收到的信息
    // 2字节
    private String m1LenHex;
    // 网关产生的会话密钥素材的密文
    private String m1Hex;

    // 2字节
    private String s1LenHex;
    // 请求报文的签名值
    private String s1Hex;

    // 响应报文
    // 2字节
    private String m2LenHex;
    // 全部会话密钥素材的密文，包括从网关接收到的以及终端自己产生的。
    private String m2Hex;

    // 2字节
    private String s2LenHex;
    // 响应报文的签名值
    private String s2Hex;


    public String getM1LenHex() {
        return m1LenHex;
    }

    public void setM1LenHex(String m1LenHex) {
        this.m1LenHex = m1LenHex;
    }

    public String getM1Hex() {
        return m1Hex;
    }

    public void setM1Hex(String m1Hex) {
        this.m1Hex = m1Hex;
    }

    public String getS1LenHex() {
        return s1LenHex;
    }

    public void setS1LenHex(String s1LenHex) {
        this.s1LenHex = s1LenHex;
    }

    public String getS1Hex() {
        return s1Hex;
    }

    public void setS1Hex(String s1Hex) {
        this.s1Hex = s1Hex;
    }

    public String getM2LenHex() {
        return m2LenHex;
    }

    public void setM2LenHex(String m2LenHex) {
        this.m2LenHex = m2LenHex;
    }

    public String getM2Hex() {
        return m2Hex;
    }

    public void setM2Hex(String m2Hex) {
        this.m2Hex = m2Hex;
    }

    public String getS2LenHex() {
        return s2LenHex;
    }

    public void setS2LenHex(String s2LenHex) {
        this.s2LenHex = s2LenHex;
    }

    public String getS2Hex() {
        return s2Hex;
    }

    public void setS2Hex(String s2Hex) {
        this.s2Hex = s2Hex;
    }

    @Override
    public String toString() {
        return "PspSessionNegotiation{" +
                "m1LenHex='" + m1LenHex + '\'' +
                ", m1Hex='" + m1Hex + '\'' +
                ", s1LenHex='" + s1LenHex + '\'' +
                ", s1Hex='" + s1Hex + '\'' +
                ", m2LenHex='" + m2LenHex + '\'' +
                ", m2Hex='" + m2Hex + '\'' +
                ", s2LenHex='" + s2LenHex + '\'' +
                ", s2Hex='" + s2Hex + '\'' +
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

        String ludHex = this.getLudHex();
        if (this.getLudLengthOct() == 0) return hexString;
        m1LenHex = ludHex.substring(0, 4);
        int m1Len = Integer.parseInt(m1LenHex.substring(2, 4) + m1LenHex.substring(0, 2), 16);
        m1Hex = ludHex.substring(4, 4 + m1Len * 2);
        ludHex = ludHex.substring(4 + m1Len * 2);

        s1LenHex = ludHex.substring(0, 4);
        int s1Len = Integer.parseInt(s1LenHex.substring(2, 4) + s1LenHex.substring(0, 2), 16);
        s1Hex = ludHex.substring(4, 4 + s1Len * 2);
        return hexString;
    }

    public PspSessionNegotiation() {
    }

    public PspSessionNegotiation(String m2Hex, String s2Hex) {
        this.m2Hex = m2Hex;
        this.s2Hex = s2Hex;

        String m2Len = StringUtils.leftPad(ByteTools.int2HexStr(this.m2Hex.length() / 2), 4, '0');
        m2LenHex = m2Len.substring(2, 4) + m2Len.substring(0, 2);

        String s2Len = StringUtils.leftPad(ByteTools.int2HexStr(this.s2Hex.length() / 2), 4, '0');
        s2LenHex = s2Len.substring(2, 4) + s2Len.substring(0, 2);

        this.ludHex = this.m2LenHex + this.m2Hex + this.s2LenHex + this.s2Hex;
        this.ludLengthOct = this.ludHex.length() / 2;
        this.ludLengthHex = StringUtils.leftPad(ByteTools.int2HexStr(this.ludLengthOct), 4, '0');
        this.ludLengthHexRevrs = this.ludLengthHex.substring(2, 4) + this.ludLengthHex.substring(0, 2);
        this.mainLinkUserDataHex = this.ludLengthHexRevrs + this.ludHex;
    }
}
