package com.example.mynettydemo.ssalinstance.entity.impl;

import android.util.Log;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.config.SSALConfigInitProperties;
import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.entity.SSALParser;
import com.example.mynettydemo.ssalinstance.entity.bo.CommunicateInfo;
import com.example.mynettydemo.ssalinstance.entity.bo.ControlData;
import com.example.mynettydemo.ssalinstance.entity.bo.DeviceAddress;
import com.example.mynettydemo.ssalinstance.entity.bo.DeviceAddressType;
import com.example.mynettydemo.ssalinstance.entity.bo.FunctionCode;
import com.example.mynettydemo.ssalinstance.entity.bo.GateAddress;
import com.example.mynettydemo.ssalinstance.entity.bo.SSALTimestamp;
import com.example.mynettydemo.ssalinstance.entity.bo.SSALVersion;
import com.example.mynettydemo.ssalinstance.entity.bo.SourceAddress;
import com.example.mynettydemo.ssalinstance.entity.bo.TargetAddress;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;
import com.example.mynettydemo.ssalinstance.enums.UserContentTypeOctEnum;
import com.example.mynettydemo.ssalinstance.util.ByteTools;
import com.example.mynettydemo.ssalinstance.util.CRCUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class DefaultSSALParser implements SSALParser {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSSALParser.class);
    private static SSALParser parser;
    private static final String TAG = "cj3";

    private DefaultSSALParser() {
    }

    public static SSALParser instance() {
        if (parser == null) parser = new DefaultSSALParser();
        return parser;
    }

    /**
     * [Decode使用]
     * 把SSAL格式[HEXString]的网络报文解析成一份普通的易操作的SSALMessage
     *
     * @param hexString
     */
    public SSALMessage parse(String hexString, EncryptListener encryptListener) throws StringIndexOutOfBoundsException {
        long startTime = System.currentTimeMillis();
        if (StringUtils.isEmpty(hexString)) return null;
        int hexLength = hexString.length();
        logger.error("hexStr.length: {}, content: {}",  hexLength, hexString);

        SSALMessage ssalMessage = new SSALMessage();
        if(!(SSALConst.SSAL_HEAD_HEX.equals(hexString.substring(0,2))) || !SSALConst.SSAL_TAIL_HEX.equals(hexString.substring(hexLength - 2, hexLength))) return null;
//        6.1 长度域L
//        帧数据长度，由2字节组成，表示传输帧中除起始字符、长度域和结束字符之外的帧字节数
//        16进制帧头部紧跟的4位，即[index]2～5位,
//        * !!!!!!!!!!!!!!! 这个长度要用的话，需要2个字节做反序才可以转化成对的十进制数 !!!!!!!!!!!!!!!!!!!!!!!!!
        ssalMessage.setFullFrameHex(hexString);
        String frameLengthHex = hexString.substring(2, 6);
        ssalMessage.setFrameLengthHex(frameLengthHex);

//        6.2 帧序号SEQ
//        帧序号SEQ，由2个字节组成，有请求帧的发起方维护，网关转发不处理，响应方在响应帧中与请求帧保持一致。当请求帧由网关主动发起时，该字段填写为全0xFF。
//        16进制帧长度位后紧跟的4位，即6～9位
        String frameSequenceHex = hexString.substring(6, 10);
        ssalMessage.setFrameSequenceHex(frameSequenceHex);
//        Log.e("cj4", "parser 时的SEQ== "+frameSequenceHex );

//        6.3 控制码C
//        控制码C，标识帧结构中的特定字段是否存在，由2字节组成。该字段中的每个bit位代表一个特定字段，当bit位置1时，该字段存在，否则该字段不存在。
//        16进制帧序号位后紧跟的4位，即10～13位
        String controlCodeHex = hexString.substring(10, 14);
        ssalMessage.setControlCodeHex(controlCodeHex);

        byte [] controlCodeByteArr = ByteTools.hexStr2ByteArr(controlCodeHex.substring(2,4) + controlCodeHex.substring(0,2));
        //byte数组转换为二进制字符串,每个字节以","隔开
        String controlCodeByteArrStr = ByteTools.byteArr2BinStr(controlCodeByteArr);
        String [] controlCodeBinArr = controlCodeByteArrStr.split(",");
        //控制码C由2字节组成
        String controlCodeBit1Str = controlCodeBinArr[0];
        boolean hasFunctionCode = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(0)));
        boolean hasSSALProtocolVersion = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(1)));
        boolean hasDeviceAddressType = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(2)));
        boolean hasDeviceAddress = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(3)));
        boolean hasSourceAddress = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(4)));
        boolean hasTargetAddress = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(5)));
        boolean hasCommunicateInfo = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(6)));
        boolean hasSSALTimestamp = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit1Str.charAt(7)));

        String controlCodeBit2Str = controlCodeBinArr[1];
        boolean hasGateAddress = FCCodeBinEnum.ONE.getValue().equals(String.valueOf(controlCodeBit2Str.charAt(0)));

        ControlData controlData = new ControlData();
        controlData.setControlCodeHex(controlCodeHex);
        ssalMessage.setControlData(controlData);
        if(hasFunctionCode) {
//            6.4.1 功能码FC
//            功能码FC，标识传输帧的类型及关键参数，由1个字节组成
            String functionCodeHex = hexString.substring(14,16);
            hexString = hexString.substring(16);
            FunctionCode functionCode = new FunctionCode(functionCodeHex);
            controlData.setFunctionCode(functionCode);
        }
        if(hasSSALProtocolVersion) {
//            6.4.2 SSAL协议版本SV
//            SSAL协议版本SV，标识SSAL协议的版本信息并包含协议中采用的密码算法信息，由2字节组成。
            String ssalVersionHex = hexString.substring(0, 4);
            hexString = hexString.substring(4);
            SSALVersion ssalVersion = new SSALVersion(ssalVersionHex);
            controlData.setSsalVersion(ssalVersion);
        }
        if(hasDeviceAddressType) {
//            6.4.3 设备地址类型DAT
            String deviceAddressTypeHexRevrs = hexString.substring(0,4);
            hexString = hexString.substring(4);
            DeviceAddressType deviceAddressType = new DeviceAddressType(deviceAddressTypeHexRevrs);
            controlData.setDeviceAddressType(deviceAddressType);
        }
        if(hasDeviceAddress) {
//            6.4.4 设备地址DA
            DeviceAddress deviceAddress = new DeviceAddress();
            controlData.setDeviceAddress(deviceAddress);
            hexString = deviceAddress.buildInstanceByHexString(hexString, controlData.getDeviceAddressType().getDeviceType());
        }
        if(hasSourceAddress) {
//            6.4.5 源地址SA
            SourceAddress sourceAddress = new SourceAddress();
            controlData.setSourceAddress(sourceAddress);
            hexString = sourceAddress.buildInstanceByHexString(hexString);
        }
        if(hasTargetAddress) {
//           6.4.6 目的地址TA
            TargetAddress targetAddress = new TargetAddress();
            controlData.setTargetAddress(targetAddress);
            hexString = targetAddress.buidInstanceByHexString(hexString);
        }
        if(hasCommunicateInfo) {
//            6.4.7 通信信息CI
            CommunicateInfo communicateInfo = new CommunicateInfo();
            hexString = communicateInfo.buildInstanceByHexString(hexString);
            controlData.setCommunicateInfo(communicateInfo);
        }
        if(hasSSALTimestamp) {
//            6.4.8 时间标签TP
//            时间标签TP，标识报文的产生时间，由报文生成者填写，由7个字节组成。
            String timestampHex = hexString.substring(0, 14);
            hexString = hexString.substring(14);
            SSALTimestamp ssalTimestamp = new SSALTimestamp(timestampHex);
            controlData.setSsalTimestamp(ssalTimestamp);
        }
        if(hasGateAddress) {
//            /*6.4.9 网关地址GA
//           网关地址GA，标识传输帧的安全网关地址，包括网关地址长度和网关地址内容两部分。
//           其中，网关地址长度为1字节，网关地址内容为变长，实际长度由网关地址长度决定。
            GateAddress gateAddress = new GateAddress();
            controlData.setGateAddress(gateAddress);
            hexString = gateAddress.buildInstanceByHexString(hexString);
        }

