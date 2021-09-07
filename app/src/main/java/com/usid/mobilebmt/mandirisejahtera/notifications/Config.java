package com.usid.mobilebmt.mandirisejahtera.notifications;

/**
 * Created by AHMAD AYIK RIFAI on 3/7/2017.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "mobilebmtmandirisejahtera";
    public static final String KEY_COUNTDOWNTIMER = "countdowntimer";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "usidmobile_firebase";
}