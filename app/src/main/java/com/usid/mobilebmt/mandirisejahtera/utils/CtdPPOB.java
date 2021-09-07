package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.usid.mobilebmt.mandirisejahtera.notifications.Config;

/**
 * Created by AHMAD AYIK RIFAI on 7/27/2017.
 */

public class CtdPPOB {

    Context mContext;

    public CtdPPOB(Context mCtx) {
        this.mContext = mCtx;
    }

    public CountDownTimer countDPPOB = new CountDownTimer(Utility.timeoutPPOB, 1000) {

        public void onTick(long millisUntilFinished) {
            //TODO: Do something every second
        }

        public void onFinish() {
            Intent pushNotification = new Intent(Config.KEY_COUNTDOWNTIMER);
            pushNotification.putExtra("countdowntimer", "0");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
//            ((Activity) mContext).finish();
        }
    }.start();

}
