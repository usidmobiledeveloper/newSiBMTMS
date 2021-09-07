package com.usid.mobilebmt.mandirisejahtera.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        playNotificationSound(context);
    }

    public void playNotificationSound(Context context) {
        try {
            Utility.setNotificationStatus(context, "1");
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/notification");
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                r.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}