package com.example.mynettydemo.ssalinstance.entity.impl;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.entity.SSALContent;
import com.example.mynettydemo.ssalinstance.entity.bo.ControlData;
import com.example.mynettydemo.ssalinstance.entity.bo.MainLinkUserData;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.ApplicationData;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.ChangeFailureThreshold;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.GateLinkHeartbeat;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.GateQuery;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.LinkManageData;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.ObtainPspBasicInfo;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.PspGateCertUpdate;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.PspLinkKeygenUpdate;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.PspSessionNegotiation;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.PspSessionPrompt;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.QueryGateLinkStatus;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.RetainData;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.SecureAccessAreaDeviceKeyNegotiationTrigger;
import com.example.mynettydemo.ssalinstance.entity.bo.lud.SessionNegotiationComplete;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;
import com.example.mynettydemo.ssalinstance.util.CRCUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SSALMessage implements SSALContent, Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(SSALMessage.class);
    private static Map<String, SSALMessage> instanceStore = new HashMap<>();

    //    完整帧的hex字符串
    private String fullFrameHex;

    //    十六进制的1位对应2进制的4位，所以，1个字节对应的8位(bit)就对应成2位16进制数
//    起始字符（98H）, 固定值
//    16进制帧头部的2位
    private static final String START = SSALConst.SSAL_HEAD_HEX;

    //    6.1 长度域L
//    帧数据长度，由2字节组成，表示传输帧中除起始字符、长度域和结束字符之外的帧字节数
//    16进制帧头部紧跟的4位，即[index]2～5位
    private String frameLengthHex;

    //    6.2 帧序号SEQ
//    帧序号SEQ，由2个字节组成，有请求帧的发起方维护，网关转发不处理，响应方在响应帧中与请求帧保持一致。当请求帧由网关主动发起时，该字段填写为全0xFF。
//    16进制帧长度位后紧跟的4位，即6～9位
//    【hzwq】帧序号在业务报文中对网关没有用处，我们自己把它作为业务号使用
//      每个掌机发送的上行业务数据分包用，后置程序返回下行数据会带回
//      注：业务号只是个序号而已，没有大小的意义，所以业务号并没有作反序
    private String frameSequenceHex;

    //    6.3 控制码C
//    控制码C，标识帧结构中的特定字段是否存在，由2字节组成。该字段中的每个bit位代表一个特定字段，当bit位置1时，该字段存在，否则该字段不存在。
//    16进制帧序号位后紧跟的4位，即10～13位
    private String controlCodeHex;

    //    6.4 控制数据CD
    private ControlData controlData;

    //    6.5 帧头校验HCS
//    帧头校验HCS，标识整个传输帧从起始字符后的首个字段到该字段之前全部字段的校验值，由2个字节组成。
//    校验算法参见附录A。
    private String frameHeadCheckSeedHex;
    private boolean frameHeadCheckPass;

    //    6.6 链路用户数据LUD
//    链路用户数据LUD，定义了主站与终端之间、终端与网关之间的报文的具体内容。
//    链路用户数据包含一个完整的应用层协议数据单元（APDU）字节序列或APDU的分帧片段。
//    由多个字节组成，长度可变。
    private MainLinkUserData linkUserData;

    //    帧校验FCS
    private String frameCheckSeedHex;
    private boolean frameCheckPass;

    //    结束字符（16H）, 固定值
