package com.kii.cloud.unity;

import java.io.IOException;

import android.os.AsyncTask;
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
	
	public static KiiPushUnityPlugin getInstance() {
		Log.d("KiiPushUnityPlugin", "#####KiiPushUnityPlugin.getInstance()");
		return INSTANCE;
	}
	
	private String listenerGameObjectName;
	private String senderId;
	
	private KiiPushUnityPlugin() {
		Log.d("KiiPushUnityPlugin", "#####KiiPushUnityPlugin constractor");
	}
	public String getListenerGameObjectName() {
		return this.listenerGameObjectName;
	}
	public void setListenerGameObjectName(String listenerGameObjectName) {
		Log.d("KiiPushUnityPlugin", "#####setListenerGameObjectName " + listenerGameObjectName);
		this.listenerGameObjectName = listenerGameObjectName;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		Log.d("KiiPushUnityPlugin", "#####setSenderId " + senderId);
		this.senderId = senderId;
	}
	
	public void sendPushNotification(String message) {
		Log.d("KiiPushUnityPlugin", "#####sendPushNotification " + message);
		UnityPlayer.UnitySendMessage(this.listenerGameObjectName, "OnPushNotificationsReceived", message);
	}

    public void getRegistrationID() {
        Log.d("KiiPushUnityPlugin", "#####getRegistrationID");
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
                        Log.d("KiiPushUnityPlugin",
                                "#####Push register is failed");
                        errorMessage = e.getMessage();
                    }
                    if (!registrationId.equals("")) {
                        Log.d("KiiPushUnityPlugin",
                                "#####Found RegistrationID : " + registrationId);
                        break;
                    }
                }
                if (TextUtils.isEmpty(registrationId)) {
                    UnityPlayer.UnitySendMessage(params[0],
                            "OnRegisterPushFailed", errorMessage);
                } else {
                    UnityPlayer.UnitySendMessage(params[0],
                            "OnRegisterPushSucceeded", registrationId);
                }
                return null;
            }
        };
        registerTask.execute(this.listenerGameObjectName, this.senderId);
    }

    public void unregisterGCM() throws IOException {
        Log.d("KiiPushUnityPlugin", "#####unregisterGCM");
        AsyncTask<String, Void, Void> unregisterTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging
                        .getInstance(UnityPlayer.currentActivity);
                try {
                    gcm.unregister();
                    UnityPlayer.UnitySendMessage(params[0],
                            "OnUnregisterPushSucceeded", "");
                } catch (IOException e) {
                    Log.d("KiiPushUnityPlugin",
                            "#####Push unregister is failed");
                    UnityPlayer.UnitySendMessage(params[0],
                            "OnUnregisterPushFailed", e.getMessage());
                }
                return null;
            }
        };
        unregisterTask.execute(this.listenerGameObjectName);
    }

}
