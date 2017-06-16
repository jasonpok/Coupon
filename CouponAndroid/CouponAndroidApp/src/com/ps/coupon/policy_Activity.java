package com.ps.coupon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ps.favoritelist.FavoriteListView;
import com.ps.helper.ResponceParser;
import com.ps.helper.WebServices;
import com.ps.map.MapView;
import com.ps.util.Common;
import com.ps.util.Data;

public class policy_Activity extends Activity {

	Button mShopOnlineBtn, mFavBtn, mMapBtn, mCloseBtn;
	ImageView mLogoBack;
	public static TextView mpolicyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.policy_layout);
		Common.globalContext = this;
		init();
		clickListener();
	}

	void init() {

		mShopOnlineBtn = (Button) findViewById(R.id.shoponline);
		mFavBtn = (Button) findViewById(R.id.favlistbtn);
		mMapBtn = (Button) findViewById(R.id.mapbtn);
		mCloseBtn = (Button) findViewById(R.id.close);
		mLogoBack = (ImageView) findViewById(R.id.logo);
		mpolicyText = (TextView) findViewById(R.id.text);
		boolean status = checkInternetConnection();
		if (status) {
			ResponceParser policy = new ResponceParser();
			policy.setAction(Data.POLICY);
			policy.execute(WebServices.POLICY_URL);
		}
		else {
			Toast.makeText(Common.globalContext, "Internet Connection Not Present",
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

	void clickListener() {

		mShopOnlineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent shoponline = new Intent(policy_Activity.this,
						StoreOnline.class);
				startActivityForResult(shoponline,CommonUtilities.REQUEST_OTHER);

			}
		});
		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent back = new Intent(policy_Activity.this, MainView.class);
				back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(back,CommonUtilities.REQUEST_OTHER);
				finish();

			}
		});

		mFavBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent favintent = new Intent(policy_Activity.this,
						FavoriteListView.class);
				startActivityForResult(favintent,CommonUtilities.REQUEST_OTHER);
				finish();
			}
		});
		mMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent map = new Intent(policy_Activity.this, MapView.class);
				startActivityForResult(map,CommonUtilities.REQUEST_OTHER);
				finish();
			}
		});
		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*Intent home = new Intent(policy_Activity.this, MainView.class);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);*/
				setResult(CommonUtilities.BACK_TO_MAIN);
				finish();

			}
		});
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