//    16进制帧尾部的2位
    private static final String END = SSALConst.SSAL_TAIL_HEX;


    private boolean decodeResult = false;

    //    用系统参数创建一个对象并返回
    private void initBySSALConfigProperties(String targetIp, int targetPort, UserContentTypeOctEnum userContentType) {
        this.setFrameSequenceHex(SSALConst.FRAME_SEQUENCE_HEX);
        this.setControlData(ControlData.giveMeOne(targetIp, targetPort, userContentType));
        this.setControlCodeHex(this.getControlData().getControlCodeHex());
    }

    //    用系统参数创建一个对象并返回
    private void initBySSALConfigProperties(String targetIp, int targetPort, String administrativeOrLogicalAddressHex, UserContentTypeOctEnum userContentType) {
        this.setFrameSequenceHex(SSALConst.FRAME_SEQUENCE_HEX);
        this.setControlData(ControlData.giveMeOne(targetIp, targetPort, administrativeOrLogicalAddressHex, userContentType));
        this.setControlCodeHex(this.getControlData().getControlCodeHex());
    }

    // 给网关发送协商信息时使用
    public static SSALMessage giveMeOne(String targetIp, int targetPort, UserContentTypeOctEnum userContentType) {
        String key = targetIp + ":" + targetPort + ":" + userContentType.getValue();
        SSALMessage instanceDown;
        if (!instanceStore.containsKey(key)) {
            instanceDown = new SSALMessage();
            instanceDown.initBySSALConfigProperties(targetIp, targetPort, userContentType);
            instanceStore.put(key, instanceDown);
        } else {
            instanceDown = instanceStore.get(key);
        }
        SSALMessage copy = instanceDown.copy();
        copy.getControlData().refreshTimestamp();
        return copy;
    }

    // 发送上行或下行业务数据时使用
    public static SSALMessage giveMeOne(String targetIp, int targetPort, String administrativeOrLogicalAddressHex, String businessId, UserContentTypeOctEnum userContentType) {
        String key = targetIp + ":" + targetPort + ":" + administrativeOrLogicalAddressHex + ":" + userContentType.getValue();
        SSALMessage instanceDown;
        if (!instanceStore.containsKey(key)) {
            instanceDown = new SSALMessage();
            instanceDown.initBySSALConfigProperties(targetIp, targetPort, administrativeOrLogicalAddressHex, userContentType);
            instanceStore.put(key, instanceDown);
        } else {
            instanceDown = instanceStore.get(key);
        }
        SSALMessage copy = instanceDown.copy();
        copy.setFrameSequenceHex(businessId);
        copy.getControlData().refreshTimestamp();
        return copy;
    }

    public static String getSTART() {
        return START;
    }

    public String getFullFrameHex() {
        return fullFrameHex;
    }

    public void setFullFrameHex(String fullFrameHex) {
        this.fullFrameHex = fullFrameHex;
    }

    public String getFrameLengthHex() {
        return frameLengthHex;
    }

    public void setFrameLengthHex(String frameLengthHex) {
        this.frameLengthHex = frameLengthHex;
    }

    public String getFrameSequenceHex() {
        return frameSequenceHex;
    }

    public void setFrameSequenceHex(String frameSequenceHex) {
        this.frameSequenceHex = frameSequenceHex;
    }

    public String getControlCodeHex() {
        return controlCodeHex;
    }

    public void setControlCodeHex(String controlCodeHex) {
        this.controlCodeHex = controlCodeHex;
    }

    public ControlData getControlData() {
        return controlData;
    }

    public void setControlData(ControlData controlData) {
        this.controlData = controlData;
    }

    public String getFrameHeadCheckSeedHex() {
        return frameHeadCheckSeedHex;
    }

    public void setFrameHeadCheckSeedHex(String frameHeadCheckSeedHex) {
        this.frameHeadCheckSeedHex = frameHeadCheckSeedHex;
    }

    public MainLinkUserData getLinkUserData() {
        return linkUserData;
    }

    public void setLinkUserData(MainLinkUserData linkUserData) {
        this.linkUserData = linkUserData;
    }

    public String getFrameCheckSeedHex() {
        return frameCheckSeedHex;
    }

    public void setFrameCheckSeedHex(String frameCheckSeedHex) {
        this.frameCheckSeedHex = frameCheckSeedHex;
    }

    public static String getEND() {
        return END;
    }

    public boolean isDecodeResult() {
        return decodeResult;
    }

    public void setDecodeResult(boolean decodeResult) {
        this.decodeResult = decodeResult;
    }

    public boolean isFrameHeadCheckPass() {
        return frameHeadCheckPass;
    }

    public void setFrameHeadCheckPass(boolean frameHeadCheckPass) {
        this.frameHeadCheckPass = frameHeadCheckPass;
    }

    public boolean isFrameCheckPass() {
        return frameCheckPass;
    }

    public void setFrameCheckPass(boolean frameCheckPass) {
        this.frameCheckPass = frameCheckPass;
    }

    /**
     * SSAL报文的实现，必须能够在解码完成后，可以直接拿到解码成功与否的标志，true表示成功，false表示失败
     */
    public boolean decodeResult() {
        return this.decodeResult;
    }

    @Override
    public String toString() {
        return "SSALMessage{" +
                "fullFrameHex='" + fullFrameHex + '\'' + "\r\n" +
                ", frameLengthHex='" + frameLengthHex + '\'' + "\r\n" +
                ", frameSequenceHex='" + frameSequenceHex + '\'' + "\r\n" +
                ", controlCodeHex='" + controlCodeHex + '\'' + "\r\n" +
                ", controlData='" + controlData + '\'' + "\r\n" +
                ", frameHeadCheckSeedHex='" + frameHeadCheckSeedHex + '\'' + "\r\n" +
                ", frameHeadCheckPass='" + frameHeadCheckPass + '\'' + "\r\n" +
                ", linkUserData='" + linkUserData + '\'' + "\r\n" +
                ", frameCheckSeedHex='" + frameCheckSeedHex + '\'' + "\r\n" +
                ", frameCheckPass='" + frameCheckPass + '\'' + "\r\n" +
                ", decodeResult='" + decodeResult + '\'' + "\r\n" +
                '}';
    }

    public SSALMessage copy() {
        try {
            return (SSALMessage) this.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(e.getMessage());
            return this;
        }
    }

    //        6.6 链路用户数据LUD
