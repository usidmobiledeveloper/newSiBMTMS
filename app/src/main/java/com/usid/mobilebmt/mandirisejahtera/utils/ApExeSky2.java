package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Encoder;

/**
 * Created by AHMAD AYIK RIFAI on 7/26/2017.
 */

public class ApExeSky2 {
    Context mContext;
    public ApExeSky2(Context mContext){
        this.mContext = mContext;
    }

    public String encrypt(String property) throws GeneralSecurityException,
            UnsupportedEncodingException {
        String key = MyVal.appsCode3();
        key = String.format("%-16.16s", key);
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher;
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(property.getBytes());
        return base64Encode(encrypted);
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }
}
