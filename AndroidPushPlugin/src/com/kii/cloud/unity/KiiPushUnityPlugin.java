package com.kii.cloud.unity;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unity3d.player.UnityPlayer;

/**
 * 
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class KiiPushUnityPlugin {
	
	private static KiiPushUnityPlugin INSTANCE = new KiiPushUnityPlugin();
	private static Handler handler = new Handler(Looper.getMainLooper());
	
	public static KiiPushUnityPlugin getInstance() {
		Log.d("KiiPushUnityPlugin", "#####KiiPushUnityPlugin.getInstance()");
		return INSTANCE;
	}
	
	private String listenerGameObjectName;
	private String senderId;
	private SharedPreferences sharedPreference;
	
	private KiiPushUnityPlugin() {
		Log.d("KiiPushUnityPlugin", "#####KiiPushUnityPlugin constractor");
	}
	public String getListenerGameObjectName() {
		if (TextUtils.isEmpty(this.listenerGameObjectName)) {
			return "KiiPushPlugin";
		}
		return this.listenerGameObjectName;
	}
	public void setListenerGameObjectName(String listenerGameObjectName) {
		Log.d("KiiPushUnityPlugin", "#####setListenerGameObjectName " + listenerGameObjectName);
		this.listenerGameObjectName = listenerGameObjectName;
	}
	public String getSenderId() {
		return this.senderId;
	}
	public void setSenderId(String senderId) {
		Log.d("KiiPushUnityPlugin", "#####setSenderId " + senderId);
		this.senderId = senderId;
	}
	
	public void sendPushNotification(Context context, String message) {
		Log.d("KiiPushUnityPlugin", "#####sendPushNotification " + message);
		Editor editor = this.getSharedPreference(context).edit();
		editor.putString("LAST_MESSAGE", message);
		editor.commit();
		this.UnitySendMessage(this.getListenerGameObjectName(), "OnPushNotificationsReceived", message);
	}
	public SharedPreferences getSharedPreference(Context context) {
		if (this.sharedPreference == null) {
			if (context != null) {
				this.sharedPreference = context.getSharedPreferences("KiiPushUnityPlugin", Context.MODE_PRIVATE);
			}
		}
		return this.sharedPreference;
	}
	public String getLastMessage() {
		String lastMessage = this.getSharedPreference(UnityPlayer.currentActivity).getString("LAST_MESSAGE", null);
		if (lastMessage != null) {
			Editor editor = this.getSharedPreference(UnityPlayer.currentActivity).edit();
			editor.remove("LAST_MESSAGE");
			editor.commit();
		}
		return lastMessage;
	}
	public void getRegistrationID() {
		Log.d("KiiPushUnityPlugin", "#####getRegistrationID");
		// Ensure that the AsyncTask is called from main thread.
		handler.post(new Runnable() {
			public void run() {
				AsyncTask<String, Void, Void> registerTask = new AsyncTask<String, Void, Void>() {
					@Override
					protected Void doInBackground(String... params) {
						String registrationId = "";
						String errorMessage = "";
						for (int retry = 0; retry < 3; retry++) {
							try {
								GoogleCloudMessaging gcm = GoogleCloudMessaging
										.getInstance(UnityPlayer.currentActivity);
								registrationId = gcm.register(params[1]);
							} catch (Throwable e) {
								// Nothing to do.
								Log.d("KiiPushUnityPlugin", "#####Push register is failed");
								errorMessage = e.getMessage();
							}
							if (!registrationId.equals("")) {
								Log.d("KiiPushUnityPlugin", "#####Found RegistrationID : " + registrationId);
								break;
							}
						}
						if (TextUtils.isEmpty(registrationId)) {
							UnitySendMessage(params[0], "OnRegisterPushFailed", errorMessage);
						} else {
							UnitySendMessage(params[0], "OnRegisterPushSucceeded", registrationId);
						}
						return null;
					}
				};
				registerTask.execute(getListenerGameObjectName(), senderId);
			}
		});
	}
	public void unregisterGCM() throws IOException {
		Log.d("KiiPushUnityPlugin", "#####unregisterGCM");
		// Ensure that the AsyncTask is called from main thread.
		handler.post(new Runnable() {
			public void run() {
				AsyncTask<String, Void, Void> unregisterTask = new AsyncTask<String, Void, Void>() {
					@Override
					protected Void doInBackground(String... params) {
						GoogleCloudMessaging gcm = GoogleCloudMessaging
								.getInstance(UnityPlayer.currentActivity);
						try {
							gcm.unregister();
							UnitySendMessage(params[0], "OnUnregisterPushSucceeded", "");
						} catch (IOException e) {
							Log.d("KiiPushUnityPlugin", "#####Push unregister is failed");
							UnitySendMessage(params[0], "OnUnregisterPushFailed", e.getMessage());
						}
						return null;
					}
				};
				unregisterTask.execute(getListenerGameObjectName());
			}
		});
	}
	private void UnitySendMessage(String object, String method, String message) {
		try {
			UnityPlayer.UnitySendMessage(object, method, message);
		} catch (Throwable th) {
			Log.e("KiiPushUnityPlugin", "#####Failed to send UnitySendMessage ex=" + th.getMessage());
		}
	}
}
