package com.kii.cloud.unity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * 
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GCMBroadcastReceiver", "#####onReceive");
		ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
