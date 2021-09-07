package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;

public class AboutTentang extends AppCompatActivity {
    private PackageInfo pInfo;
    private TextView tvVer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_tentang);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tvVer = (TextView) findViewById(R.id.tvVersi);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                tvVer.setText(getResources().getString(R.string.app_name) + "\nVersi : "
                        + Integer.toString(pInfo.versionCode).substring(0, 1)
                        + "."
                        + Integer.toString(pInfo.versionCode).substring(1));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
