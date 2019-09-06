package com.example.mynettydemo.ssalinstance.encrypt;


import com.example.mynettydemo.ssalinstance.util.ByteTools;

import org.apache.commons.lang.StringUtils;

/**
 * LUD加密协议
 * 1.1	CBC加密算法
 * 使用SM4-CBC算法对数据加密。
 * LEN 	为明文长度，2字节，大端格式
 * M 		为明文
 * LRC 	为LEN+M的数据的每个字节异或，然后将最后的一个字节的异或值取反
 * EM = SM4-ENC-CBC(K, IV, DATA) = SM4-ENC-CBC(K, IV, (LEN+M+LRC)
 * 注意，数据加密的填充规则为：当LEN+M+LRC的长度不为16的倍数时，用0x80 0x00 0x00…补位；
 * 否则，当LEN+M+LRC的长度刚好为16的倍数时，不用再补0x80 0x00 0x00…。
 * <p>
 * <p>
 * 1.2	CBC解密算法
 * 使用SM4-CBC算法对数据解密。
 * LEN 	为明文长度，2字节，大端格式
 * M 		为明文
 * LRC 	为LEN+M的异或值取反
 * M = SM4-DEC-CBC(K, IV, DATA) = SM4-DEC-CBC(K, IV, (LEN+M+LRC)
 * 注意：解密后的数据格式去填充规则参考上节。
 *
 * @author zcj
 * @date 2019/8/3
 */
public class EncryptProtocol {


    /**
     * 拼装加密协议,LUD在加密之前需要先适配协议
     *
     * @param bytes lud数据
     * @return
     */
    public static String checkEncrypt(byte[] bytes) {
        //长度 字节数组
        byte[] cd = checkLeng(bytes);//追加长度，获得LEN+M
        byte b = (byte) (Lrc(cd) & 0xFF);//计算LRC
        byte[] hexbyte = ByteTools.byteMerger(cd, new byte[]{b});//(LEN+M+LRC)
        byte[] data = cover(hexbyte);//补位够16的倍数
        return ByteTools.byteArr2HexStr(data);
    }

    /**
     * 解密完成后去协议
     *
     * @param bytes
     * @return
     */
    public static String checkDecrypt(byte[] bytes) {
        String data = ByteTools.byteArr2HexStr(bytes);
        String hexLen = data.substring(0, 4);
        data = data.substring(4);
        int dataLen = Integer.parseInt(hexLen, 16) * 2;
        String sjy = data.substring(0, dataLen);
        data = data.substring(dataLen);
        String lrc2 = data.substring(0, 2);
        byte[] bbb = ByteTools.int2ByteArr(Lrc(ByteTools.hexStr2ByteArr(hexLen + sjy)));
        String str = ByteTools.byteArr2HexStr(new byte[]{bbb[3]});
        if (str.equals(lrc2)) {
            return sjy;
        }
        return "";
    }


    /**
     * 检测字节数据长度
     *
     * @param bytes
     * @return
     */
    public static byte[] checkLeng(byte[] bytes) {
        String hexLen = ByteTools.int2HexStr(bytes.length);//得出字节长度，转成十六进制字符串
        String hex = StringUtils.leftPad(hexLen, 4, '0');//格式化字符串为4，即两个字节
        return ByteTools.byteMerger(ByteTools.hexStr2ByteArr(hex), bytes);
    }

    /**
     * lrc 数据验证
     *
     * @param bytes bytes
     * @return int
     */
    public static int Lrc(byte[] bytes) {
        int buff = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            buff = buff ^ (int) bytes[i];

        }
        return ~buff;
    }


    /**
     * 数据补位
     *
     * @param bytes
     * @return
     */
    public static byte[] cover(byte[] bytes) {
        int ys = (16 - bytes.length % 16);//计算需要补的字节数，不会超过16
        if (ys != 16) {
            byte[] bys = new byte[ys];
            for (int i = 0; i < bys.length; i++) {
                bys[i] = 0x00;
            }
            bys[0] = (byte) 0x80;
            bytes = ByteTools.byteMerger(bytes, bys);
        }
        return bytes;
    }


}
