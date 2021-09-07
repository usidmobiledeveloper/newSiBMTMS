package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;
import android.util.Log;

import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.base.BaseApp;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MyVal {

    public static String PREFS_NAME = "mobilebmt-prefs";
    public static String KEY_TOKEN = "token";
    public static String MYTAG = "AYIK";

    static {
        System.loadLibrary("native-lib");
    }

    public static native String stringJNI();

    public static native String appsCode();

    public static native String appsCode2();

    public static native String appsCode3();

    public static native String salt1();

    public static native String salt2();

    public static native String URI_LOGIN();

    public static native String URI_RELOGIN();

    public static native String URL_BASE_ENC();

    public static native String URL_BASE_CONTENT_ENC();

    public static native String URL_BASE_PPOB_ENC();

    public static native String username();

    public static native String pass();

    public static String password = pass() + BaseApp.getAppContext().getResources().getString(R.string.kodebmt);

    public static String strHmac(String valueToDigest) {
        String result = "";
        try {
            byte[] key = stringJNI().getBytes();
            HmacUtils hm = new HmacUtils();
            String messageDigest = null;
            messageDigest = hm.generateHmac256(valueToDigest, key);
            result = messageDigest;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String URL_BASE() {
        String url = "";
        try {
            url = new NumSky(BaseApp.getAppContext()).decrypt(URL_BASE_ENC());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String URL_BASE_CONTENT() {
        String url = "";
        url ="https://img.usid.co.id/";
        /*try {
            //http://103.101.194.34:3388/mobileugt/

            //url = new NumSky(BaseApp.getAppContext()).decrypt(URL_BASE_CONTENT_ENC());
            //Log.d("AYIK", "base content "+ url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return url;
    }

    public static String URL_BASE_PPOB() {
        String url = "";
        try {
            url = new NumSky(BaseApp.getAppContext()).decrypt(URL_BASE_PPOB_ENC());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }

    /*public static String Urlbase45131(Context context) {
        String url = null;
        *//*try {
            url = new NumSky(context).decrypt(BuildConfig.BASE_45131);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*//*

        url = "https://api.usid.co.id/bmtservice3/";
        return url;
    }

    public static String Urlbase3388(Context context) {
        String url = null;
        try {
            url = new NumSky(context).decrypt(BuildConfig.BASE_3388);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String Urlbasesgpay(Context context) {
        String url = null;
        try {
            url = new NumSky(context).decrypt(BuildConfig.BASE_SGPAY);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }*/

}
