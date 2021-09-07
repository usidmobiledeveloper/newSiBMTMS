package com.usid.mobilebmt.mandirisejahtera.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;

import com.scottyab.rootbeer.RootBeer;
import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.ControllerActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
import com.usid.mobilebmt.mandirisejahtera.base.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.WIFI_SERVICE;

public class Utility2 {
    public static String createSignature(String payload, String key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] signatureByte = sha256_HMAC.doFinal(payload.getBytes("UTF-8"));
        return android.util.Base64.encodeToString(signatureByte, 16);
    }

    public static String createPayload(String path, String verb, String token, String timestamp, String body) {
        String payload = "path=" + path + "&verb=" + verb + "&token=" + token + "&timestamp=" + timestamp
                + "&body=" + body;
        return payload;
    }

    public static String responseMessage(int code, String msgError) {

        String message;
        String key = "responseDesc";

        switch (code) {
            case 502:
                message = "Bad Gateway, " + trimMessage(msgError, key);
                break;
            case 405:
                message = "Method Not Allowed, " + trimMessage(msgError, key);
                break;
            case 400:
                message = "Bad Request, " + trimMessage(msgError, key);
                break;
            case 408:
                message = "Request Timeout, " + trimMessage(msgError, key);
                break;
            case 409:
                message = "Conflict, " + trimMessage(msgError, key);
                break;
            case 413:
                message = "Request Entity Too Large, " + trimMessage(msgError, key);
                break;
            case 504:
                message = "Gateway Timeout, " + trimMessage(msgError, key);
                break;
            case 403:
                message = "Forbidden, " + trimMessage(msgError, key);
                break;
            case 410:
                message = "Gone, " + trimMessage(msgError, key);
                break;
            case 500:
                message = "Internal Server Error, " + trimMessage(msgError, key);
                break;
            case 411:
                message = "Length Required, " + trimMessage(msgError, key);
                break;
            case 301:
                message = "Moved Permanently, " + trimMessage(msgError, key);
                break;
            case 302:
                message = "Temporary Redirect, " + trimMessage(msgError, key);
                break;
            case 300:
                message = "Multiple Choices, " + trimMessage(msgError, key);
                break;
            case 406:
                message = "Not Acceptable, " + trimMessage(msgError, key);
                break;
            case 404:
                message = "Not Found, " + trimMessage(msgError, key);
                break;
            case 501:
                message = "Not Implemented, " + trimMessage(msgError, key);
                break;
            case 304:
                message = "Not Modified, " + trimMessage(msgError, key);
                break;
            case 402:
                message = "Payment Required, " + trimMessage(msgError, key);
                break;
            case 412:
                message = "Precondition Failed, " + trimMessage(msgError, key);
                break;
            case 407:
                message = "Proxy Authentication Required, " + trimMessage(msgError, key);
                break;
            case 414:
                message = "Request-URI Too Large, " + trimMessage(msgError, key);
                break;
            case 401:
                message = "Unauthorized, " + trimMessage(msgError, key);
                break;
            case 503:
                message = "Service Unavailable, " + trimMessage(msgError, key);
                break;
            case 415:
                message = "Unsupported Media Type, " + trimMessage(msgError, key);
                break;
            case 305:
                message = "Use Proxy, " + trimMessage(msgError, key);
                break;
            case 505:
                message = "HTTP Version Not Supported, " + trimMessage(msgError, key);
                break;
            case 303:
                message = "See Other, " + trimMessage(msgError, key);
                break;
            case 205:
                message = "Reset Content, " + trimMessage(msgError, key);
                break;
            case 203:
                message = "Non-Authoritative Information, " + trimMessage(msgError, key);
                break;
            default:
                message = "Unknow";
                break;
        }

        return message;
    }

    public static String trimMessage(String json, String key) {
        String trimmedString;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "Internal server error";
        }

        return trimmedString;
    }

    public static void setPrefsAuthToken(String token) {
        SharedPreferences sharedPref = BaseApp.getAppContext().getSharedPreferences(MyVal.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MyVal.KEY_TOKEN, token);
        editor.apply();

    }

    public static String getPrefsAuthToken() {
        SharedPreferences sharedPref = BaseApp.getAppContext().getSharedPreferences(MyVal.PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPref.getString(MyVal.KEY_TOKEN, "");

        return token;

    }

    public static List<LinkedHashMap<String, String>> additionalObject(Context context) {
        List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String, String> test = new LinkedHashMap<String, String>();

        String strManufacture = Build.MANUFACTURER;
        String strBrand = Build.BRAND;
        String strModel = Build.MODEL;
        String strHardware = Build.HARDWARE;
        String strDevice = Build.DEVICE;
        String strProduct = Build.PRODUCT;
        String strAndroidversion = androidVersion(Build.VERSION.SDK_INT);
        String strOs = Build.VERSION.INCREMENTAL;
        String strRooted = String.valueOf(isRootBeer(context) && isRootBinary(context));

        String strAppId = BuildConfig.APPLICATION_ID;
        String strVername = BuildConfig.VERSION_NAME;
        String strVercode = String.valueOf(BuildConfig.VERSION_CODE);
        String strBuildtype = BuildConfig.BUILD_TYPE;

      /*  test.put("kodeaktivasi", kdaktivasi);
        test.put("nokartu", nokartu);
        test.put("imsi", imsi);
        test.put("kodebmt", kodebmt);*/

        test.put("manufacture", strManufacture);
        test.put("brand", strBrand);
        test.put("model", strModel);
        test.put("hardware", strHardware);
        test.put("device", strDevice);
        test.put("product", strProduct);
        test.put("androidversion", strAndroidversion);
        test.put("os", strOs);
        test.put("rooted", strRooted);
        //test.put("location", location);
        test.put("appid", strAppId);
        test.put("vername", strVername);
        test.put("vercode", strVercode);
        test.put("buildtype", strBuildtype);

        list.add(test);

        return list;
    }

    public static boolean isRootBeer(Context context) {
        RootBeer rootBeer = new RootBeer(context);
        return rootBeer.isRooted();
    }

    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;

                    break;
                }
            }
        }
        return found;
    }

    public static boolean isRootBinary(Context context) {
        return findBinary("su");
    }


    public static String getMobileIPAddress(Context context) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

    public static boolean verifyInstallerId(Context context) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    public static String substringLatLng(double lat, double lng) {
        String strLat, strLng;
        if (String.valueOf(lat).length() <= 14) {
            strLat = String.valueOf(lat);
        } else {
            strLat = String.valueOf(lat).substring(0, 14);
        }

        if (String.valueOf(lng).length() <= 14) {
            strLng = String.valueOf(lng);
        } else {
            strLng = String.valueOf(lng).substring(0, 14);
        }

        return strLat + "," + strLng;

    }

    public static String androidVersion(int version) {

        String ver = "0.0";
        switch (version) {
            case 30:
                ver = "11.0";
                break;
            case 29:
                ver = "10.0";
                break;
            case 28:
                ver = "9.0";
                break;
            case 27:
                ver = "8.1";
                break;
            case 26:
                ver = "8.0";
                break;
            case 25:
                ver = "7.1.1";
                break;
            case 24:
                ver = "7.0";
                break;
            case 23:
                ver = "6.0";
                break;
            case 22:
                ver = "5.1";
                break;
            case 21:
                ver = "5.0";
                break;
            case 20:
                ver = "4.4W";
                break;
            case 19:
                ver = "4.4";
                break;
            case 18:
                ver = "4.3";
                break;
            case 17:
                ver = "4.2";
                break;
            case 16:
                ver = "4.1";
                break;
            default:
                ver = "0.0";
                break;
        }

        return ver;
    }

    public static void showAlertRelogin(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sesi Berakhir");
        builder.setMessage("Sesi Anda telah berakhir, Silahkan klik \"OK\" untuk memuat ulang Aplikasi");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.finish();

            }
        });

        /*builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });*/

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public static void showAlertRelogin(Activity context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.finish();

            }
        });

        /*builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });*/

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static void reAktivasi(Context context) {
        SharedPreferences config;
        config = context.getSharedPreferences("config", 0);
        SharedPreferences.Editor editor = config.edit();
        editor.putString("METAREG", "");
        editor.putString("3D0k", "");
        editor.putString("NAMA", "");
        editor.putString("NOHP", "");
        editor.apply();

        context.startActivity(new Intent(context, SplashActivity.class));
    }

    public static boolean errorImsi(String message) {
        boolean isError = false;

        if (message.contains("102 Kode Registrasi tidak valid, silahkan hubungi call center kami!")) {
            isError = true;
        } else if (message.contains("107 Status aplikasi DIBLOKIR!")) {
            isError = true;
        } else if (message.contains("108 Status aplikasi BELUM AKTIF!")) {
            isError = true;
        } else if (message.contains("103 SIM Card yang digunakan tidak sesuai. Silahkan gunakan SIM Card saat aktivasi atau lakukan verifikasi ulang dengan cara reinstall aplikasi!")) {
            isError = true;
        } else if (message.contains("106 PIN salah 3 kali, aplikasi terblokir. Silahkan hubungi call center kami!")) {
            isError = true;
        }
        return isError;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getISO8601() {

        Date datenow = new Date();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        return isoFormat.format(datenow);
    }

}
