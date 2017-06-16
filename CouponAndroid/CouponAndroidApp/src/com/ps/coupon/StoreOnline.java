package com.ps.coupon;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

public class StoreOnline extends Activity {

	WebView webview;
	Button mCloseBtn;
	ImageView mLogoBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.store_online_layout);
		init();
		clickListener();
	}

	private void clickListener() {
		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*Intent home = new Intent(StoreOnline.this, MainView.class);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);*/
				setResult(CommonUtilities.BACK_TO_MAIN);
				
				finish();

			}
		});
		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				/*Intent back = new Intent(StoreOnline.this, MainView.class);
				back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(back);*/
				setResult(CommonUtilities.BACK_TO_MAIN);
				finish();

			}
		});


	}

	@SuppressLint("SetJavaScriptEnabled")
	void init() {

		WebView webview = (WebView) findViewById(R.id.webview);
		mCloseBtn = (Button) findViewById(R.id.close);
		mLogoBack = (ImageView)findViewById(R.id.logo);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl("https://toysrus.co.il/");
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);

				return true;
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
