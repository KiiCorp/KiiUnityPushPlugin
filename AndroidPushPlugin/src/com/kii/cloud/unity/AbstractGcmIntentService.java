package com.kii.cloud.unity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unity3d.player.UnityPlayer;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

/**
 * AbstractGcmIntentService is a base class for Services that handle GCM(Google Cloud Messaging).
 * You can change a behavior when a push notification is received to override the onHandlePushMessage method.
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public abstract class AbstractGcmIntentService extends IntentService {

	public AbstractGcmIntentService() {
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
			JSONObject message = this.toJson(extras);
			MessageType type = getMessageType(message);
			boolean isPropagate = this.onHandlePushMessage(type, message, this.isForeground());
			if (isPropagate) {
				KiiPushUnityPlugin.getInstance().sendPushNotification(this, message.toString());
			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}
	/**
	 * Called by IntentService when service receives push notification.
	 * 
	 * @param messageType
	 * @param receivedMessage 
	 * @param isForeground
	 * @return Return true if you want to raise a receiving event to the Unity.
	 */
	protected abstract boolean onHandlePushMessage(MessageType messageType, JSONObject receivedMessage, boolean isForeground);
	/**
	 * Converts Bundle to JSONObject.
	 * 
	 * @param bundle
	 * @return
	 */
	protected JSONObject toJson(Bundle bundle) {
		JSONObject json = new JSONObject();
		for (String key : bundle.keySet()) {
			try {
				json.put(key, bundle.get(key));
			} catch (JSONException ignore) {
			}
		}
		return json;
	}
	/**
	 * Gets resource id of launcher icon.
	 * 
	 * @return
	 */
	protected int getIcon() {
		return this.getResources().getIdentifier("ic_launcher", "drawable", this.getPackageName());
	}
	/**
	 * Gets resource id of sound file.
	 * 
	 * @return
	 */
	protected int getSound() {
		return this.getResources().getIdentifier("default_sound", "raw", this.getPackageName());
	}
	/**
	 * Gets app name
	 * 
	 * @return
	 */
	protected String getAppName() {
		try {
			int resourceId = this.getResources().getIdentifier("app_name", "string", this.getPackageName());
			return this.getResources().getString(resourceId);
		} catch (Exception ignore) {
			return "Missing @string/app_name";
		}
	}
	/**
	 * Checks if the application is on foreground.
	 * 
	 * @return
	 */
	protected boolean isForeground(){
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : processInfoList){
			if(info.processName.equals(this.getPackageName()) && info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
				return true;
			}
		}
		return false;
	}
	/**
	 * Gets the string value to which the specified key is mapped, or null if resource file contains no mapping for the key.
	 * 
	 * @param key
	 * @return
	 */
	protected String getResouceValueAsString(String key) {
		int id = this.getResources().getIdentifier(key, "string", this.getPackageName());
		if (id == 0) {
			return null;
		}
		return this.getResources().getString(id);
	}
	/**
	 * Gets the boolean value to which the specified key is mapped, or false if resource file contains no mapping for the key.
	 * 
	 * @param key
	 * @return
	 */
	protected boolean getResouceValueAsBoolean(String key) {
		int id = this.getResources().getIdentifier(key, "string", this.getPackageName());
		if (id == 0) {
			return false;
		}
		String value = this.getResources().getString(id);
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception ignore) {
			return false;
		}
	}
	/**
	 * Gets the int value to which the specified key is mapped, or 0 if resource file contains no mapping for the key.
	 * 
	 * @param key
	 * @return
	 */
	protected int getResouceValueAsInteger(String key) {
		int id = this.getResources().getIdentifier(key, "string", this.getPackageName());
		if (id == 0) {
			return 0;
		}
		String value = this.getResources().getString(id);
		try {
			return Integer.parseInt(value);
		} catch (Exception ignore) {
			return 0;
		}
	}
	/**
	 * Shows a received message in the notification area.
	 * 
	 * @param message 
	 * @param useSound 
	 * @param ledColor format is '#AARRGGBB'
	 * @param vibrationMilliseconds set 0 if you want to disable vibration.
	 * @param title Literal text or JsonPath
	 * @param text Literal text or JsonPath
	 */
	protected void showNotificationArea(JSONObject message, boolean useSound, String ledColor, long vibrationMilliseconds, String title, String text) {
		NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			
			String notificationTitle = this.getText(message, title, this.getAppName());
			String notificationText = this.getText(message, text, "");
			
			String launchClassName = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName()).getComponent().getClassName();
			ComponentName componentName = new ComponentName(this.getPackageName(), launchClassName);
			Intent notificationIntent = (new Intent()).setComponent(componentName);
			notificationIntent.putExtra("notificationData", message.toString());
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			int icon = this.getIcon();
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setContentIntent(pendingIntent)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(icon)
				.setContentTitle(notificationTitle)
				.setContentText(notificationText);
			Notification notification = notificationBuilder.build();
			notification.defaults = 0;
			if (useSound) {
				int sound = this.getSound();
				if (sound == 0) {
					notification.defaults |= Notification.DEFAULT_SOUND;
				} else {
					Uri soundUri = Uri.parse("android.resource://" + UnityPlayer.currentActivity.getPackageName()  + "/" + sound);
					notification.sound = soundUri;
				}
			}
			if ("DEFAULT".equalsIgnoreCase(ledColor)) {
				notification.defaults |= Notification.DEFAULT_LIGHTS;
			} else if (!TextUtils.isEmpty(ledColor)) {
				try {
					int argb = (int)Long.parseLong(ledColor.replaceFirst("#", ""), 16);
					notification.flags |= Notification.FLAG_SHOW_LIGHTS;
					notification.ledARGB = argb;
					notification.ledOnMS = 1000;
					notification.ledOffMS = 1000;
				} catch (Exception ignore) {
				}
			}
			if (vibrationMilliseconds > 0) {
				long[] vibratePattern = {0, vibrationMilliseconds, vibrationMilliseconds};
				notification.vibrate = vibratePattern;
			}
			notificationManager.notify(0, notification);
		}
	}
	/**
	 * @param json
	 * @param text literal or JSONPath
	 * @param fallback
	 * @return
	 */
	protected String getText(JSONObject json, String text, String fallback) {
		try {
			if (JsonPath.isJsonQuery(text)) {
				String value = JsonPath.query(json, text);
				if (value == null) {
					return fallback;
				} else {
					return value;
				}
			} else {
				return text;
			}
		} catch (Exception ignore) {
			return fallback;
		}
	}
	protected enum MessageType {
		/**
		 * 'Push to App' notifications
		 */
		PUSH_TO_APP,
		/**
		 * 'Push to User' notifications
		 */
		PUSH_TO_USER,
		/**
		 * 'Direct Push' notifications
		 */
		DIRECT_PUSH
	}
	protected static final String[] PUSH_TO_APP_FIELDS = {"bucketType", "bucketID", "objectID", "modifiedAt"};
	protected static final String[] PUSH_TO_USER_FIELDS = {"topic"};
	protected MessageType getMessageType(JSONObject message) {
		for (String field : PUSH_TO_APP_FIELDS) {
			if (message.has(field)) {
				return MessageType.PUSH_TO_APP;
			}
		}
		for (String field : PUSH_TO_USER_FIELDS) {
			if (message.has(field)) {
				return MessageType.PUSH_TO_USER;
			}
		}
		return MessageType.DIRECT_PUSH;
	}
}
