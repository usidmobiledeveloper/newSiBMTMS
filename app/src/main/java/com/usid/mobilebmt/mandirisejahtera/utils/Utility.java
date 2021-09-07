package com.usid.mobilebmt.mandirisejahtera.utils;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.registrasi.Registrasi2Activity;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Class which has Utility methods
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0 ? true : false;
    }

    public static String StringToCurency(String x) {
        String s = x;
        String r = "";
        for (int i = 1; i < s.length() + 1; ++i) {
            r = s.substring(s.length() - i, s.length() - i + 1) + r;
            if ((i % 3 == 0) && (i != s.length())) {
                r = "." + r;
            }
        }
        return r.replace(".,", ",");// +"," + Float. .substring(2);
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month - 1];
    }

    public static boolean isRekening(String txt) {
        boolean textbool = false;
        String text = txt.trim();
        text = text.replace(".", "");
        if (text.length() < 10) {
            textbool = false;
        } else {
            textbool = true;
        }
        return textbool;
    }

    public static String DoubleToCurrency(long x) {
        String s = Long.toString(x);
        String r = "";
        for (int i = 1; i < s.length() + 1; ++i) {
            r = s.substring(s.length() - i, s.length() - i + 1) + r;
            if ((i % 3 == 0) && (i != s.length())) {
                r = "." + r;
            }
        }
        return r;// +"," + Float. .substring(2);
    }

    public static boolean is6digit(String txt) {
        boolean textbool = false;
        String text = txt.trim();
        if (text.length() == 6) {
            textbool = true;
        } else {
            textbool = false;
        }
        return textbool;
    }

    public static boolean is16digit(String txt) {
        boolean textbool = false;
        String text = txt.trim();
        if (text.length() == 16) {
            textbool = true;
        } else {
            textbool = false;
        }
        return textbool;
    }

    public static String md5(String input) {
        String result = input;
        if (input != null) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                BigInteger hash = new BigInteger(1, md.digest());
                result = hash.toString(16);
//                if ((result.length() % 2) != 0) {
//                    result = "0" + result;
//                }
                while (result.length() < 32) {
                    result = "0" + result;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    public static String namaOperator(String inputs) {

        String input = inputs.substring(0, 4);
        String result = "";

        if (input.equals("0815") || input.equals("0816") || input.equals("0855") || input.equals("0856") || input.equals("0857")
                || input.equals("0858") || input.equals("0814")) {
            result = "INDOSAT";
        } else if (input.equals("0812") || input.equals("0813") || input.equals("0811") || input.equals("0821") || input.equals("0822") || input.equals("0851") || input.equals("0852") || input.equals("0853") || input.equals("0854") || input.equals("0823")) {
            result = "TELKOMSEL";
        } else if (input.equals("0831") || input.equals("0832") || input.equals("0833") || input.equals("0838")) {
            result = "AXIS";
        } else if (input.equals("0817") || input.equals("0818") || input.equals("0819") || input.equals("0877") || input.equals("0878") || input.equals("0879") || input.equals("0859")) {
            result = "XL";
        } else if (input.equals("0894") || input.equals("0895") || input.equals("0896") || input.equals("0897") || input.equals("0898") || input.equals("0899")) {
            result = "TRI";
        } else if (input.equals("0881") || input.equals("0882") || input.equals("0883") || input.equals("0884") || input.equals("0885") || input.equals("0886") || input.equals("0887") || input.equals("0888") || input.equals("0889")) {
            result = "SMARTFREN";
        } else {
            result = "Kosong";
        }
        return result;
    }

    // tambahan ayik==
    public static long timeout = 570 * 1000;
    public static long timeoutPPOB = 540 * 1000;
//    public static long timeout = 50000;
//    public static long timeoutPPOB = 60000;

    public static boolean isNetworkAvailable(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }
        isConnected = false;
        return false;
    }

    public static void playNotificationSound(Context context) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinearLayout addRow(ArrayList<String> z, Context ctx, LinearLayout mKonten) {
        String token = "";
        for (String item : z) {
            String[] subs = item.split(": ");
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.struk_item, null);
            final TextView mKiri = addView.findViewById(R.id.mKiri);
            if (subs[0].startsWith("OUTLET"))
                continue;

            mKiri.setText(subs[0]);
            final TextView mTgh = addView.findViewById(R.id.mTengah);
            final TextView mTitik2 = addView.findViewById(R.id.mTitik2);
            final TextView mRp = addView.findViewById(R.id.mRp);
            final TextView mKanan = addView.findViewById(R.id.mKanan);

            Typeface face = Typeface.createFromAsset(ctx.getAssets(), "cour.ttf");
            mKiri.setTypeface(face, Typeface.BOLD);
            mTgh.setTypeface(face, Typeface.BOLD);
            mTitik2.setTypeface(face, Typeface.BOLD);
            mRp.setTypeface(face, Typeface.BOLD);
            mKanan.setTypeface(face, Typeface.BOLD);

            final ImageButton btnSalin = addView.findViewById(R.id.btn_salin);

            if (subs[0].startsWith("TOKEN")) {
                btnSalin.setVisibility(View.VISIBLE);
            } else {
                btnSalin.setVisibility(View.INVISIBLE);
            }

            token = z.get(0).replace(" ", "");
            token = token.replace("TOKEN:", "");

            StringBuilder s;
            s = new StringBuilder(token);

            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            token = s.toString();

            final String finalToken = token;

            btnSalin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager manager = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Salin token", finalToken);
                    assert manager != null;
                    manager.setPrimaryClip(clipData);

                    Toast.makeText(ctx, "Salin token", Toast.LENGTH_SHORT).show();
                }
            });

            try {
                if (subs[1].contains("Rp")) {
                    mTitik2.setVisibility(View.GONE);
                    mKanan.setVisibility(View.GONE);
                    mTgh.setText(": Rp");
                    if (z.get(0).equals(item)) {
                        //mRp.setTypeface(face, Typeface.BOLD);
                        mRp.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        //mKiri.setTypeface(face, Typeface.BOLD);
                        mKiri.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        //mTgh.setTypeface(face, Typeface.BOLD);
                        mTgh.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                    }
                    mRp.setText(subs[1].replace(" ", "").replace("Rp", ""));
                    mRp.setGravity(Gravity.RIGHT);
                } else {
                    mRp.setVisibility(View.GONE);
                    mTgh.setVisibility(View.GONE);
                    mKanan.setText(subs[1]);
                    mKanan.setGravity(Gravity.LEFT);
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            LayoutTransition transition = new LayoutTransition();
            mKonten.setLayoutTransition(transition);
            mKonten.addView(addView, 0);
        }
        return mKonten;
    }

    public static String toJsonString(String nokartu, String imsi, String pin, String kodeproduk, String idpel, long nominal, String trxID, String uid, String pid, String noresi, String idpelppob, int versi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        if (!pin.equals("0")) obj.put("pin", pin);
        obj.put("kodeproduk", kodeproduk);
        if (!idpel.equals("0")) obj.put("idpel", idpelppob);
        if (nominal != 0) obj.put("nominal", nominal);
        if (!trxID.equals("0")) obj.put("idtrx", trxID);
        if (!uid.equals("0")) obj.put("uid", uid);
        if (!pid.equals("0")) obj.put("pid", pid);
        if (!kodeproduk.equals("0")) obj.put("produk", kodeproduk);
        if (!idpelppob.equals("0")) obj.put("idpelnama", idpel);
        if (!noresi.equals("0")) obj.put("noresi", noresi);
        if (versi != 0) obj.put("versi", versi);
        return obj.toString();
    }

    public static String toJsonStringPPOB(String uid, String pid, String produk, String idpel, String idtrx) {
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("pid", pid);
        obj.put("produk", produk);
        obj.put("idpel", idpel);
        obj.put("idtrx", idtrx);
        return obj.toString();
    }

    public static String toJsonStringPDAMpayment(String nokartu, String imsi, String pin, String kodeproduk, String idpel, long nominal, String trxID, String uid, String pid, String noresi, String idpelppob, String idpelppob2, String idpelppob3) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        if (!pin.equals("0")) obj.put("pin", pin);
        obj.put("kodeproduk", kodeproduk);
        if (!idpel.equals("0")) obj.put("idpel", idpelppob);
        if (!idpel.equals("0")) obj.put("idpel2", idpelppob2);
        if (!idpel.equals("0")) obj.put("idpel3", idpelppob3);
        if (nominal != 0) obj.put("nominal", nominal);
        if (!trxID.equals("0")) obj.put("idtrx", trxID);
        if (!uid.equals("0")) obj.put("uid", uid);
        if (!pid.equals("0")) obj.put("pid", pid);
        if (!kodeproduk.equals("0")) obj.put("produk", kodeproduk);
        if (!idpelppob.equals("0")) obj.put("idpelnama", idpel);
        if (!noresi.equals("0")) obj.put("noresi", noresi);
        return obj.toString();
    }

    public static String toJsonStringPDAM(String uid, String pid, String produk, String idpel, String idpel2, String idpel3, String idtrx) {
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("pid", pid);
        obj.put("produk", produk);
        obj.put("idpel", idpel);
        obj.put("idpel2", idpel2);
        obj.put("idpel3", idpel3);
        obj.put("idtrx", idtrx);
        return obj.toString();
    }

    public static String leftRight(String paramString1, String paramString2, int paramInt) {
        int i = paramString1.length();
        int j = paramString2.length();
        int k = i + j;
        if (k <= paramInt) {
            String str2 = "" + paramString1;
            int n = paramInt - k;
            for (int i1 = 0; i1 < n; i1++)
                str2 = str2 + " ";
            return str2 + paramString2;
        }
        String str1 = "" + paramString1 + "\n";
        for (int m = 0; m < paramInt - j; m++)
            str1 = str1 + " ";
        return str1 + paramString2;
    }

    //ayik baru
    public static void setNotificationStatus(Context ctx, String status) {
        SharedPreferences pref = ctx.getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("statusnotification", status);
        editor.commit();
    }

    public static String toJsonStringBPJS(String nokartu, String imsi, String pin, String kodeproduk, String idpel, long nominal, String trxID, String uid, String pid, String noresi, String idpelppob, int versi, String bulan, String notelp) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        if (!pin.equals("0")) obj.put("pin", pin);
        obj.put("kodeproduk", kodeproduk);
        if (!idpel.equals("0")) obj.put("idpel", idpelppob);
        if (nominal != 0) obj.put("nominal", nominal);
        if (!trxID.equals("0")) obj.put("idtrx", trxID);
        if (!uid.equals("0")) obj.put("uid", uid);
        if (!pid.equals("0")) obj.put("pid", pid);
        if (!kodeproduk.equals("0")) obj.put("produk", kodeproduk);
        if (!idpelppob.equals("0")) obj.put("idpelnama", idpel);
        if (!noresi.equals("0")) obj.put("noresi", noresi);
        if (versi != 0) obj.put("versi", versi);

        if (kodeproduk.equals("BPJSKES")) {
            obj.put("jml_bulan", bulan);
            obj.put("notelp", notelp);
        }

        return obj.toString();
    }

   /* public static String toJsonStringEToll(String nokartu, String imsi, String pin, String kodeproduk, String idpel, long nominal, String trxID, String uid, String pid, String noresi, String idpelppob, int versi, String bulan, String notelp) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        if (!pin.equals("0")) obj.put("pin", pin);
        obj.put("kodeproduk", kodeproduk);
        if (!idpel.equals("0")) obj.put("idpel", idpelppob);
        if (nominal != 0) obj.put("nominal", nominal);
        if (!trxID.equals("0")) obj.put("idtrx", trxID);
        if (!uid.equals("0")) obj.put("uid", uid);
        if (!pid.equals("0")) obj.put("pid", pid);
        if (!kodeproduk.equals("0")) obj.put("produk", kodeproduk);
        if (!idpelppob.equals("0")) obj.put("idpelnama", idpel);
        if (!noresi.equals("0")) obj.put("noresi", noresi);
        if (versi != 0) obj.put("versi", versi);

        if (kodeproduk.equals("BPJSKES")) {
            obj.put("jml_bulan", bulan);
            obj.put("notelp", notelp);
        }

        return obj.toString();
    }*/

    public static String toJsonStringPPOBBPJS(String uid, String pid, String produk, String idpel, String idtrx, String bulan, String notelp) {
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("pid", pid);
        obj.put("produk", produk);
        obj.put("idpel", idpel);
        obj.put("idtrx", idtrx);

        if (produk.equals("BPJSKES")) {
            obj.put("jml_bulan", bulan);
            obj.put("notelp", notelp);
        }


        return obj.toString();
    }

    public static String toJsonStringPPOBEToll(String uid, String pid, String produk, String idpel, String idtrx, String bulan, String notelp) {
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("pid", pid);
        obj.put("produk", produk);
        obj.put("idpel", idpel);
        obj.put("idtrx", idtrx);

        if (produk.equals("BPJSKES")) {
            obj.put("jml_bulan", bulan);
            obj.put("notelp", notelp);
        }

        return obj.toString();
    }

    public static String toJsonStringEToll(String nokartu, String imsi, String pin, String kodeproduk, String idpel, long nominal, String trxID, String uid, String pid, String noresi, String idpelppob, int versi, String bulan, String notelp) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        if (!pin.equals("0")) obj.put("pin", pin);
        obj.put("kodeproduk", kodeproduk);
        if (!idpel.equals("0")) obj.put("idpel", idpelppob);
        if (nominal != 0) obj.put("nominal", nominal);
        if (!trxID.equals("0")) obj.put("idtrx", trxID);
        if (!uid.equals("0")) obj.put("uid", uid);
        if (!pid.equals("0")) obj.put("pid", pid);
        if (!kodeproduk.equals("0")) obj.put("produk", kodeproduk);
        if (!idpelppob.equals("0")) obj.put("idpelnama", idpel);
        if (!noresi.equals("0")) obj.put("noresi", noresi);
        if (versi != 0) obj.put("versi", versi);

        if (kodeproduk.equals("BPJSKES")) {
            obj.put("jml_bulan", bulan);
            obj.put("notelp", notelp);
        }

        return obj.toString();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMSIRegister(Context context) {

        String strIMSI = "";

        //if (isGenymotionEmulator(Build.MANUFACTURER) || buildModelContainsEmulatorHints(Build.MODEL)) {
        if (isEmulator()) {
            strIMSI = "EMULATOR";
        } else {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                assert subsManager != null;
                List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

                if (subsList != null) {

                    ArrayList<String> listIMSI = new ArrayList<String>();

                    for (SubscriptionInfo subsInfo : subsList) {
                        if (subsInfo != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                strIMSI = String.valueOf(subsInfo.getSubscriptionId());
                            } else {
                                strIMSI = subsInfo.getIccId();
                            }

                            if (strIMSI.equals("") || strIMSI == null)
                                strIMSI = androidID + "sim" + String.valueOf(subsInfo.getSubscriptionId());

                            if (strIMSI != null)
                                if (!strIMSI.equals(""))
                                    listIMSI.add(strIMSI);
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (listIMSI.size() == 1) {
                            strIMSI = androidID + "sim" + listIMSI.get(0);

                        } else if (listIMSI.size() == 2) {
                            strIMSI = androidID + "sim" + listIMSI.get(0) + listIMSI.get(1);

                        } else {
                            strIMSI = "TIDAK ADA KARTU";
                        }
                    } else {
                        if (listIMSI.size() > 0) {
                            strIMSI = String.valueOf(listIMSI.get(0));
                        } else {
                            strIMSI = "TIDAK ADA KARTU";
                        }
                    }

                } else {
                    strIMSI = "TIDAK ADA KARTU";
                }
            } else {
                TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                assert tMgr != null;
                strIMSI = tMgr.getSimSerialNumber();
            }

        }

        return strIMSI;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMSIRead(Context context) {
        String strIMSIRead = "";

        //if (isGenymotionEmulator(Build.MANUFACTURER) || buildModelContainsEmulatorHints(Build.MODEL)) {
        if (isEmulator()) {
            strIMSIRead = "NO KARTU SALAH";
        } else {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            SharedPreferences config = context.getSharedPreferences("config", 0);
            String strIMSIPref = config.getString("METAREG", "");
            try {
                strIMSIPref = new NumSky(context).decrypt(strIMSIPref);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                assert subsManager != null;
                List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

                if (subsList != null) {
                    ArrayList<String> listIMSI = new ArrayList<String>();

                    for (SubscriptionInfo subsInfo : subsList) {
                        if (subsInfo != null) {
                            String imsi = "";

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                imsi = String.valueOf(subsInfo.getSubscriptionId());
                            } else {
                                imsi = subsInfo.getIccId();
                            }

                            if (imsi.equals("") || imsi == null)
                                imsi = androidID + "sim" + String.valueOf(subsInfo.getSubscriptionId());

                            if (imsi != null)
                                if (!imsi.equals(""))
                                    listIMSI.add(imsi);
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        if (listIMSI.size() == 1) {
                            if (String.valueOf(androidID + "sim" + listIMSI.get(0)).equals(strIMSIPref)) {
                                strIMSIRead = String.valueOf(androidID + "sim" + listIMSI.get(0));
                            } else {
                                strIMSIRead = "NO KARTU SALAH";
                            }
                        } else if (listIMSI.size() == 2) {
                            if (String.valueOf(androidID + "sim" + listIMSI.get(0) + listIMSI.get(1)).equals(strIMSIPref)) {
                                strIMSIRead = String.valueOf(androidID + "sim" + listIMSI.get(0) + listIMSI.get(1));

                            } else {
                                strIMSIRead = "NO KARTU SALAH";
                            }
                        } else {
                            strIMSIRead = "NO KARTU SALAH";
                        }
                    } else {
                        if (listIMSI.size() > 0) {
                            if (String.valueOf(listIMSI.get(0)).equals(strIMSIPref)) {
                                strIMSIRead = String.valueOf(listIMSI.get(0));
                            } else if (listIMSI.size() > 1) {
                                if (String.valueOf(listIMSI.get(1)).equals(strIMSIPref)) {
                                    strIMSIRead = String.valueOf(listIMSI.get(1));
                                } else {
                                    strIMSIRead = "NO KARTU SALAH";
                                }
                            } else {
                                strIMSIRead = "NO KARTU SALAH";
                            }
                        } else {
                            strIMSIRead = "NO KARTU SALAH";
                        }
                    }

                } else {
                    strIMSIRead = "NO KARTU SALAH";
                }
            } else {
                TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                assert tMgr != null;
                strIMSIRead = tMgr.getSimSerialNumber();
            }
        }

        return strIMSIRead;
    }

    private static boolean isEmulator() {

        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }


    public static void enableViews(View... views) {
        if (views != null)
            for (View v : views) {
                v.setVisibility(View.VISIBLE);
            }
    }

    public static void disableViews(View... views) {
        if (views != null)
            for (View v : views) {
                v.setVisibility(View.GONE);
            }
    }

    public static LinearLayout addRowTrf(ArrayList<String> z, Context ctx, LinearLayout mKonten) {
        String token = "";
        for (String item : z) {
            String[] subs = item.split("#");
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.struk_item_transfer, null);
            final TextView mKiri = addView.findViewById(R.id.mKiri);
            if (subs[0].startsWith("OUTLET"))
                continue;

            mKiri.setText(subs[0]);
            final TextView mTgh = addView.findViewById(R.id.mTengah);
            final TextView mTitik2 = addView.findViewById(R.id.mTitik2);
            final TextView mRp = addView.findViewById(R.id.mRp);
            final TextView mKanan = addView.findViewById(R.id.mKanan);
            final TextView mSpace = addView.findViewById(R.id.mSpace);

            Typeface face = Typeface.createFromAsset(ctx.getAssets(), "cour.ttf");
            mKiri.setTypeface(face, Typeface.BOLD);
            mTgh.setTypeface(face, Typeface.BOLD);
            mTitik2.setTypeface(face, Typeface.BOLD);
            mRp.setTypeface(face, Typeface.BOLD);
            mKanan.setTypeface(face, Typeface.BOLD);


            final ImageButton btnSalin = addView.findViewById(R.id.btn_salin);

            if (subs[0].startsWith("TOKEN")) {
                btnSalin.setVisibility(View.VISIBLE);
            } else {
                btnSalin.setVisibility(View.INVISIBLE);
            }

            token = z.get(0).replace(" ", "");
            token = token.replace("TOKEN:", "");

            StringBuilder s;
            s = new StringBuilder(token);

            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            token = s.toString();

            final String finalToken = token;

            btnSalin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager manager = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Salin token", finalToken);
                    assert manager != null;
                    manager.setPrimaryClip(clipData);

                    Toast.makeText(ctx, "Salin token", Toast.LENGTH_SHORT).show();
                }
            });

            try {
                if (subs[1].contains("Rp")) {
                    mRp.setVisibility(View.VISIBLE);
                    mKanan.setVisibility(View.GONE);
                    mTgh.setVisibility(View.VISIBLE);
                    mSpace.setVisibility(View.INVISIBLE);

                    mRp.setText(subs[1].replace("Rp ", ""));
                    mRp.setGravity(Gravity.RIGHT);

                    if (subs[0].equalsIgnoreCase("JUMLAH")) {
                        mKiri.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mTitik2.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mTgh.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mRp.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                    }

                } else {
                    mRp.setVisibility(View.GONE);
                    mKanan.setVisibility(View.VISIBLE);
                    mTgh.setVisibility(View.GONE);
                    mSpace.setVisibility(View.GONE);

                    mKanan.setText(subs[1]);
                    mKanan.setGravity(Gravity.LEFT);
                }


            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            LayoutTransition transition = new LayoutTransition();
            mKonten.setLayoutTransition(transition);
            mKonten.addView(addView, 0);
        }
        return mKonten;
    }

    public static LinearLayout addRowPembelian(ArrayList<String> z, Context ctx, LinearLayout mKonten) {
        String token = "";
        for (String item : z) {
            String[] subs = item.split("#");
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.struk_item_pembelian, null);
            final TextView mKiri = addView.findViewById(R.id.mKiri);
            if (subs[0].startsWith("OUTLET"))
                continue;

            mKiri.setText(subs[0]);
            final TextView mTgh = addView.findViewById(R.id.mTengah);
            final TextView mTitik2 = addView.findViewById(R.id.mTitik2);
            final TextView mRp = addView.findViewById(R.id.mRp);
            final TextView mKanan = addView.findViewById(R.id.mKanan);

            Typeface face = Typeface.createFromAsset(ctx.getAssets(), "cour.ttf");
            mKiri.setTypeface(face, Typeface.BOLD);
            mTgh.setTypeface(face, Typeface.BOLD);
            mTitik2.setTypeface(face, Typeface.BOLD);
            mRp.setTypeface(face, Typeface.BOLD);
            mKanan.setTypeface(face, Typeface.BOLD);

            final ImageButton btnSalin = addView.findViewById(R.id.btn_salin);

            if (subs[0].startsWith("TOKEN")) {
                btnSalin.setVisibility(View.VISIBLE);
            } else {
                btnSalin.setVisibility(View.INVISIBLE);
            }

            token = z.get(0).replace(" ", "");
            token = token.replace("TOKEN:", "");

            StringBuilder s;
            s = new StringBuilder(token);

            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            token = s.toString();

            final String finalToken = token;

            btnSalin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager manager = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Salin token", finalToken);
                    assert manager != null;
                    manager.setPrimaryClip(clipData);

                    Toast.makeText(ctx, "Salin token", Toast.LENGTH_SHORT).show();
                }
            });

            try {
                if (subs[1].contains("Rp")) {
                    mRp.setVisibility(View.VISIBLE);
                    mKanan.setVisibility(View.GONE);
                    mTgh.setVisibility(View.VISIBLE);

                    mRp.setText(subs[1].replace("Rp ", ""));
                    mRp.setGravity(Gravity.RIGHT);

                    if (subs[0].equalsIgnoreCase("JUMLAH")) {
                        mKiri.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mTitik2.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mTgh.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        mRp.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                    }

                } else {
                    mRp.setVisibility(View.GONE);
                    mKanan.setVisibility(View.VISIBLE);
                    mTgh.setVisibility(View.GONE);

                    mKanan.setText(subs[1]);
                    mKanan.setGravity(Gravity.LEFT);
                }


            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            LayoutTransition transition = new LayoutTransition();
            mKonten.setLayoutTransition(transition);
            mKonten.addView(addView, 0);
        }
        return mKonten;
    }

}
