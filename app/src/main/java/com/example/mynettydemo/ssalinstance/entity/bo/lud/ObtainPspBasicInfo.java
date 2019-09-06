package com.example.mynettydemo.ssalinstance.entity.bo.lud;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;

import org.apache.commons.lang.StringUtils;

//  /*网关获取终端基本信息的报文，包括请求报文和响应报文。*/
//  b) 获取终端基本信息报文
//        请求报文的数据长度为0，数据域为空。
//        响应报文的数据长度根据实际返回内容的长度填写，返回信息根据实际情况填写。
//        数据域部分采用长度+内容的方式组合表示获取到的终端基本信息，如果某个基本信息没有则将其长度设置为0即可。
public class ObtainPspBasicInfo extends MainLinkUserData {
    public static final String REQ_HEX = StringUtils.EMPTY;

    // Data1的长度, 必选
    private String field1LenHex = "0800";
    // ESAM序列号, 长度为8字节。必选
    private String esamSNHex;

    // Data2的长度
    private String field2LenHex = "0400";
    // ESAM版本号, 长度为4字节。可选
    private String esamVersionHex;

    // Data3的长度
    private String field3LenHex = "1000";
    // 链路对称密钥版本, 长度为16字节。必选
    private String linkKeyVersionHex;

    // Data4的长度
    private String field4LenHex = "0200";
    // 证书版本, 长度为2字节。网关证书版本+终端证书版本, 可选
    private String certVersionHex;

    // Data5的长度
    private String field5LenHex = "0400";
    // 链路会话时效门限, 长度为4字节。可选
    private String linkSessionFailureThresholdHex;

    // Data6的长度
    private String field6LenHex = "0400";
    // 链路会话时效剩余时间, 长度为4字节。可选
    private String linkSessionThresholdRestTimeHex;

    // Data7的长度
    private String field7LenHex = "0800";
    // 当前计数器（终端自己维护）, 长度为8字节。必选
    private String currentCounterHex;

    // Data8的长度
    private String field8LenHex = "1000";
    // 链路证书序列号, 长度为16字节。必选
    private String linkCertSNHex;

    // Data9的长度
    private String field9LenHex;
    // 链路证书, 长度可变，由len9定义。必选
    private String linkCertHex;

    // Data10的长度
    private String field10LenHex = "1000";
    // 网关证书序列号, 长度为16字节。可选
    private String gateCertSNHex;

    // Data11的长度
    private String field11LenHex = "1800";
    // 终端序列号, 长度为24字节。必选
    private String pspSNHex;

    // Data12的长度
    private String field12LenHex = "0000";
    // 终端ESN号, 长度为20字节。可选
    private String pspESNHex;

    public ObtainPspBasicInfo() {
    }

