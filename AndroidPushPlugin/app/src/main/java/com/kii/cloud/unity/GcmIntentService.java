package com.kii.cloud.unity;

import org.json.JSONObject;

import android.content.Context;

/**
 * Default implementation of AbstractGcmIntentService.
 * You can change a behavior when a push notification is received by edit the configuration file /Plugins/Android/res/values/kii-push-config.xml. 
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class GcmIntentService extends AbstractGcmIntentService {

	public GcmIntentService() {
		super();
	}
	@Override
	protected boolean onHandlePushMessage(Context context, MessageType messageType, JSONObject receivedMessage, boolean isForeground) {
		// Get configuration from resource file.
		NotificationAreaConfiguration config = this.getNotificationConfiguration(messageType);
		if (config.isShowInNotificationArea() && !isForeground) {
			this.showNotificationArea(
					context,
					receivedMessage,
					config.isUseSound(),
					config.getLedColor(),
					config.getVibrationMilliseconds(),
					config.getNotificationTitle(),
					config.getNotificationTicker(),
					config.getNotificationText());
		}
		return true;
	}
	/**
	 * Gets configuration of behavior when received push notification from resource file.
	 * 
	 * @param type
	 * @return
	 */
	private NotificationAreaConfiguration getNotificationConfiguration(MessageType type) {
		String prefix = null;
		switch (type) {
			case PUSH_TO_APP:
				prefix = "kii_push_app_";
				break;
			case PUSH_TO_USER:
				prefix = "kii_push_user_";
				break;
			case DIRECT_PUSH:
				prefix = "kii_push_direct_";
				break;
		}
		boolean showInNotificationArea = this.getResouceValueAsBoolean(prefix + "showInNotificationArea");
		boolean useSound = this.getResouceValueAsBoolean(prefix + "useSound");
		String ledColor = this.getResouceValueAsString(prefix + "ledColor");
		int vibrationMilliseconds = this.getResouceValueAsInteger(prefix + "vibrationMilliseconds");
		String notificationTitle = this.getResouceValueAsString(prefix + "notificationTitle");
		String notificationTicker = this.getResouceValueAsString(prefix + "notificationTicker");
		String notificationText = this.getResouceValueAsString(prefix + "notificationText");
		return new NotificationAreaConfiguration(showInNotificationArea, useSound, ledColor, vibrationMilliseconds, notificationTitle, notificationTicker, notificationText);
	}
	/**
	 * Configuration class for showing notification area.
	 */
	private static class NotificationAreaConfiguration {
		private final boolean showInNotificationArea;
		private final boolean useSound;
		private final String ledColor;
		private final int vibrationMilliseconds;
		private final String notificationTitle;
		private final String notificationTicker;
		private final String notificationText;
		NotificationAreaConfiguration(boolean showInNotificationArea, boolean useSound, String ledColor, int vibrationMilliseconds, String notificationTitle, String notificationTicker, String notificationText)
		{
			this.showInNotificationArea = showInNotificationArea;
			this.useSound = useSound;
			this.ledColor = ledColor;
			this.vibrationMilliseconds = vibrationMilliseconds;
			this.notificationTitle = notificationTitle;
			this.notificationTicker = notificationTicker;
			this.notificationText = notificationText;
		}
		public boolean isShowInNotificationArea() {
			return showInNotificationArea;
		}
		public boolean isUseSound() {
			return useSound;
		}
		public String getLedColor() {
			return ledColor;
		}
		public long getVibrationMilliseconds() {
			return vibrationMilliseconds;
		}
		public String getNotificationTitle() {
			return notificationTitle;
		}
		public String getNotificationTicker() {
			return notificationTicker;
		}
		public String getNotificationText() {
			return notificationText;
		}
	}
}
