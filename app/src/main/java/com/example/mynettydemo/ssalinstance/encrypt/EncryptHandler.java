package com.example.mynettydemo.ssalinstance.encrypt;


import com.example.mynettydemo.ssalinstance.client.EncryptListener;
import com.example.mynettydemo.ssalinstance.util.EncryptUtils;

/**
 * @author zcj
 * @date 2019/8/3
 */
public class EncryptHandler implements EncryptListener {

    @Override
    public String encrypt(String originalLudHex) {
        return EncryptUtils.encryption(originalLudHex);
    }

    @Override
    public String decrypt(String encryptLudHex) {
        return EncryptUtils.deciphering(encryptLudHex);
    }
}
