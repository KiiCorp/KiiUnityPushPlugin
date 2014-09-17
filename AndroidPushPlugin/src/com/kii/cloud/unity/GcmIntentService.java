package com.kii.cloud.unity;

import org.json.JSONObject;

import android.content.Context;

/**
 * @author noriyoshi.fukuzaki@kii.com
 *
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
					config.getLedSettings(),
					config.getVibrationMilliseconds(),
					config.getNotificationTitle(),
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
		String notificationText = this.getResouceValueAsString(prefix + "notificationText");
		return new NotificationAreaConfiguration(showInNotificationArea, useSound, ledColor, vibrationMilliseconds, notificationTitle, notificationText);
	}
	private static class NotificationAreaConfiguration {
		private final boolean showInNotificationArea;
		private final boolean useSound;
		private final String ledSettings;
		private final int vibrationMilliseconds;
		private final String notificationTitle;
		private final String notificationText;
		NotificationAreaConfiguration(boolean showInNotificationArea, boolean useSound, String ledSettings, int vibrationMilliseconds, String notificationTitle, String notificationText)
		{
			this.showInNotificationArea = showInNotificationArea;
			this.useSound = useSound;
			this.ledSettings = ledSettings;
			this.vibrationMilliseconds = vibrationMilliseconds;
			this.notificationTitle = notificationTitle;
			this.notificationText = notificationText;
		}
		public boolean isShowInNotificationArea() {
			return showInNotificationArea;
		}
		public boolean isUseSound() {
			return useSound;
		}
		public String getLedSettings() {
			return ledSettings;
		}
		public long getVibrationMilliseconds() {
			return vibrationMilliseconds;
		}
		public String getNotificationTitle() {
			return notificationTitle;
		}
		public String getNotificationText() {
			return notificationText;
		}
	}
}
