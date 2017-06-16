package com.ps.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ps.coupon.MainView;
import com.ps.coupon.policy_Activity;
import com.ps.db.DbHelper;
import com.ps.favbean.CitybeanAfterParse;
import com.ps.favoritelist.FavoriteListView;
import com.ps.map.MapView;
import com.ps.util.Common;
import com.ps.util.Data;

public class ResponceParser extends AsyncTask<String, Void, JSONArray> {

	ProgressDialog dialog;
	ServiceHandler service;
	public String strResponce = null;
	protected ProgressDialog mProgressDialog;
	public static int action;
	CitybeanAfterParse citycustomList;
	JSONArray json;
	Bitmap bmp;
	URL url;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(Common.globalContext);
		onCreateDialog(Data.DIALOG_DOWNLOAD_PROGRESS);
	}

	public ResponceParser() {

		service = new ServiceHandler();

	}

	public void setAction(int action) {
		ResponceParser.action = action;
	}

	protected JSONArray doInBackground(String... args) {

		switch (action) {
		case Data.LIKE: {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("action", "dolike"));
			params.add(new BasicNameValuePair("like", "Yes"));
			params.add(new BasicNameValuePair("coupan_id", Data.likeValues));
			params.add(new BasicNameValuePair("device_id", Data.DEVICE_ID));

			json = new JSONParser().makeHttpRequest(args[0], "POST", params);

		}
			break;
		case Data.CITY: {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("action", "doGetStoreBycity"));
			params.add(new BasicNameValuePair("city", Common.CITY_INDEX));

			json = new JSONParser().makeHttpRequest(args[0], "POST", params);

		}
			break;

		case Data.POLICY: {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("action", "getPolicy"));
			params.add(new BasicNameValuePair("policy", "Yes"));

			json = new JSONParser().makeHttpRequest(args[0], "POST", params);
		}
			break;
		}
		return json;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPostExecute(JSONArray jsonArray) {
		super.onPostExecute(jsonArray);
		try {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		} catch (Exception e) {

		}

		switch (action) {
		case Data.LIKE: {
			try {
				String status = "";
				JSONObject mJsonObject = jsonArray.getJSONObject(0);
				try {
					status = mJsonObject.getString("status");

					Common.vStatus = mJsonObject.getString("total_likes");
					Common.COUPON_ID = mJsonObject.getString("coupan_id");

				} catch (Exception e) {

				}

				if (status.equalsIgnoreCase("success")) {
					if (Common.isFavorMainListClicked) {

						Common.favList.get(Common.Index).setiTotalCount(
								Common.favList.get(Common.Index)
										.getiTotalCount() + 1);
						FavoriteListView.myPager.setAdapter(FavoriteListView.adapter);
						FavoriteListView.adapter.notifyDataSetChanged();
						FavoriteListView.myPager.setCurrentItem(Common.Index);
						
						
						
					} else {

						Common.couponList.get(Common.Index).setiTotalCount(
								Common.couponList.get(Common.Index)
										.getiTotalCount() + 1);

						try {
							MainView.myPager.setAdapter(MainView.adapter);
							MainView.adapter.notifyDataSetChanged();
							MainView.myPager.setCurrentItem(Common.Index);
							
							
						} catch (Exception e) {
							// TODO: handle exception
						}

						DbHelper db = new DbHelper(Common.globalContext);
						db.updateInfo(Common.vStatus, Data.likeValues);

					}
				} else {
					// Toast.makeText(Common.globalContext, "Already liked",
					// Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;
		case Data.CITY: {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			try {

				if (Common.cityListAfterParse.size() != 0) {
					Common.cityListAfterParse = null;
					Common.cityListAfterParse = new ArrayList<CitybeanAfterParse>();
				}

				JSONArray jArr = new JSONArray(json.toString());
				for (int count = 0; count < jArr.length(); count++) {
					citycustomList = new CitybeanAfterParse();
					JSONObject mJsonObject = jArr.getJSONObject(count);
					citycustomList.setsCityId(mJsonObject.getString("id"));
					citycustomList.setsCityAddress(mJsonObject
							.getString("s_address"));

					citycustomList
							.setsCityLong(mJsonObject.getString("s_long"));
					citycustomList.setsCityLat(mJsonObject.getString("s_lat"));
					citycustomList.setsCityImage(mJsonObject
							.getString("s_image"));
					citycustomList
							.setsCityName(mJsonObject.getString("s_name"));
					Common.cityListAfterParse.add(citycustomList);
				}

				MapView.googleMap.clear();

				for (int i = 0; i < Common.cityListAfterParse.size(); i++) {
					if (bmp != null) {
						bmp = null;
					}

					try {
						url = new URL(Common.cityListAfterParse.get(i)
								.getsCityImage());

						bmp = BitmapFactory.decodeStream(url.openConnection()
								.getInputStream());

					} catch (Exception e) {
						Log.e("", "Exception " + e.getMessage());

					}

					MapView.googleMap
							.addMarker(new MarkerOptions()
									.position(
											new LatLng(
													Double.parseDouble(Common.cityListAfterParse
															.get(i)
															.getsCityLat()
															.toString()),
													Double.parseDouble(Common.cityListAfterParse
															.get(i)
															.getsCityLong()
															.toString())))
									.title(Common.cityListAfterParse.get(i)
											.getsCityAddress().toString())
									.icon(BitmapDescriptorFactory
											.fromBitmap(bmp)));
				}
			} catch (Exception e) {
				Log.e("", "add marker Exception " + e.getMessage());

			}

			Common.mactivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					try {

						CameraUpdate center = CameraUpdateFactory
								.newLatLng(new LatLng(Double
										.parseDouble(Common.cityListAfterParse
												.get(0).getsCityLat()
												.toString()), Double
										.parseDouble(Common.cityListAfterParse
												.get(0).getsCityLong()
												.toString())));
						CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

						MapView.googleMap.moveCamera(center);
						MapView.googleMap.animateCamera(zoom);
					} catch (Exception e) {

					}

				}
			});

		}
			break;

		case Data.POLICY: {

			try {

				JSONArray jArr = new JSONArray(json.toString());
				for (int count = 0; count < jArr.length(); count++) {
					JSONObject mJsonObject = jsonArray.getJSONObject(count);
					String status = mJsonObject.getString("status");
					JSONObject mJsonObject1 = mJsonObject
							.getJSONObject("policy");
					String text = mJsonObject1.getString("p_name");

					if (status.equalsIgnoreCase("success")) {
						Common.POLICY_DESCRIPTION = text.toString();
						policy_Activity.mpolicyText
								.setText(Common.POLICY_DESCRIPTION);
					} else {
						Toast.makeText(Common.globalContext, "" + status,
								Toast.LENGTH_SHORT).show();
					}
				}

			} catch (Exception e) {

			}
		}
			break;
		}

	}

	private Dialog onCreateDialog(int id) {
		switch (id) {
		case Data.DIALOG_DOWNLOAD_PROGRESS:

			mProgressDialog.setMessage("Wait..");

			mProgressDialog.setMax(100);
			mProgressDialog
					.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			mProgressDialog.setCancelable(false);
			try {
				mProgressDialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}

			return mProgressDialog;
		default:
			return null;
		}
	}
}