    public ObtainPspBasicInfo(String esamSNHex, String esamVersionHex, String linkKeyVersionHex, String certVersionHex, String linkSessionFailureThresholdHex, String linkSessionThresholdRestTimeHex, String currentCounterHex, String linkCertSNHex, String linkCertHex, String gateCertSNHex, String pspSNHex, String pspESNHex) {
        this.esamSNHex = StringUtils.isEmpty(esamSNHex) ? StringUtils.EMPTY : StringUtils.leftPad(esamSNHex.length() % 2 == 0 ? esamSNHex : "0" + esamSNHex, 8 * 2, '0');
        if (StringUtils.isEmpty(this.esamSNHex)) this.field1LenHex = "0000";

        this.esamVersionHex = StringUtils.isEmpty(esamVersionHex) ? StringUtils.EMPTY : StringUtils.leftPad(esamVersionHex.length() % 2 == 0 ? esamVersionHex : "0" + esamVersionHex, 4 * 2, '0');
        if (StringUtils.isEmpty(this.esamVersionHex)) this.field2LenHex = "0000";

        this.linkKeyVersionHex = StringUtils.isEmpty(linkKeyVersionHex) ? StringUtils.EMPTY : StringUtils.leftPad(linkKeyVersionHex.length() % 2 == 0 ? linkKeyVersionHex : "0" + linkKeyVersionHex, 16 * 2, '0');
        if (StringUtils.isEmpty(this.linkKeyVersionHex)) this.field3LenHex = "0000";

        this.certVersionHex = StringUtils.isEmpty(certVersionHex) ? StringUtils.EMPTY : StringUtils.leftPad(certVersionHex.length() % 2 == 0 ? certVersionHex : "0" + certVersionHex, 2 * 2, '0');
        if (StringUtils.isEmpty(this.certVersionHex)) this.field4LenHex = "0000";

        this.linkSessionFailureThresholdHex = StringUtils.isEmpty(linkSessionFailureThresholdHex) ? StringUtils.EMPTY : StringUtils.leftPad(linkSessionFailureThresholdHex.length() % 2 == 0 ? linkSessionFailureThresholdHex : "0" + linkSessionFailureThresholdHex, 4 * 2, '0');
        if (StringUtils.isEmpty(this.linkSessionFailureThresholdHex)) this.field5LenHex = "0000";

        this.linkSessionThresholdRestTimeHex = StringUtils.isEmpty(linkSessionThresholdRestTimeHex) ? StringUtils.EMPTY : StringUtils.leftPad(linkSessionThresholdRestTimeHex.length() % 2 == 0 ? linkSessionThresholdRestTimeHex : "0" + linkSessionThresholdRestTimeHex, 4 * 2, '0');
        if (StringUtils.isEmpty(this.linkSessionThresholdRestTimeHex)) this.field6LenHex = "0000";

        this.currentCounterHex = StringUtils.isEmpty(currentCounterHex) ? StringUtils.EMPTY : StringUtils.leftPad(currentCounterHex.length() % 2 == 0 ? currentCounterHex : "0" + currentCounterHex, 8 * 2, '0');
        if (StringUtils.isEmpty(this.currentCounterHex)) this.field7LenHex = "0000";

        this.linkCertSNHex = StringUtils.isEmpty(linkCertSNHex) ? StringUtils.EMPTY : StringUtils.leftPad(linkCertSNHex.length() % 2 == 0 ? linkCertSNHex : "0" + linkCertSNHex, 16 * 2, '0');
        if (StringUtils.isEmpty(this.linkCertSNHex)) this.field8LenHex = "0000";

        this.linkCertHex = StringUtils.isEmpty(linkCertHex) ? StringUtils.EMPTY : linkCertHex.length() % 2 == 0 ? linkCertHex : "0" + linkCertHex;
        String field9Len = StringUtils.leftPad(ByteTools.int2HexStr(this.linkCertHex.length() / 2), 4, '0');
        this.field9LenHex = field9Len.substring(2, 4) + field9Len.substring(0, 2);

        this.gateCertSNHex = StringUtils.isEmpty(gateCertSNHex) ? StringUtils.EMPTY : StringUtils.leftPad(gateCertSNHex.length() % 2 == 0 ? gateCertSNHex : "0" + gateCertSNHex, 16 * 2, '0');
        if (StringUtils.isEmpty(this.gateCertSNHex)) this.field10LenHex = "0000";

        this.pspSNHex = StringUtils.isEmpty(pspSNHex) ? StringUtils.EMPTY : StringUtils.leftPad(pspSNHex.length() % 2 == 0 ? pspSNHex : "0" + pspSNHex, 24 * 2, '0');
        if (StringUtils.isEmpty(this.pspSNHex)) this.field11LenHex = "0000";

        this.pspESNHex = StringUtils.isEmpty(pspESNHex) ? StringUtils.EMPTY : StringUtils.leftPad(pspESNHex.length() % 2 == 0 ? pspESNHex : "0" + pspESNHex, 20 * 2, '0');
        if (StringUtils.isEmpty(this.pspESNHex)) this.field12LenHex = "0000";

        this.ludHex = this.field1LenHex + this.esamSNHex
                    + this.field2LenHex + this.esamVersionHex
                    + this.field3LenHex + this.linkKeyVersionHex
                    + this.field4LenHex + this.certVersionHex
                    + this.field5LenHex + this.linkSessionFailureThresholdHex
                    + this.field6LenHex + this.linkSessionThresholdRestTimeHex
                    + this.field7LenHex + this.currentCounterHex
                    + this.field8LenHex + this.linkCertSNHex
                    + this.field9LenHex + this.linkCertHex
                    + this.field10LenHex + this.gateCertSNHex
                    + this.field11LenHex + this.pspSNHex
                    + this.field12LenHex + this.pspESNHex;
        this.ludLengthOct = this.ludHex.length() / 2;
        this.ludLengthHex = StringUtils.leftPad(ByteTools.int2HexStr(this.ludLengthOct), 4, '0');
        this.ludLengthHexRevrs = this.ludLengthHex.substring(2, 4) + this.ludLengthHex.substring(0, 2);
        this.mainLinkUserDataHex = this.ludLengthHexRevrs + this.ludHex;
    }

    public String getField1LenHex() {
        return field1LenHex;
    }

    public void setField1LenHex(String field1LenHex) {
        this.field1LenHex = field1LenHex;
    }

    public String getEsamSNHex() {
        return esamSNHex;
    }

    public void setEsamSNHex(String esamSNHex) {
        this.esamSNHex = esamSNHex;
    }

    public String getField2LenHex() {
        return field2LenHex;
    }

    public void setField2LenHex(String field2LenHex) {
        this.field2LenHex = field2LenHex;
    }

    public String getEsamVersionHex() {
        return esamVersionHex;
    }

    public void setEsamVersionHex(String esamVersionHex) {
        this.esamVersionHex = esamVersionHex;
    }

    public String getField3LenHex() {
        return field3LenHex;
    }

    public void setField3LenHex(String field3LenHex) {
        this.field3LenHex = field3LenHex;
    }

    public String getLinkKeyVersionHex() {
        return linkKeyVersionHex;
    }

    public void setLinkKeyVersionHex(String linkKeyVersionHex) {
        this.linkKeyVersionHex = linkKeyVersionHex;
    }

    public String getField4LenHex() {
        return field4LenHex;
    }

    public void setField4LenHex(String field4LenHex) {
        this.field4LenHex = field4LenHex;
    }

