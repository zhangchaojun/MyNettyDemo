package com.example.mynettydemo.ssalinstance.entity.bo;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.ApplicationData;
import com.example.mynettydemo.ssalinstance.enums.DeviceTypeOctEnum;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.SSALErrorHexEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*6.6 链路用户数据LUD
链路用户数据LUD，定义了主站与终端之间、终端与网关之间的报文的具体内容。
链路用户数据包含一个完整的应用层协议数据单元（APDU）字节序列或APDU的分帧片段。
由多个字节组成，长度可变*/
public abstract class MainLinkUserData implements Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(MainLinkUserData.class);

    protected String mainLinkUserDataHex;
    /*数据长度字段固定为2字节*/
    protected String ludLengthHexRevrs;
    protected String ludLengthHex;
    protected int ludLengthOct;

    protected String backCodeHexRevrs;
    protected String backCodeHex;
    protected int backCodeOct;
    protected SSALErrorHexEnum backCode;

    protected String ludHex;
//    protected String ludPlain;

    public String getMainLinkUserDataHex() {
        return mainLinkUserDataHex;
    }

    public void setMainLinkUserDataHex(String mainLinkUserDataHex) {
        this.mainLinkUserDataHex = mainLinkUserDataHex;
    }

    public String getLudHex() {
        return ludHex;
    }

    public void setLudHex(String ludHex) {
        this.ludHex = ludHex;
    }

