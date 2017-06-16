package com.ps.helper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.ps.coupon.CommonUtilities;
import com.ps.util.Common;
import com.ps.util.RequestReceiver;

public class WebserviceHelper extends AsyncTask<String, Integer, String> {

	private RequestReceiver mContext;
	private HttpClient httpClient;

	@SuppressWarnings("unused")
	private String method = null;
	private Map<String, String> paramMap = new HashMap<String, String>();
	private String errorMessage;
	private boolean error_flag = false;
	ProgressDialog mProgressDialog;
	private String url = null;

	public WebserviceHelper() {
	}

	public WebserviceHelper(RequestReceiver context) {
		mContext = context;
	}

	WebserviceHelper(RequestReceiver context, String setMethod) {
		mContext = context;
		method = setMethod;
	}

	private void clearErrors() {
		this.errorMessage = null;
		this.error_flag = false;
	}

	private void setErrors(String theMessage) {
		this.errorMessage = theMessage;
		this.error_flag = true;
	}

	public void setMethod(String m) {
		method = m;
	}

	public void addParam(String key, String value) {
		paramMap.put(key, value);
	}

	@Override
	protected void onPreExecute() {
		httpClient = new DefaultHttpClient();
		mProgressDialog = new ProgressDialog(Common.globalContext);
		mProgressDialog.setMessage("Wait..");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);

		mProgressDialog.setCancelable(true);
		try {
			mProgressDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}

		this.clearErrors();
	}

	@Override
	protected String doInBackground(String... urls) {
		String data = null;

		try {

			String url = urls[0];
			 
			HttpGet request = new HttpGet(url);
			HttpParams params = request.getParams();
			HttpConnectionParams.setSoTimeout(params, 60000); // 1 minute
			request.setParams(params);
			
			data = httpClient.execute(request, new BasicResponseHandler());

		} catch (IOException e) {

			this.setErrors("We are having problems connecting to the network.");

			e.printStackTrace();
		} catch (Exception e) {
			// do some stuff or whatever

			e.printStackTrace();
		}

		return CommonUtilities.trimString(data);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		((RequestReceiver) mContext).requestFinished(result);
	}

	public boolean errors_occurred() {
		return this.error_flag;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public String buildUrl(String strServiceUrl) {
		
		
		url = strServiceUrl;
		urlMethod();
		return url;
	}

	
	public void urlMethod() {

		boolean first = true;
		for (Map.Entry<String, String> e : paramMap.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();
			
			try {
				value = URLEncoder.encode(value,"UTF-8");	
			} catch (Exception e2) {
				// TODO: handle exception
			}
			

			if (first) {
				first = false;
			} else {
				url += "&";
			}
			
			
			url += key + "=" + value;
			
			
		}

	}

}
