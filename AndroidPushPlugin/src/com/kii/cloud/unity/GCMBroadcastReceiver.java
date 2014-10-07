package com.kii.cloud.unity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Implementation of BroadcastReceiver to handle a push notification.
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GCMBroadcastReceiver", "#####onReceive");
		ComponentName comp = getIntentService(context);
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
	private ComponentName getIntentService(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
			if (info.services != null) {
				try {
					for (ServiceInfo service : info.services) {
						Class<?> serviceClass = Class.forName(service.name);
						if (AbstractGcmIntentService.class.isAssignableFrom(serviceClass)) {
							Log.d("GCMBroadcastReceiver", "found the IntentService. package=" + service.packageName + " class=" + service.name);
							return new ComponentName(service.packageName, service.name);
						}
					}
				} catch (Exception ignore) {
				}
			}
		} catch (Exception ignore) {
		}
		Log.w("GCMBroadcastReceiver", "cannot find the IntentService in AndroidManifest.xml try to use defalut. package=" + context.getPackageName() + " class=" + GcmIntentService.class.getName());
		return new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
	}
}
