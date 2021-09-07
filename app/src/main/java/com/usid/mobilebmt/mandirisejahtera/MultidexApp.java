package com.usid.mobilebmt.mandirisejahtera;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.usid.mobilebmt.mandirisejahtera.base.BaseApp;

public class MultidexApp extends BaseApp {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
