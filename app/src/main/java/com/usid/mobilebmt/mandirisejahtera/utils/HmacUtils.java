package com.usid.mobilebmt.mandirisejahtera.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {

    String generateHmac256(String message, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] bytes = hmac("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes);
    }

    byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}