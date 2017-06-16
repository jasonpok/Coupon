package com.ps.coupon;

import static com.ps.coupon.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class SplashActivity extends Activity {
	public static String vRegID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		// -----GCM Registration--------//
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		vRegID = GCMRegistrar.getRegistrationId(this);
		System.out.println("vRegID 1" + vRegID);

		if (vRegID.equals("")) {
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
			System.out.println("vRegID 2" + vRegID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// GCMRegistrar.getRegistrationId(this);
				System.out.println("vRegID 3" + vRegID);
				System.out.println("GCM Register on server" + vRegID);
			} else {
				System.out.println("vRegID 4" + vRegID);
				GCMRegistrar.setRegisteredOnServer(this, true);
			}
		}
		System.out.println("vRegID 5" + vRegID);
		// Log.v("", "Already registered:  " + vRegID);
	}

	// /**
	// * Receiving push messages
	// */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// System.out.println("newMessage" + newMessage);
			// vRegID = newMessage;
			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}

	}

}