    public String getCertVersionHex() {
        return certVersionHex;
    }

    public void setCertVersionHex(String certVersionHex) {
        this.certVersionHex = certVersionHex;
    }

    public String getField5LenHex() {
        return field5LenHex;
    }

    public void setField5LenHex(String field5LenHex) {
        this.field5LenHex = field5LenHex;
    }

    public String getLinkSessionFailureThresholdHex() {
        return linkSessionFailureThresholdHex;
    }

    public void setLinkSessionFailureThresholdHex(String linkSessionFailureThresholdHex) {
        this.linkSessionFailureThresholdHex = linkSessionFailureThresholdHex;
    }

    public String getField6LenHex() {
        return field6LenHex;
    }

    public void setField6LenHex(String field6LenHex) {
        this.field6LenHex = field6LenHex;
    }

    public String getLinkSessionThresholdRestTimeHex() {
        return linkSessionThresholdRestTimeHex;
    }

    public void setLinkSessionThresholdRestTimeHex(String linkSessionThresholdRestTimeHex) {
        this.linkSessionThresholdRestTimeHex = linkSessionThresholdRestTimeHex;
    }

    public String getField7LenHex() {
        return field7LenHex;
    }

    public void setField7LenHex(String field7LenHex) {
        this.field7LenHex = field7LenHex;
    }

    public String getCurrentCounterHex() {
        return currentCounterHex;
    }

    public void setCurrentCounterHex(String currentCounterHex) {
        this.currentCounterHex = currentCounterHex;
    }

    public String getField8LenHex() {
        return field8LenHex;
    }

    public void setField8LenHex(String field8LenHex) {
        this.field8LenHex = field8LenHex;
    }

    public String getLinkCertSNHex() {
        return linkCertSNHex;
    }

    public void setLinkCertSNHex(String linkCertSNHex) {
        this.linkCertSNHex = linkCertSNHex;
    }

    public String getField9LenHex() {
        return field9LenHex;
    }

    public void setField9LenHex(String field9LenHex) {
        this.field9LenHex = field9LenHex;
    }

    public String getLinkCertHex() {
        return linkCertHex;
    }

    public void setLinkCertHex(String linkCertHex) {
        this.linkCertHex = linkCertHex;
    }

    public String getField10LenHex() {
        return field10LenHex;
    }

    public void setField10LenHex(String field10LenHex) {
        this.field10LenHex = field10LenHex;
    }

    public String getGateCertSNHex() {
        return gateCertSNHex;
    }

    public void setGateCertSNHex(String gateCertSNHex) {
        this.gateCertSNHex = gateCertSNHex;
    }

    public String getField11LenHex() {
        return field11LenHex;
    }

    public void setField11LenHex(String field11LenHex) {
        this.field11LenHex = field11LenHex;
    }

    public String getPspSNHex() {
        return pspSNHex;
    }

    public void setPspSNHex(String pspSNHex) {
        this.pspSNHex = pspSNHex;
    }

    public String getField12LenHex() {
        return field12LenHex;
    }

    public void setField12LenHex(String field12LenHex) {
        this.field12LenHex = field12LenHex;
    }

    public String getPspESNHex() {
        return pspESNHex;
    }

    public void setPspESNHex(String pspESNHex) {
        this.pspESNHex = pspESNHex;
    }

    @Override
    public String toString() {
        return "ObtainPspBasicInfo{" +
                "field1LenHex='" + field1LenHex + '\'' +
                ", esamSNHex='" + esamSNHex + '\'' +
                ", field2LenHex='" + field2LenHex + '\'' +
                ", esamVersionHex='" + esamVersionHex + '\'' +
                ", field3LenHex='" + field3LenHex + '\'' +
                ", linkKeyVersionHex='" + linkKeyVersionHex + '\'' +
                ", field4LenHex='" + field4LenHex + '\'' +
                ", certVersionHex='" + certVersionHex + '\'' +
                ", field5LenHex='" + field5LenHex + '\'' +
                ", linkSessionFailureThresholdHex='" + linkSessionFailureThresholdHex + '\'' +
                ", field6LenHex='" + field6LenHex + '\'' +
                ", linkSessionThresholdRestTimeHex='" + linkSessionThresholdRestTimeHex + '\'' +
                ", field7LenHex='" + field7LenHex + '\'' +
                ", currentCounterHex='" + currentCounterHex + '\'' +
                ", field8LenHex='" + field8LenHex + '\'' +
                ", linkCertSNHex='" + linkCertSNHex + '\'' +
                ", field9LenHex='" + field9LenHex + '\'' +
                ", linkCertHex='" + linkCertHex + '\'' +
                ", field10LenHex='" + field10LenHex + '\'' +
                ", gateCertSNHex='" + gateCertSNHex + '\'' +
                ", field11LenHex='" + field11LenHex + '\'' +
                ", pspSNHex='" + pspSNHex + '\'' +
                ", field12LenHex='" + field12LenHex + '\'' +
                ", pspESNHex='" + pspESNHex + '\'' +
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
        // @TODO parse msg
        return hexString;
    }
}
