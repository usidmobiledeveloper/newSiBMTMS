package com.usid.mobilebmt.mandirisejahtera.base;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {
    private static BaseApp mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }
    public static synchronized BaseApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}