//        6.5 帧头校验HCS
//        帧头校验HCS，标识整个传输帧从起始字符后的首个字段到该字段之前全部字段的校验值，由2个字节组成。
//        校验算法参见附录A。
        String frameHeadCheckSeedHex = hexString.substring(0,4);
        String data2CalHcs = ssalMessage.getFrameLengthHex()
                    + ssalMessage.getFrameSequenceHex()
                    + ssalMessage.getControlCodeHex()
                    + ssalMessage.getControlData().getControlDataHexForHcsCheck();
        if(!CRCUtil.calculationHcs(data2CalHcs).equals(frameHeadCheckSeedHex)) {
            //返回 hcs 错误。
            logger.trace("hcs check failed!");
            ssalMessage.setFrameHeadCheckPass(false);
            return ssalMessage;
        }
        ssalMessage.setFrameHeadCheckPass(true);
        ssalMessage.setFrameHeadCheckSeedHex(frameHeadCheckSeedHex);
        hexString = hexString.substring(4);

//        6.6 链路用户数据LUD
//        链路用户数据LUD，定义了主站与终端之间、终端与网关之间的报文的具体内容。
        hexString = ssalMessage.buildLinkDataByHexString(hexString, encryptListener);

//        6.7 帧校验FCS
//         帧校验FCS，标识整个传输帧从起始字符后的首个字段到该字段之前全部字段的校验值，由2个字节组成。校验算法参见附录A。
        String fcs = hexString.substring(0, 4);
        String data2CalFcs = ssalMessage.getFullFrameHex().substring(2, ssalMessage.getFullFrameHex().length() - 6);
        if(!CRCUtil.calculationFcs(data2CalFcs).equals(fcs)) {
            //返回 fcs 错误。
            logger.trace("fcs check failed!");
            ssalMessage.setFrameCheckPass(false);
            return ssalMessage;
        }
        ssalMessage.setFrameCheckPass(true);
        ssalMessage.setFrameCheckSeedHex(fcs);
        ssalMessage.setDecodeResult(true);
        long endTime = System.currentTimeMillis();
        logger.warn("parse success: startTime[millis] = {}, endTime[millis] = {}", startTime, endTime);
        logger.trace("ssalMessage: {}", ssalMessage);
        return ssalMessage;
    }

    /**
     * [配合Decode使用]
     * 把SSALMessage中的用户数据取出，转回为普通网络数据
     *
     * @param ssalMessage
     */
    public ByteBuf removeSSAL(SSALMessage ssalMessage) {
        return Unpooled.wrappedBuffer(ByteTools.hexStr2ByteArr(ssalMessage.getLinkUserData().getLudHex()));
    }

    /**
     * [配合Decode使用]
     * 把SSALMessage对象转化为SSAL格式的网络数据
     *
     * @param ssalMessageList
     */
    @Override
    public ByteBuf removeSSAL(List<SSALMessage> ssalMessageList) {
        StringBuilder fullLudHexBuilder = new StringBuilder();
        for (SSALMessage msg : ssalMessageList) {
            fullLudHexBuilder.append(msg.getLinkUserData().getLudHex());
        }
        return Unpooled.wrappedBuffer(ByteTools.hexStr2ByteArr(fullLudHexBuilder.toString()));
    }

    /**
     * [Encode使用]
     * 把普通（http/ws/mqtt/...等基于tcp协议传输的）报文转化为一个易操作的SSALMessage集合
     *  @param plainByteBuf
     * @param targetIp
     * @param targetPort
     * @param da
     * @param businessId
     * @param userContentType
     * @param startupSymbol
     * @param encryptListener
     */
    @Override
    public List<SSALMessage> parseReverse(ByteBuf plainByteBuf, String targetIp, int targetPort, String da, String businessId, UserContentTypeOctEnum userContentType, FCCodeBinEnum startupSymbol, EncryptListener encryptListener) {
        SSALMessage ssalMessage = SSALMessage.giveMeOne(targetIp, targetPort, da, businessId, userContentType);
        int readableBytes = plainByteBuf.readableBytes();
        byte[] bytes = new byte[readableBytes];
        plainByteBuf.readBytes(bytes);
        String ludHex = ByteTools.byteArr2HexStr(bytes);

        int ludBytesLimit = SSALConfigInitProperties.maxFrameLength;
        logger.trace("ludBytesLimit: {}", ludBytesLimit);
        String[] ludHexes = ByteTools.splitStrForSpecificLength(ludHex, ludBytesLimit * 2 - 8);
        List<SSALMessage> ssalMessageList = new ArrayList<>();
        int pkgNum = ludHexes.length;
        String pkgNumHexTmp = StringUtils.leftPad(ByteTools.int2HexStr(pkgNum), 4, '0');
        String pkgNumHex = pkgNumHexTmp.substring(2, 4) + pkgNumHexTmp.substring(0, 2);
        for (int i = 0; i < pkgNum; i++) {
            long startTime = System.currentTimeMillis();
            String ludHex0 = ludHexes[i];
            String curPkgNumHexTmp = StringUtils.leftPad(ByteTools.int2HexStr(i), 4, '0');
            String curPkgNumHex = curPkgNumHexTmp.substring(2, 4) + curPkgNumHexTmp.substring(0, 2);
            ludHex0 = pkgNumHex + curPkgNumHex + ludHex0;
            if (SSALConfigInitProperties.encryptEnabled) {
                logger.error("encrypt is Enabled.");
                ludHex0 = encryptListener.encrypt(ludHex0);
            }
            logger.error("ludHex0[{}]: {}", ludHex0.length(), ludHex0);
            SSALMessage copy = ssalMessage.copy();
            copy.loadLudHex(ludHex0, startupSymbol);
            copy.initCRC();
            long endTime = System.currentTimeMillis();
            logger.warn("assembleSSAL success: startTime[millis] = {}, endTime[millis] = {}", startTime, endTime);
            ssalMessageList.add(copy);
        }
        return ssalMessageList;
    }

    /**
     * [配合Encode使用]
     * 把SSALMessage对象转化为SSAL格式的网络数据
     *
     * @param ssalMessage
     */
    public ByteBuf loadSSAL(SSALMessage ssalMessage) {
        return Unpooled.wrappedBuffer(ByteTools.hexStr2ByteArr(ssalMessage.getFullFrameHex()));
    }
}
