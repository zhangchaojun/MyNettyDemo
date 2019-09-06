package com.example.mynettydemo.ssalinstance.entity.bo.lud;



import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

import org.apache.commons.lang.StringUtils;

//  /*主站与终端之间需要进行加密传输的业务报文。报文格式符合终端的业务报文协议要求。*/
//  f) 应用数据报文
//  应用报文是网关与终端完成会话密钥协商后，主站与终端之间的原有业务报文经过网关进行加解密处理的报文。
//  请求报文的数据长度字段根据实际情况填写，数据域部分为业务报文的密文值；
// 响应报文的数据长度字段和返回信息字段也是根据实际情况填写，数据域部分为业务报文的密文值。
public class ApplicationData extends MainLinkUserData {

    //    针对业务数据的分包使用，其他数据不用分包，使用默认值
    private int pkgNum = 1;
    private  String pkgNumHex = "0100";
    private int currentPkgNum = 0;
    private String currentPkgNumHex = "0000";

    @Override
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol, encryptListener);
        if (StringUtils.isEmpty(ludHex)) return hexString;
        pkgNumHex = ludHex.substring(0, 4);
        pkgNum = Integer.parseInt(pkgNumHex.substring(2, 4) + pkgNumHex.substring(0, 2), 16);
        currentPkgNumHex = ludHex.substring(4, 8);
        currentPkgNum = Integer.parseInt(currentPkgNumHex.substring(2, 4) + currentPkgNumHex.substring(0, 2), 16);
        ludHex = ludHex.substring(8);
//        ludPlain = ByteTools.hexStr2Str(ludHex);
        return hexString;
    }

    public int getPkgNum() {
        return pkgNum;
    }

    public void setPkgNum(int pkgNum) {
        this.pkgNum = pkgNum;
    }

    public String getPkgNumHex() {
        return pkgNumHex;
    }

    public void setPkgNumHex(String pkgNumHex) {
        this.pkgNumHex = pkgNumHex;
    }

    public int getCurrentPkgNum() {
        return currentPkgNum;
    }

    public void setCurrentPkgNum(int currentPkgNum) {
        this.currentPkgNum = currentPkgNum;
    }

    public String getCurrentPkgNumHex() {
        return currentPkgNumHex;
    }

    public void setCurrentPkgNumHex(String currentPkgNumHex) {
        this.currentPkgNumHex = currentPkgNumHex;
    }
}