//    public String getLudPlain() {
//        return ludPlain;
//    }
//
//    public void setLudPlain(String ludPlain) {
//        this.ludPlain = ludPlain;
//    }

    public String getLudLengthHexRevrs() {
        return ludLengthHexRevrs;
    }

    public void setLudLengthHexRevrs(String ludLengthHexRevrs) {
        this.ludLengthHexRevrs = ludLengthHexRevrs;
    }

    public String getLudLengthHex() {
        return ludLengthHex;
    }

    public void setLudLengthHex(String ludLengthHex) {
        this.ludLengthHex = ludLengthHex;
    }

    public int getLudLengthOct() {
        return ludLengthOct;
    }

    public void setLudLengthOct(int ludLengthOct) {
        this.ludLengthOct = ludLengthOct;
    }

    public SSALErrorHexEnum getBackCode() {
        return backCode;
    }

    public void setBackCode(SSALErrorHexEnum backCode) {
        this.backCode = backCode;
    }

    public String getBackCodeHexRevrs() {
        return backCodeHexRevrs;
    }

    public void setBackCodeHexRevrs(String backCodeHexRevrs) {
        this.backCodeHexRevrs = backCodeHexRevrs;
    }

    public String getBackCodeHex() {
        return backCodeHex;
    }

    public void setBackCodeHex(String backCodeHex) {
        this.backCodeHex = backCodeHex;
    }

    public int getBackCodeOct() {
        return backCodeOct;
    }

    public void setBackCodeOct(int backCodeOct) {
        this.backCodeOct = backCodeOct;
    }

    public MainLinkUserData copy() {
        try {
            return (MainLinkUserData) this.clone();
        }catch (CloneNotSupportedException e) {
            logger.error(e.getMessage());
            return this;
        }
    }

    @Override
    public String toString() {
        return "MainLinkUserData{" +
                "mainLinkUserDataHex='" + mainLinkUserDataHex + '\'' +
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

    /**
     * 只有应用数据需要过加解密，其他类型的报文数据不需要过加解密
     * */
    protected String initBackCodeByHexStringIfNeeded(String hexString, FCCodeBinEnum startUpSymbol) {
        String ludLengthHexRevrs = hexString.substring(0, 4);
        String ludLenghtHex = ludLengthHexRevrs.substring(2, 4) + ludLengthHexRevrs.substring(0, 2);
        this.setLudLengthHex(ludLenghtHex);
        this.setLudLengthHexRevrs(ludLengthHexRevrs);
        int ludLengthOct = Integer.parseInt(ludLenghtHex, 16);
        this.setLudLengthOct(ludLengthOct);
        hexString = hexString.substring(4);
        hexString = cutBackCodeIfNeed(hexString, startUpSymbol);

        String ludHex = hexString.substring(0, this.getLudLengthOct() * 2);
        this.setLudHex(ludHex);
        this.setMainLinkUserDataHex(this.getLudLengthHex() + this.getLudHex());
//        this.setLudPlain(ByteTools.hexStr2Str(ludHex));
        return hexString;
    }

    protected String initBackCodeByHexStringIfNeeded(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        String ludLengthHexRevrs = hexString.substring(0, 4);
        String ludLenghtHex = ludLengthHexRevrs.substring(2, 4) + ludLengthHexRevrs.substring(0, 2);
        this.setLudLengthHex(ludLenghtHex);
        this.setLudLengthHexRevrs(ludLengthHexRevrs);
        int ludLengthOct = Integer.parseInt(ludLenghtHex, 16);
        this.setLudLengthOct(ludLengthOct);
        hexString = hexString.substring(4);
        hexString = cutBackCodeIfNeed(hexString, startUpSymbol);

        String ludHex = hexString.substring(0, this.getLudLengthOct() * 2);
        if (SSALConfigInitProperties.encryptEnabled) ludHex = encryptListener.decrypt(ludHex);
        this.setLudHex(ludHex);
        this.setMainLinkUserDataHex(this.getLudLengthHex() + this.getLudHex());
//        this.setLudPlain(ByteTools.hexStr2Str(ludHex));
        return hexString;
    }

    private String cutBackCodeIfNeed(String hexString, FCCodeBinEnum startUpSymbol) {
        if (FCCodeBinEnum.ZERO.equals(startUpSymbol)) {
            //从动站需要返回码
            String backCodeHexRevrs = hexString.substring(0, 4);
            this.setBackCodeHexRevrs(backCodeHexRevrs);
            String backCodeHex = backCodeHexRevrs.substring(2, 4) + backCodeHexRevrs.substring(0, 2);
            this.setBackCodeHex(backCodeHex);
            int backCodeOct = Integer.parseInt(backCodeHex, 16);
            this.setBackCodeOct(backCodeOct);
            this.setBackCode(SSALErrorHexEnum.getByValue(String.valueOf(backCodeOct), 10));
            hexString = hexString.substring(4);
        }
        return hexString;
    }

    public static MainLinkUserData buildFromLudHex(String ludHex, FCCodeBinEnum startupSymbol, int currentPkgNum, int pkgNum) {
        ApplicationData linkUserData = new ApplicationData();
        if (!StringUtils.isEmpty(ludHex)) {
            linkUserData.setCurrentPkgNum(currentPkgNum);
            String currentPkgNumHexTmp = StringUtils.leftPad(ByteTools.int2HexStr(currentPkgNum), 4, '0');
            linkUserData.setCurrentPkgNumHex(currentPkgNumHexTmp.substring(2, 4) + currentPkgNumHexTmp.substring(0, 2));
            linkUserData.setPkgNum(pkgNum);
            String pkgNumHexTmp = StringUtils.leftPad(ByteTools.int2HexStr(pkgNum), 4, '0');
            linkUserData.setPkgNumHex(pkgNumHexTmp.substring(2, 4) + pkgNumHexTmp.substring(0, 2));
//            linkUserData.setLudPlain(ByteTools.hexStr2Str(linkUserData.getPkgNumHex() + linkUserData.getCurrentPkgNumHex() + ludHex));
            linkUserData.setLudLengthOct(ludHex.length() / 2 + 2 + 2);
            linkUserData.setLudLengthHex(StringUtils.leftPad(ByteTools.int2HexStr(linkUserData.getLudLengthOct()), 4, '0'));
            linkUserData.setLudLengthHexRevrs(linkUserData.getLudLengthHex().substring(2, 4) + linkUserData.getLudLengthHex().substring(0, 2));
        }else {
            linkUserData.setLudLengthHex("0000");
            linkUserData.setLudLengthHexRevrs("0000");
        }
        String ludHexTmp = linkUserData.getPkgNumHex() + linkUserData.getCurrentPkgNumHex() + ludHex;
        if (FCCodeBinEnum.ZERO.equals(startupSymbol) || !DeviceTypeOctEnum.PSP.equals(SSALConfigInitProperties.deviceType)) {
//            下行数据需要返回码
            ludHexTmp = SSALErrorHexEnum.SUCCESS.getValue() + ludHexTmp;
        }
        linkUserData.setLudHex(ludHexTmp);
        linkUserData.setMainLinkUserDataHex(linkUserData.getLudLengthHexRevrs() + linkUserData.getLudHex());
        return linkUserData.copy();
    }

    public static MainLinkUserData buildFromLudHex(String ludHex, FCCodeBinEnum startupSymbol) {
        return buildFromLudHex(ludHex, startupSymbol, null);
    }

    public static MainLinkUserData buildFromLudHex(String ludHex, FCCodeBinEnum startupSymbol, String backCodeHex) {
        MainLinkUserData linkUserData = new MainLinkUserData() {
            @Override
            public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
                return null;
            }
        };
        if (!StringUtils.isEmpty(ludHex)) {
//            linkUserData.setLudPlain(ByteTools.hexStr2Str(ludHex));
            linkUserData.setLudLengthOct(ludHex.length() / 2);
            linkUserData.setLudLengthHex(StringUtils.leftPad(ByteTools.int2HexStr(linkUserData.getLudLengthOct()), 4, '0'));
            linkUserData.setLudLengthHexRevrs(linkUserData.getLudLengthHex().substring(2, 4) + linkUserData.getLudLengthHex().substring(0, 2));
        }else {
            linkUserData.setLudLengthHex("0000");
            linkUserData.setLudLengthHexRevrs("0000");
        }
        if (!StringUtils.isEmpty(backCodeHex) || FCCodeBinEnum.ZERO.equals(startupSymbol) || !DeviceTypeOctEnum.PSP.equals(SSALConfigInitProperties.deviceType)) {
//            下行数据需要返回码
            String backCode = backCodeHex;
            if (StringUtils.isEmpty(backCodeHex)) backCode = SSALErrorHexEnum.SUCCESS.getValue();
            ludHex = backCode + ludHex;
        }
        linkUserData.setLudHex(ludHex);
        linkUserData.setMainLinkUserDataHex(linkUserData.getLudLengthHexRevrs() + linkUserData.getLudHex());
        return linkUserData.copy();
    }

    public abstract String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener);
}
