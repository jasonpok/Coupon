package com.ps.hcentercoupon;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	// public static final String SENDER_ID = "244303119180";
	// Cart Crunch Android App...
	public static final String SENDER_ID = "808417889021";
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "AndroidHive GCM";
	public static final String DISPLAY_MESSAGE_ACTION = "com.ps.hcentercoupon.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";
	
	public static final int BACK_TO_MAIN = 20000;
	public static final int REQUEST_OTHER = 10000; 
	public static final int REQUEST_COUPON_DETAIL = 10001;

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */

	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	
	public static String trimString(String val)
	{
		if (val == null)
			return "";
		return val.substring(val.indexOf('['));
	}
}
