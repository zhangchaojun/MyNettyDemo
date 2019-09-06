package com.example.mynettydemo.ssalinstance.entity.bo.lud;

import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//  l) 网关链路心跳报文
//        网关链路心跳报文，是所有与网关建立链接的客户机与网关进行的链路心跳维护。
public class GateLinkHeartbeat extends MainLinkUserData implements Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(GateLinkHeartbeat.class);
    public static final String HEARTBEAT_LUD_HEX = "681900C1C0AA003BEF01000100B407E3030604130908037C7ADA16";
    public String buildInstanceByHexString(String hexString, FCCodeBinEnum startUpSymbol, EncryptListener encryptListener) {
        hexString = this.initBackCodeByHexStringIfNeeded(hexString, startUpSymbol);
        return hexString;
    }

    public GateLinkHeartbeat copy() {
        try {
            return (GateLinkHeartbeat) this.clone();
        }catch (CloneNotSupportedException e) {
            logger.error(e.getMessage());
            return this;
        }
    }
}
