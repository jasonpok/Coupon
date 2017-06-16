package com.ps.hcentercoupon;


import static com.ps.hcentercoupon.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ps.favbean.Favorite;
import com.ps.helper.UserFunctions;
import com.ps.helper.WebServices;
import com.ps.helper.WebserviceHelper;
import com.ps.util.Common;
import com.ps.util.Data;
import com.ps.util.ImageLoader;
import com.ps.util.RequestReceiver;

public class HomeView extends Activity implements RequestReceiver {
	ImageView button,banner;
	public static String devicename = "";
	public static String deviceid = "";
	TelephonyManager mTelephonyManager;

	public String vRegID = "";
	Context mContext;
	
	/////////////////
	ProgressDialog mProgressDialog;
	String mresult;
	JSONArray jArray;
	boolean status,b;
	
	
	
	////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_layout);
		Common.globalContext = this;
		Common.mactivity = this;
		mContext = this;
		boolean status = checkInternetConnection();

		if (status) {

			init();
			button = (ImageView) findViewById(R.id.nextbtn);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(HomeView.this, ThumbnailView.class);
					startActivity(intent);
				}
			});
			banner = (ImageView) findViewById(R.id.img_banner);
			
			
		} else {
			Toast.makeText(mContext, "Internet Connection Not Present",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	private boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			System.out.println("Internet Connection Not Present");
			return false;
		}
	}

	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "79MH35FG6GMQMCJ24F9S");
	}

	void init() {

		getRegistrationId();

		getDeviceInfo();

		UserFunctions user = new UserFunctions();
		user.setAction(Data.GSM);
		user.execute();
		
		BannerAsync bannerSync = new BannerAsync();
		bannerSync.execute(WebServices.COUPON_BANNER_URL);

		
		
		// Common.mactivity.runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// DbHelper db = new DbHelper(Common.globalContext);
		// String infoLogin = db.getLoginInfo();
		// if (infoLogin.equalsIgnoreCase("")) {
		// db.addIntoLogin("login");
		// UserFunctions user = new UserFunctions();
		// user.setAction(Data.GSM);
		// user.execute();
		// }
		// }
		// });

	}
	

	/////////////////////////////////
	//////////  WEBSERVICE START
	/////////////////////////////////
	
	
	private void loadWebService() {
		WebserviceHelper helper = new WebserviceHelper(this);
		helper.addParam("action", "getCoupan");
		helper.addParam("coupan", "Yes");
		helper.addParam("version", "v2");
		helper.addParam("category", "0");
		helper.addParam("page", "0");
		helper.addParam("items", String.format("%d", Common.totalCouponCount));
		helper.addParam("device_id", Data.DEVICE_ID);
		helper.execute(helper.buildUrl(WebServices.COUPON_URL));
	}
	

	@Override
	public void requestFinished(String result) {

		mresult = result;
		
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute();

	}


	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Wait..");

			mProgressDialog.setCancelable(true);

			try {
				mProgressDialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			
			try {

				jArray = new JSONArray(mresult);
				
				if(jArray.length() > 0)
				{
					Common.couponList1.clear();
					Common.couponList2.clear();
					Common.couponList3.clear();
					Common.couponList4.clear();
					Common.couponList5.clear();
				}
				
				
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject json_data = jArray.getJSONObject(i);
					Favorite fav = new Favorite();
					fav.setId(json_data.getInt(Data.ID));
					
					fav.setsCouponName(json_data
							.getString(Data.COUPON_NAME));
					fav.setsCouponCategory(json_data.getInt(Data.COUPON_CATEGORY));
					fav.settDate(json_data.getString(Data.TO_DATE));
					fav.setfDate(json_data
							.getString(Data.FROM_DATE));
					fav.setsImgPath(json_data
							.getString(Data.IMAGE_PATH));
					fav.setiTotalCount(json_data
							.getInt(Data.TOTAL_LIKE));
					fav.setsComment(json_data
							.getString(Data.COMMENT));
					fav.setsImageUrl(json_data
							.getString(Data.IMAGE_URL));
					fav.setiCouponNumber(json_data
							.getString(Data.COUPON_NUMBER));
					fav.setsCouponShareImg(json_data
							.getString(Data.COUPON_SHARE_IMAGE));

					
					if(fav.getsCouponCategory() == 1)
						Common.couponList1.add(fav);
					else if(fav.getsCouponCategory() == 2)
						Common.couponList2.add(fav);
					else if(fav.getsCouponCategory() == 3)
						Common.couponList3.add(fav);
					else if(fav.getsCouponCategory() == 4)
						Common.couponList4.add(fav);
					else if(fav.getsCouponCategory() == 5)
						Common.couponList5.add(fav);
					
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {

			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			} catch (Exception e) {

			}
			//loadPager();

		}
	}
	
	
	public class BannerAsync extends AsyncTask<String, Integer, String> {

		//ProgressDialog mProgressDialog;
		private HttpClient httpClient;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			mProgressDialog = new ProgressDialog(Common.globalContext);
			mProgressDialog.setMessage("Wait..");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);

			mProgressDialog.setCancelable(true);
			
			httpClient = new DefaultHttpClient();
			try {
				mProgressDialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		protected String doInBackground(String... params) {

		
			String data=null;
			try {
				
				HttpGet request = new HttpGet(WebServices.COUPON_BANNER_URL);
				HttpParams rparams = request.getParams();
				HttpConnectionParams.setSoTimeout(rparams,10000); // 1 minute
				request.setParams(rparams);
				
				data = httpClient.execute(request, new BasicResponseHandler());
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				status = false;
				e.printStackTrace();
			}catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				status = false;
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				status = false;
			}
			

			if (data != null) {
				status = true;
				System.out.println("Its working");
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				result = CommonUtilities.trimString(result);
				
				String bannerUrl = new JSONArray(result).getJSONObject(0).getString("url");
				ImageLoader imageLoader;
				imageLoader = new ImageLoader(Common.globalContext);
				imageLoader.setRequiredSize(1200);
				imageLoader.DisplayImage(bannerUrl, banner,false);
				
				Async sync = new Async();
				sync.execute(WebServices.COUPON_COUNT_URL);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	public class Async extends AsyncTask<String, Integer, String> {

		
		private HttpClient httpClient;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			mProgressDialog = new ProgressDialog(Common.globalContext);
//			mProgressDialog.setMessage("Wait..");
//			mProgressDialog.setIndeterminate(false);
//			mProgressDialog.setMax(100);
//
//			mProgressDialog.setCancelable(true);
			httpClient = new DefaultHttpClient();
			
			
			try {
				//mProgressDialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		protected String doInBackground(String... params) {

		
			String data=null;
			try {
				
				HttpGet request = new HttpGet(WebServices.COUPON_COUNT_URL);
				HttpParams rparams = request.getParams();
				HttpConnectionParams.setSoTimeout(rparams,10000); // 1 minute
				request.setParams(rparams);
				
				data = httpClient.execute(request, new BasicResponseHandler());
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				status = false;
				e.printStackTrace();
			}catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				status = false;
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				status = false;
			}
			

			if (data != null) {
				status = true;
				System.out.println("Its working");
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				result = CommonUtilities.trimString(result);
				
				Common.totalCouponCount = new JSONArray(result).getJSONObject(0).getInt("count");
			
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			loadWebService();
		}

	}
	
	/////////////////////////////////
	//////////END
	/////////////////////////////////

	void getDeviceInfo() {
		try {
			mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			deviceid = mTelephonyManager.getDeviceId();

		} catch (Exception e) {
			deviceid = Secure
					.getString(getContentResolver(), Secure.ANDROID_ID);
		}
		Data.DEVICE_ID = deviceid;
		devicename = getDeviceName();
		Data.DEVICE_NAME = devicename;

	}

	public void getRegistrationId() {
		// -----GCM Registration--------//
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		vRegID = GCMRegistrar.getRegistrationId(this);

		if (vRegID.equals("")) {
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// GCMRegistrar.getRegistrationId(this);
			} else {
				GCMRegistrar.setRegisteredOnServer(this, true);
			}
		}

		 Data.DEVICE_GCMID = vRegID;
		

	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		FlurryAgent.onEndSession(this);

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
