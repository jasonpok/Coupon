package com.ps.coupon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ps.db.DbHelper;
import com.ps.favbean.Favorite;
import com.ps.favoritelist.FavoriteListView;
import com.ps.helper.WebServices;
import com.ps.helper.WebserviceHelper;
import com.ps.map.MapView;
import com.ps.util.Common;
import com.ps.util.Data;
import com.ps.util.ImageLoader;
import com.ps.util.RequestReceiver;

public class ThumbnailView extends FragmentActivity implements RequestReceiver {

	Button policyBtn, favListBtn, mShopOnlineBtn, mMapBtn, mSortBtn;
	Button assortedBtn, babyBtn, boyBtn, girlBtn, powerCardBtn;
	Spinner mSpinner;
	Context mcontext;
	ImageView mLogoBack;
	String mresult;
	static String mSpinnerText;
	ProgressDialog mProgressDialog;
	public static ListView list;
	public static ViewPagerAdapter adapter;
	int firstVisibleRow, lastVisibleRow;
	boolean isinCorrectPositioned;
	Activity mActivity;
	JSONArray jArray;
	boolean b;
	int index;
	public static ViewPager myPager;
	boolean status;
	int couponArrayLength = 10;
	boolean isLastItem;
	static int couponindex;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.thumbnail_layout);
		
		
		Common.setCategory(1);
		
		mcontext = this;
		Common.mactivity = this;
		Common.globalContext = this;
		b = checkInternetConnection();
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clickListener();
		
		

	}

	@Override
	protected void onStart() {
		super.onStart();
		Common.globalContext = this;
		Common.mactivity = this;
		
		try {
			loadPager();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		updateCategorySelection();
	}

	@SuppressLint("NewApi")
	private void init() throws IOException {
		policyBtn = (Button) findViewById(R.id.policybtn);
		favListBtn = (Button) findViewById(R.id.favlistbtn);
		mLogoBack = (ImageView) findViewById(R.id.logo);
		mSpinner = (Spinner) findViewById(R.id.spinner);
		mShopOnlineBtn = (Button) findViewById(R.id.shoponline);
		mMapBtn = (Button) findViewById(R.id.mapbtn);
		mSortBtn = (Button) findViewById(R.id.button2);

		list = (ListView) findViewById(R.id.list);
		
		assortedBtn = (Button) findViewById(R.id.assortedbtn);
		babyBtn = (Button) findViewById(R.id.babybtn);
		boyBtn = (Button) findViewById(R.id.boybtn);
		girlBtn = (Button) findViewById(R.id.girlbtn);
		powerCardBtn = (Button) findViewById(R.id.powerbtn);
		
		
		assortedBtn.setSelected(true);
		babyBtn.setSelected(false);
		boyBtn.setSelected(false);
		girlBtn.setSelected(false);
		powerCardBtn.setSelected(false);
	
	}

	private void loadWebService() {

		WebserviceHelper helper = new WebserviceHelper(this);
		helper.addParam("action", "getCoupan");
		helper.addParam("coupan", "Yes");
		helper.addParam("category", "0");
		helper.addParam("device_id", Data.DEVICE_ID);
		helper.execute(helper.buildUrl(WebServices.COUPON_URL));
		
	}
	
	void loadPager() {

		adapter = new ViewPagerAdapter(this, Common.couponList1);
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);

	}
	
	void updateCategorySelection()
	{
		int cat = Common.selectedCategory;
		
		assortedBtn.setSelected(false);
		babyBtn.setSelected(false);
		boyBtn.setSelected(false);
		girlBtn.setSelected(false);
		powerCardBtn.setSelected(false);
		
		switch(cat)
		{
			case 1:
				assortedBtn.setSelected(true);
				break;
			case 2:
				babyBtn.setSelected(true);
				break;
			case 3:
				boyBtn.setSelected(true);
				break;
			case 4:
				girlBtn.setSelected(true);
				break;
			case 5:
				powerCardBtn.setSelected(true);
				break;
			default:
				assortedBtn.setSelected(true);
				break;
		}
		
	}
	
	private void clickListener() {

		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WebserviceHelper helper = new WebserviceHelper(ThumbnailView.this);

				helper.addParam("action", "getCoupan");
				helper.addParam("coupan", "Yes");
				helper.addParam("device_id", Data.DEVICE_ID);
				helper.addParam("page", "0");
				helper.addParam("items", String.format("%d", Common.totalCouponCount));
				helper.execute(helper.buildUrl(WebServices.COUPON_URL));
			}
		});

		policyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.gc();
				Intent policy = new Intent(ThumbnailView.this, policy_Activity.class);
				startActivityForResult(policy, CommonUtilities.REQUEST_OTHER);
			}
		});

		favListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.gc();
				Intent favintent = new Intent(ThumbnailView.this,
						FavoriteListView.class);
				startActivityForResult(favintent, CommonUtilities.REQUEST_OTHER);
				

			}
		});

		mShopOnlineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.gc();
				Intent shoponline = new Intent(ThumbnailView.this, StoreOnline.class);
				startActivityForResult(shoponline, CommonUtilities.REQUEST_OTHER);
				
			}
		});

		mMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				System.gc();
				Intent map = new Intent(ThumbnailView.this, MapView.class);
				startActivityForResult(map, CommonUtilities.REQUEST_OTHER);

			}
		});
		
		
		assortedBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(1);
				adapter.notifyDataSetChanged();
				
				assortedBtn.setSelected(true);
				babyBtn.setSelected(false);
				boyBtn.setSelected(false);
				girlBtn.setSelected(false);
				powerCardBtn.setSelected(false);
				
			}
		});
		
		
		babyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(2);
				adapter.notifyDataSetChanged();
				
				assortedBtn.setSelected(false);
				babyBtn.setSelected(true);
				boyBtn.setSelected(false);
				girlBtn.setSelected(false);
				powerCardBtn.setSelected(false);
			}
		});
		
		boyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(3);
				adapter.notifyDataSetChanged();
				assortedBtn.setSelected(false);
				babyBtn.setSelected(false);
				boyBtn.setSelected(true);
				girlBtn.setSelected(false);
				powerCardBtn.setSelected(false);
			}
		});
		
		girlBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(4);
				adapter.notifyDataSetChanged();
				assortedBtn.setSelected(false);
				babyBtn.setSelected(false);
				boyBtn.setSelected(false);
				girlBtn.setSelected(true);
				powerCardBtn.setSelected(false);
			}
		});
		
		powerCardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(5);
				adapter.notifyDataSetChanged();
				assortedBtn.setSelected(false);
				babyBtn.setSelected(false);
				boyBtn.setSelected(false);
				girlBtn.setSelected(false);
				powerCardBtn.setSelected(true);
			}
		});

	}
	
	

	@Override
	public void requestFinished(String result) {
		
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

	public class ViewPagerAdapter extends PagerAdapter{

		Activity activity;
		int mCurrentPage;
		String imgName = "";
		boolean isSendClicked, isFavClicked;
		String impath = "";
		int dbIndex;
		DbHelper db;
		protected ProgressDialog mProgressDialog;
		ImageView leftArrowBtn;
		ImageView RightArrowBtn;
		

		

		public ViewPagerAdapter(Activity act, ArrayList<Favorite> imgArra) {

			activity = act;
		}
		public void showCouponsFrom(int startIndex)
		{
			Intent intent = new Intent (ThumbnailView.this,MainView.class);
			intent.putExtra("startIndex", startIndex);
			
			startActivity(intent);
			
		}
		
		public int getCount() {
			
			return (int)Math.ceil((double)Common.couponList.size() / 6);
		}
		
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View container, final int position) {

			View vi = activity.getLayoutInflater().inflate(R.layout.list_thumbrow,
					null);
			ImageView img1,img2,img3,img4,img5,img6;

			TextView pageNo;

			
			ImageLoader imageLoader;
			imageLoader = new ImageLoader(Common.globalContext);
			leftArrowBtn = (ImageView) vi.findViewById(R.id.leftarrow);
			RightArrowBtn = (ImageView) vi.findViewById(R.id.rightarrow);
			
			img1 = (ImageView) vi.findViewById(R.id.list_image1);
			img2 = (ImageView) vi.findViewById(R.id.list_image2);
			img3 = (ImageView) vi.findViewById(R.id.list_image3);
			img4 = (ImageView) vi.findViewById(R.id.list_image4);
			img5 = (ImageView) vi.findViewById(R.id.list_image5);
			img6 = (ImageView) vi.findViewById(R.id.list_image6);
			
			pageNo = (TextView) vi.findViewById(R.id.pageNoText);
			
				//pageInfo
			int size = Common.couponList.size();
			final int couponPosition = position * 6;
			
		
			
			pageNo.setText(String.format("%d/%d",position+1, (int)Math.ceil((double)size/6)));
			// 17002262
			if(size > couponPosition)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition)
						.getsImgPath(), img1);
				img1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition);
					}
				});
			}
			else
			{
				img1.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back1).setVisibility(View.GONE);
				
			}
			
			if(size > couponPosition + 1)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition+1)
						.getsImgPath(), img2);
				img2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition+1);
					}
				});
			}
			else
			{
				img2.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back2).setVisibility(View.GONE);
			}
			
			if(size > couponPosition + 2)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition+2)
						.getsImgPath(), img3);
				img3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition+2);
					}
				});
			}
			else
			{
				img3.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back3).setVisibility(View.GONE);
			}
			
			if(size > couponPosition + 3)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition+3)
						.getsImgPath(), img4);
				img4.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition+3);
					}
				});
			}
			else
			{
				img4.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back4).setVisibility(View.GONE);
			}
			
			if(size > couponPosition + 4)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition+4)
						.getsImgPath(), img5);
				img5.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition+4);
					}
				});
			}
			else
			{
				img5.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back5).setVisibility(View.GONE);
			}
			
			if(size > couponPosition + 5)
			{
				imageLoader.DisplayImage(Common.couponList.get(couponPosition+5)
						.getsImgPath(), img6);
				img6.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCouponsFrom(couponPosition+5);
					}
				});
			}
			else
			{
				img6.setVisibility(View.GONE);
				vi.findViewById(R.id.image_back6).setVisibility(View.GONE);
			}
			
			
			
			if ((position+1)*6 >= size ) {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.INVISIBLE);
			} else {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			}
			
			if (position == 0) {
				leftArrowBtn.setVisibility(View.INVISIBLE);
			}
			

			leftArrowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					index--;
					myPager.setCurrentItem(position - 1);

				}
			});
			RightArrowBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					index++;
					myPager.setCurrentItem(position + 1);

				}
			});
			
			((ViewPager) container).addView(vi, 0);
			return vi;

		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}


		public boolean isSdPresent() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}


		
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if(requestCode == CommonUtilities.REQUEST_OTHER)
			return;
			
	}

	

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		Intent intent = new Intent(ThumbnailView.this, HomeView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();

	}

	
}
