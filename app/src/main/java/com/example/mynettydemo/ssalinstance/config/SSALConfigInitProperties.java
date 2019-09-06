package com.example.mynettydemo.ssalinstance.config;


import com.example.mynettydemo.ssalinstance.consts.SSALConst;
import com.example.mynettydemo.ssalinstance.enums.DeviceTypeOctEnum;
import com.example.mynettydemo.ssalinstance.enums.EncryptAlgorithmOctEnum;
import com.example.mynettydemo.ssalinstance.enums.PSPAPPProtocolVersionOctEnum;

// SSAL服务启动之前，需要先初始化一些基本信息，供组装SSAL报文时使用
public class SSALConfigInitProperties {
    //    设备类型，当前支持掌机端和后置，后置取值采集主站
//    掌机为主动站，后置为从动站
//    主动站发送的主动帧不需要返回码，从动站发送的响应帧需要有返回码，2字节，0x0000表示成功
    public static DeviceTypeOctEnum deviceType = DeviceTypeOctEnum.PSP;

    public static String ssalVersionBigHex = "1";
    public static String ssalVersionSmallHex = "0";

    public static EncryptAlgorithmOctEnum encryptAlgorithm = EncryptAlgorithmOctEnum.CBC_CIPHERTEXT;
    public static boolean encryptEnabled = false;
    public static PSPAPPProtocolVersionOctEnum pspAppProtocolVerion = PSPAPPProtocolVersionOctEnum.STATUTE_16;


    //    请求真的发给谁，默认发给前置，没有iGate就发给targetIp，就是后置服务
    public static String frontProxyIp;
    public static Integer frontProxyPort = 80;
    //    请求真的发给谁，默认发给iGate，没有iGate就发给targetIp，就是后置服务
    public static String serverIp;
    public static int serverPort;

//    目标ip和端口 TA
    public static String targetIp;
    public static int targetPort = 80;

//    当前ip和端口 SA
    public static String localIp;
    public static int localPort = 80;

//    deviceAddress MAC  DA  [掌机物理地址， 16进制字符串, 推荐用掌机esam号， 一个掌机一个，被网关作为密钥协商与否的标记]
    public static String pspMACAddress = "123456";

//    网关ip和端口  GA
//    【网关ip为空将被视为不存在网关，进入lud数据长度不受6k限制的SSAL不分包模式】
    public static String gateIp;
    public static int gatePort = 80;

    public static int maxFrameLength = SSALConst.MAX_LUD_BYTE_LENGTH;
}
