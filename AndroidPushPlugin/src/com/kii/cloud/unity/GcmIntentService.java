package com.kii.cloud.unity;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("KiiGcmIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("GcmIntentService", "#####onHandleIntent");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		Log.d("GcmIntentService", "#####messageType=" + messageType);
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			Bundle extras = intent.getExtras();
			String message = this.toJson(extras).toString();
			KiiPushUnityPlugin.getInstance().sendPushNotification(message);
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}
	private JSONObject toJson(Bundle bundle) {
		JSONObject json = new JSONObject();
		for (String key : bundle.keySet()) {
			try {
				json.put(key, bundle.get(key));
			} catch (JSONException ignore) {
			}
		}
		return json;
	}
}
