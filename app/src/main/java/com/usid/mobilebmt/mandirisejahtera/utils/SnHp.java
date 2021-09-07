package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SnHp {
	Context mContext;
	public SnHp(Context mContext){
	       this.mContext = mContext;
	}
	public TelephonyManager telephonyManager() {
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager;
	}
}