//        链路用户数据LUD，定义了主站与终端之间、终端与网关之间的报文的具体内容。
    public String buildLinkDataByHexString(String hexString, EncryptListener encryptListener) {
        switch (controlData.getFunctionCode().getUserContentTypeOct()) {
            case APPLICATION_DATA:
                ApplicationData applicationData = new ApplicationData();
                this.setLinkUserData(applicationData);
                hexString = applicationData.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case LINK_MANAGE:
                LinkManageData linkManageData = new LinkManageData();
                this.setLinkUserData(linkManageData);
                hexString = linkManageData.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case OBTAIN_PSP_BASIC_INFO:
                ObtainPspBasicInfo obtainPspBasicInfo = new ObtainPspBasicInfo();
                this.setLinkUserData(obtainPspBasicInfo);
                hexString = obtainPspBasicInfo.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case PSP_SESSION_NEGOTIATION:
                PspSessionNegotiation pspSessionNegotiation = new PspSessionNegotiation();
                this.setLinkUserData(pspSessionNegotiation);
                hexString = pspSessionNegotiation.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case CHANGE_FAILURE_THRESHOLD:
                ChangeFailureThreshold changeFailureThreshold = new ChangeFailureThreshold();
                this.setLinkUserData(changeFailureThreshold);
                hexString = changeFailureThreshold.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case PSP_LINK_KEYGEN_UPDATE:
                PspLinkKeygenUpdate pspLinkKeygenUpdate = new PspLinkKeygenUpdate();
                this.setLinkUserData(pspLinkKeygenUpdate);
                hexString = pspLinkKeygenUpdate.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case PSP_GATE_CERT_UPDATE:
                PspGateCertUpdate pspGateCertUpdate = new PspGateCertUpdate();
                this.setLinkUserData(pspGateCertUpdate);
                hexString = pspGateCertUpdate.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case QUERY_GATE_LINK_STATUS:
                QueryGateLinkStatus queryGateLinkStatus = new QueryGateLinkStatus();
                this.setLinkUserData(queryGateLinkStatus);
                hexString = queryGateLinkStatus.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case SECURE_ACCESS_AREA_DEVICE_KEY_NEGOTIATION_TRIGGER:
                SecureAccessAreaDeviceKeyNegotiationTrigger secureAccessAreaDeviceKeyNegotiationTrigger = new SecureAccessAreaDeviceKeyNegotiationTrigger();
                this.setLinkUserData(secureAccessAreaDeviceKeyNegotiationTrigger);
                hexString = secureAccessAreaDeviceKeyNegotiationTrigger.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case GATE_LINK_HEARTBEAT:
                GateLinkHeartbeat gateLinkHeartbeat = new GateLinkHeartbeat();
                this.setLinkUserData(gateLinkHeartbeat);
                hexString = gateLinkHeartbeat.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case PSP_SESSION_NEGOTIATION_PROMPT:
                PspSessionPrompt pspSessionPrompt = new PspSessionPrompt();
                this.setLinkUserData(pspSessionPrompt);
                hexString = pspSessionPrompt.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case SESSION_NEGOTIATION_COMPLETE:
                SessionNegotiationComplete sessionNegotiationComplete = new SessionNegotiationComplete();
                this.setLinkUserData(sessionNegotiationComplete);
                hexString = sessionNegotiationComplete.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            case GATE_QUERY:
                GateQuery gateQuery = new GateQuery();
                this.setLinkUserData(gateQuery);
                hexString = gateQuery.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
                break;
            default:
                // retain data
                RetainData retainData = new RetainData();
                this.setLinkUserData(retainData);
                hexString = retainData.buildInstanceByHexString(hexString, controlData.getFunctionCode().getStartUpSymbol(), encryptListener);
        }

        hexString = hexString.substring(this.getLinkUserData().getLudLengthOct() * 2);

        return hexString;
    }

    //    密钥协商或处理错误时使用
