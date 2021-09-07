package com.usid.mobilebmt.mandirisejahtera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationsInfoActivity;
import com.usid.mobilebmt.mandirisejahtera.registrasi.PreRegistrasiActivity;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ControllerActivity extends Activity {
    private SharedPreferences config;
    private String imsi = "", cd = "";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @SuppressLint("HardwareIds") String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("AYIK~", "id " + id);
        if(id.equals("41d1634e089640c9")){
            startActivity(new Intent(ControllerActivity.this, InfoActivity.class));
            finish();
            return;
        }

        config = getSharedPreferences("config", 0);
        NumSky nmsk = new NumSky(ControllerActivity.this);
        SnHp telpMan = new SnHp(ControllerActivity.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
            imsi = Utility.getIMSIRead(this);
        }
        try {
            cd = nmsk.encrypt(imsi);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent nextIntent = null;
        String metareg = config.getString("METAREG", "");
        if ((metareg.equals("")) || config.getString("3D0k", "").equals(""))
            nextIntent = new Intent(getApplicationContext(), PreRegistrasiActivity.class);
        else {
            if (!metareg.equals("") && !config.getString("3D0k", "").equals("")) {
                if (!cd.equals(metareg)) {
                    nextIntent = new Intent(getApplicationContext(), PreRegistrasiActivity.class);
                } else {
                    //ayik baru
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String statusNotification = pref.getString("statusnotification", "0");
                    if (statusNotification.equals("1")) {
                        nextIntent = new Intent(getApplicationContext(), NotificationsInfoActivity.class);
                        nextIntent.putExtra("from", "background");
                    } else {
                        nextIntent = new Intent(getApplicationContext(), InfoActivity.class);
                    }

                }
            } else {
                nextIntent = new Intent(getApplicationContext(), PreRegistrasiActivity.class);
            }
        }
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }
}
