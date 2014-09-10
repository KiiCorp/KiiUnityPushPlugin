package com.kii.cloud.unity;

import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class JsonPath {
	public static String query(JSONObject json, String query) {
		String[] fields = query.replace("$.", "").split("\\.");
		try {
			String value = null;
			for (String field : fields) {
				Object o = json.get(field);
				if (o instanceof JSONObject) {
					json = (JSONObject)o;
				} else {
					json = null;
					value = o.toString();
				}
			}
			return value;
		} catch (Exception e) {
			return null;
		}
	}
	public static boolean isJsonQuery(String query) {
		if (TextUtils.isEmpty(query)) {
			return false;
		}
		if (query.startsWith("$.") && query.lastIndexOf("$.") == 0) {
			return true;
		}
		return false;
	}
}