//    装载用户数据【通过ludHex的方式】
    public void loadLudHex(String ludHex, FCCodeBinEnum startupSymbol) {
        this.setLinkUserData(MainLinkUserData.buildFromLudHex(ludHex, startupSymbol));
    }

    //    密钥协商或处理错误时使用
    public void loadLudHex(String ludHex, FCCodeBinEnum startupSymbol, String errorCode) {
        this.setLinkUserData(MainLinkUserData.buildFromLudHex(ludHex, startupSymbol, errorCode));
    }


    //    装载链路用户数据后，初始化帧头全帧校验值
    public void initCRC() {
        int frameLengthOct = (this.getFrameSequenceHex().length()                                                            // 帧序号
                + this.getControlCodeHex().length()                                                                // 控制码
                + this.getControlData().getControlDataHex().length()                                    // 控制数据
                + 4                                                                                                                        // hcs
                + this.getLinkUserData().getMainLinkUserDataHex().length()                        // 用户数据
                + 4) / 2;                                                                                                                // fcs
        logger.trace("frameLengthOct: {}", frameLengthOct);
        String frameLengthHex = StringUtils.leftPad(ByteTools.int2HexStr(frameLengthOct), 4, '0');
        logger.trace("frameLengthHex: {}", frameLengthHex);
        String frameLengthHexRevrs = frameLengthHex.substring(2, 4) + frameLengthHex.substring(0, 2);
        this.setFrameLengthHex(frameLengthHexRevrs);
        String frameHead = (this.getFrameLengthHex()
                + this.getFrameSequenceHex()
                + this.getControlCodeHex()
                + this.getControlData().getControlDataHex()).toUpperCase();
        String hcs = CRCUtil.calculationHcs(frameHead);
        this.setFrameHeadCheckSeedHex(hcs);
        String frameBody = frameHead + hcs + this.getLinkUserData().getMainLinkUserDataHex();
        String fcs = CRCUtil.calculationFcs(frameBody);
        this.setFrameCheckSeedHex(fcs);
        String fullFrameHex = START + frameBody + fcs + END;
        logger.trace("fullFrameHex: {}", fullFrameHex);
        this.setFullFrameHex(fullFrameHex);
    }
}
