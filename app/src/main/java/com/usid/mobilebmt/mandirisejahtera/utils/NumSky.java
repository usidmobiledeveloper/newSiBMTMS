package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import Decoder.BASE64Encoder;
import Decoder.BASE64Decoder;


public class NumSky {
    Context mContext;
    public NumSky(Context mContext){
        this.mContext = mContext;
    }
//    private static final char[] PASSWORD = toCharArray();
    private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10,
            (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };

    public String encrypt(String property) throws GeneralSecurityException,
            UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(MyVal.appsCode().toCharArray()));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher
                .init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }

    public String decrypt(String property)
            throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(MyVal.appsCode().toCharArray()));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher
                .init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }
}
