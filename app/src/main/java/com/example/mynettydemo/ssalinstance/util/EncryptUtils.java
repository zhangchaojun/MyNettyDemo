package com.example.mynettydemo.ssalinstance.util;


import android.util.Log;


import com.example.mynettydemo.ssalinstance.encrypt.EncryptProtocol;

import org.apache.commons.lang.StringUtils;



/**
 * @author zcj
 * @date 2019/8/3
 */
public class EncryptUtils {

    //秘钥 key
    private static final String SECRETKEY = "11111111111111111111111111111111";
    //秘钥 iv 值
    private static final String IV = "00000000000000000000000000000000";


    /**
     * 加密
     */
    public static synchronized String encryption(String data) {
        long start = System.currentTimeMillis();

        //c语言SM4 加密
        byte[] key = ByteTools.hexStr2ByteArr(SECRETKEY);
        byte[] iv = ByteTools.hexStr2ByteArr(IV);
        //过一遍加密协议
        String dataHex = EncryptProtocol.checkEncrypt(ByteTools.hexStr2ByteArr(data));
        byte[] input = ByteTools.hexStr2ByteArr(dataHex);
        int inputLen = input.length;
        byte[] output = new byte[inputLen];
//        int code = SM4Util.hzSm4Cbc(key, 0, iv, input, inputLen, output);
//        if (SM4Util.SUCCESS_CODE == code) {
//            long end = System.currentTimeMillis();
//            Log.e(TIMELOG, "本次加密耗时==" + (end - start));
//            return ByteTools.byteArr2HexStr(output);
//        }
        return "";
    }

    /**
     * 解密
     */
    public static synchronized String deciphering(String data) {

        long start = System.currentTimeMillis();
        //c语言SM4解密
        byte[] key = ByteTools.hexStr2ByteArr(SECRETKEY);
        byte[] iv = ByteTools.hexStr2ByteArr(IV);
        byte[] input = ByteTools.hexStr2ByteArr(data);
        int inputLen = input.length;
        byte[] output = new byte[inputLen];

//        int code = SM4Util.hzSm4Cbc(key, 1, iv, input, inputLen, output);
//        if (SM4Util.SUCCESS_CODE == code) {
//            String plainText = ByteTools.byteArr2HexStr(output);
//            //当数据为空时，直接返回数据
//            if (StringUtils.isEmpty(plainText)) return "";
//            //过一遍加密协议
//            String hexData = EncryptProtocol.checkDecrypt(ByteTools.hexStr2ByteArr(plainText));
//            long end = System.currentTimeMillis();
//            Log.e(TIMELOG, "本次解密耗时==" + (end - start));
//            return hexData;
//        }
        return "";
    }

    public static void main(String[] args) {
        String encryption = EncryptUtils.encryption("1234");
        System.out.println(encryption);
    }

}
