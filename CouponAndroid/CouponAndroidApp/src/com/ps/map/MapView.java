package com.ps.map;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.ps.coupon.CommonUtilities;
import com.ps.coupon.MainView;
import com.ps.coupon.R;
import com.ps.coupon.StoreOnline;
import com.ps.coupon.policy_Activity;
import com.ps.favbean.Citybean;
import com.ps.favbean.Mapbean;
import com.ps.favoritelist.FavoriteListView;
import com.ps.helper.ResponceParser;
import com.ps.helper.WebServices;
import com.ps.helper.WebserviceHelper;
import com.ps.util.Common;
import com.ps.util.Data;
import com.ps.util.RequestReceiver;

public class MapView extends Activity implements RequestReceiver,
		OnItemSelectedListener {

	// Google Map
	public static GoogleMap googleMap;
	Button policyBtn, favListBtn, mShopOnlineBtn, mSearchBtn, mCloseBtn;
	ImageView mLogoBack, mdirshowBtn;
	String mresult, mcityName;
	EditText etPlace;
	TextView get_dir;
	ProgressDialog mProgressDialog;
	Mapbean mapcustomList = null;
	Citybean citycustomList = null;
	JSONArray jArray;

	URL url;
	Spinner mSpinner;

	@SuppressWarnings("rawtypes")
	ArrayList categories;
	@SuppressWarnings("rawtypes")
	ArrayAdapter dataAdapter;
	boolean checkNet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Locale.setDefault(new Locale("iw", "IL"));
		setContentView(R.layout.mapview);
		Common.globalContext = this;
		Common.mactivity = this;
		checkNet = checkInternetConnection();
		if (checkNet) {
			init();
			clickListener();
			initilizeMap();
		} else {
			Toast.makeText(Common.globalContext,
					"Internet Connection Not Present", Toast.LENGTH_SHORT)
					.show();
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

	private void clickListener() {

		policyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				googleMap = null;
				Intent policy = new Intent(MapView.this, policy_Activity.class);
				Common.globalContext.startActivity(policy);
				finish();
			}
		});

		favListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				googleMap = null;
				Intent favintent = new Intent(MapView.this,
						FavoriteListView.class);
				Common.globalContext.startActivity(favintent);
				
				finish();
			}
		});

		mShopOnlineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				googleMap = null;
				Intent shoponline = new Intent(MapView.this, StoreOnline.class);
				Common.globalContext.startActivity(shoponline);
				finish();

			}
		});

		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				googleMap = null;
				/*Intent back = new Intent(MapView.this, MainView.class);
				back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(back);*/
				setResult(CommonUtilities.BACK_TO_MAIN);
				finish();

			}
		});

		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				googleMap = null;
				/*Intent home = new Intent(MapView.this, MainView.class);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);*/
				
				setResult(CommonUtilities.BACK_TO_MAIN);
				finish();

			}
		});

		mdirshowBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mcityName == null) {
					Toast.makeText(Common.globalContext, "select address",
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						String url = "waze://?q=" + mcityName;
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse(url));
						Common.globalContext.startActivity(intent);
					} catch (ActivityNotFoundException ex) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id=com.waze"));
						Common.globalContext.startActivity(intent);
					}
				}
			}
		});

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void init() {

		policyBtn = (Button) findViewById(R.id.policybtn);
		favListBtn = (Button) findViewById(R.id.favlistbtn);
		mShopOnlineBtn = (Button) findViewById(R.id.shoponline);
		mSearchBtn = (Button) findViewById(R.id.btn_show);
		etPlace = (EditText) findViewById(R.id.et_place);
		mdirshowBtn = (ImageView) findViewById(R.id.dir_show);
		get_dir = (TextView) findViewById(R.id.get_dir);
		mCloseBtn = (Button) findViewById(R.id.close);
		mLogoBack = (ImageView) findViewById(R.id.logo);
		mSpinner = (Spinner) findViewById(R.id.spinner);
		// Spinner click listener

		categories = new ArrayList();
		categories.add("אנא בחר עיר");

		dataAdapter = new ArrayAdapter(MapView.this,
				android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		mSpinner.setAdapter(dataAdapter);
		

		WebserviceHelper helper = new WebserviceHelper(this);
		helper.addParam("action", "getStore");
		helper.addParam("store", "Yes");

		helper.execute(helper.buildUrl(WebServices.MAP_URL));
	}

	@SuppressLint("NewApi")
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(Common.globalContext,
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}

		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				googleMap.getProjection();
				mcityName = marker.getTitle();
				get_dir.setText(mcityName.toString());

				return false;
			}
		});

		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {

				return null;
			}

			@Override
			public View getInfoContents(Marker arg0) {
				View myContentsView = null;
				myContentsView = getLayoutInflater().inflate(
						R.layout.custom_info_content, null);
				TextView tvTitle = ((TextView) myContentsView
						.findViewById(R.id.title));
				tvTitle.setText(arg0.getTitle());

				return myContentsView;
			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void requestFinished(String result) {

		try {

			JSONArray jArray = new JSONArray(result);
			// Common.mapList = new ArrayList<Mapbean>();
			if (Common.cityList.size() != 0) {
				Common.cityList = null;
				Common.cityList = new ArrayList<Citybean>();
			}

			for (int i = 0; i < jArray.length(); i++) {
				if (i == 0) {

					JSONObject json_data = jArray.getJSONObject(0);
					JSONArray store_array = json_data.getJSONArray("store");

					for (int j = 0; j < store_array.length(); j++) {
						JSONObject jsonObject = store_array.getJSONObject(j);

						mapcustomList = new Mapbean();
						mapcustomList.setId(Integer.parseInt(jsonObject
								.getString("id")));
						mapcustomList.setsStoreName(jsonObject
								.getString("s_name"));
						mapcustomList.setsStoreText(jsonObject
								.getString("s_text"));
						mapcustomList.setsDescription(jsonObject
								.getString("s_address"));
						mapcustomList.setiLatitute(jsonObject
								.getString("s_lat"));
						mapcustomList.setiLongitute(jsonObject
								.getString("s_long"));
						mapcustomList.setsImgPath(jsonObject
								.getString("s_image"));
						Common.mapList.add(mapcustomList);
					}
				} else if (i == 1) {

					JSONObject json_data = jArray.getJSONObject(1);
					JSONArray city_array = json_data.getJSONArray("city");

					for (int j = 0; j < city_array.length(); j++) {
						JSONObject jsonObject = city_array.getJSONObject(j);

						citycustomList = new Citybean();
						citycustomList.setsCityId(jsonObject.getString("id"));
						citycustomList.setsCityLat(jsonObject
								.getString("c_lat"));
						citycustomList.setsCityLong(jsonObject
								.getString("c_long"));
						citycustomList.setsCityName(jsonObject
								.getString("c_name"));

						Common.cityList.add(citycustomList);

					}
				}

			}

			if (categories.size() != 0) {
				categories = null;
				categories = new ArrayList();
			}

			for (int i = 0; i < Common.cityList.size(); i++) {
				categories.add(Common.cityList.get(i).getsCityName());
			}

			dataAdapter = new ArrayAdapter(MapView.this,
					android.R.layout.simple_spinner_item, categories);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// attaching data adapter to spinner
			mSpinner.setAdapter(dataAdapter);
			mSpinner.setOnItemSelectedListener(this);

		} catch (JSONException e) {
			e.printStackTrace();

		} catch (NullPointerException e) {

		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		Common.CITY_INDEX = Common.cityList.get(position).getsCityId();

		ResponceParser res = new ResponceParser();
		res.setAction(Data.CITY);
		res.execute(WebServices.CITY_URL);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		googleMap = null;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if(resultCode == CommonUtilities.BACK_TO_MAIN)
		{
			setResult(CommonUtilities.BACK_TO_MAIN);
			finish();
			
		}
		
	}
	
	
}
