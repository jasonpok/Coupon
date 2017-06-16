package com.ps.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ps.util.Common;
import com.ps.util.Data;

public class UserFunctions extends AsyncTask<String, Integer, String> {

	ServiceHandler service;
	public static int action;
	Context mcontext;
	public String strResponce = null;
	protected ProgressDialog mProgressDialog;
	String responcestr;

	// constructor
	public UserFunctions() {

		service = new ServiceHandler();
		mProgressDialog = new ProgressDialog(Common.globalContext);
	}

	@Override
	protected String doInBackground(String... param) {

		switch (action) {
		case Data.DISPLAY_COUPON: {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("action", "getCoupan"));
			params.add(new BasicNameValuePair("coupan", "Yes"));
			params.add(new BasicNameValuePair("device_id", Data.DEVICE_ID));
			strResponce = service.makeServiceCall(WebServices.COUPON_URL, 1,
					params);
			

		}
			break;
		case Data.GSM: {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("action", "putGCM"));
			params.add(new BasicNameValuePair("device_id", Data.DEVICE_ID));
			params.add(new BasicNameValuePair("device_name", Data.DEVICE_NAME));
			params.add(new BasicNameValuePair("device_gcm", Data.DEVICE_GCMID));
			params.add(new BasicNameValuePair("device_type", Data.DEVICE_TYPE));
			Log.e("","value "+Data.DEVICE_GCMID);
			strResponce = service.makeServiceCall(WebServices.GCM_URL, 1,
					params);

		}
			break;

		}

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		onCreateDialog(Data.DIALOG_DOWNLOAD_PROGRESS);

	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);

		try {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}

	public void setAction(int action) {
		UserFunctions.action = action;
	}

	private Dialog onCreateDialog(int id) {
		switch (id) {
		case Data.DIALOG_DOWNLOAD_PROGRESS:

			mProgressDialog.setMessage("Wait..");
			mProgressDialog.setMax(100);
			mProgressDialog
					.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

}