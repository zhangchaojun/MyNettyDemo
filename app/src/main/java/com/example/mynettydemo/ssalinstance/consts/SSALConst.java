package com.example.mynettydemo.ssalinstance.consts;

import com.example.mynettydemo.ssalinstance.util.ByteTools;

import io.netty.util.AttributeKey;

public interface SSALConst {
    String SSAL_HEAD_HEX = "98";
    String SSAL_TAIL_HEX = "16";
    byte[] SSAL_FRAME_HEAD_BYTES = ByteTools.hexStr2ByteArr(SSAL_HEAD_HEX);
    byte[] SSAL_FRAME_TAIL_BYTES = ByteTools.hexStr2ByteArr(SSAL_TAIL_HEX);
    String FRAME_SEQUENCE_HEX = "0100";
    //    6k	网关限制，每个SSAL包lud字段长度不可超过6k数据
    int MAX_LUD_BYTE_LENGTH = 1000 * 4;
    //    60k 没有网关情况下，lud字段长度位是2个字节，最大可表示的数字是65535，协议帧的长度也是2字节，为了避免帧长度最终超标，lud数据长度限制为60k
    int MAX_LUD_BYTE_LENGTH_NO_GATE = 1000 * 60;
    AttributeKey<String> PROTOCOL_TAG_KEY = AttributeKey.newInstance("BIZ_PROTOCOL");
}
