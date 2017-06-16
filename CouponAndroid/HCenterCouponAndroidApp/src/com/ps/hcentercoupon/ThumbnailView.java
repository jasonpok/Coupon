package com.ps.hcentercoupon;

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
import android.preference.PreferenceManager.OnActivityResultListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.internal.fl;
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
	Activity mActivity;
	JSONArray jArray;
	boolean b;
	int index;
	public static ViewPager myPager;
	boolean status;
	int couponArrayLength = 10;
	boolean isLastItem;
	static int couponindex;
	float firstPoint;
	
	

	

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
		
		switch(Common.selectedCategory){
			case 1:
				myPager.setCurrentItem(Common.cat1StartIndex);
				break;
			case 2:
				myPager.setCurrentItem(Common.cat2StartIndex);
				break;
			case 3:
				myPager.setCurrentItem(Common.cat3StartIndex);
				break;
			case 4:
				myPager.setCurrentItem(Common.cat4StartIndex);
				break;
			case 5:
				myPager.setCurrentItem(Common.cat5StartIndex);
				break;
		}
		
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

		adapter = new ViewPagerAdapter(this, Common.getThumbArrayList());
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
		
		myPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int page) {
				// TODO Auto-generated method stub
				Log.i("onPageSelected:",String.format("%d", page));
				
				if(page < Common.cat2StartIndex)
					Common.setCategory(1);
				else if(page < Common.cat3StartIndex)
					Common.setCategory(2);
				else if(page < Common.cat4StartIndex)
					Common.setCategory(3);
				else if(page < Common.cat5StartIndex)
					Common.setCategory(4);
				else
					Common.setCategory(5);
				updateCategorySelection();
				
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			/*	Log.i("onPageScrollStateChanged:",String.format("%d", state));
				
				if (state == ViewPager.SCROLL_STATE_IDLE) {
					
					if((myPager.getCurrentItem() == myPager.getAdapter().getCount()-1) ) 						
					{
						if  (Common.selectedCategory != 5)
						{
							Common.setCategory(Common.selectedCategory+1);
							updateCategorySelection();
							adapter.notifyDataSetChanged();
							myPager.setCurrentItem(1);
						}
						else
						{
							myPager.setCurrentItem(myPager.getCurrentItem()-1);
						}
					}
					else if((myPager.getCurrentItem() == 0)) 						
					{
						 if (Common.selectedCategory != 1)
						 {
							Common.setCategory(Common.selectedCategory-1);
							updateCategorySelection();
							adapter.notifyDataSetChanged();
							myPager.setCurrentItem(adapter.getCount()-2);
						 }
						 else
							 myPager.setCurrentItem(1);
					}
					
			    }*/
			
			}
		});
		
		/*myPager.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					firstPoint = event.getX();
					Log.i("onPageSelected:",String.format("down:%f",firstPoint));
					
					event.setLocation(firstPoint, event.getY());
					myPager.getChildAt(0).dispatchTouchEvent(event);
					return false;
					
				}else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					firstPoint = 0;
					Log.i("onPageSelected:",String.format("up:%f",firstPoint));
					
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE)
				{
					if(firstPoint == 0)
						firstPoint = event.getX();
					float offset = event.getX() - firstPoint;
					Log.i("onPageSelected:",String.format("(firstPoint: %f)",firstPoint));
					Log.i("onPageSelected:",String.format("(%f-%f)", event.getX(),firstPoint-offset));
					event.setLocation(firstPoint-offset, event.getY());
					Log.i("onPageSelected:",String.format("currentItem:%d",myPager.getCurrentItem()));
					myPager.getChildAt(0).dispatchTouchEvent(event);
					return false;
				}
				return false;
				 
			}
		});
		*/

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
				//Common.setCategory(1);
				//adapter.notifyDataSetChanged();
				
				myPager.setCurrentItem(Common.cat1StartIndex);
				
				
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
				//adapter.notifyDataSetChanged();
				myPager.setCurrentItem(Common.cat2StartIndex);
				updateCategorySelection();
			}
		});
		
		boyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(3);
				//adapter.notifyDataSetChanged();
				myPager.setCurrentItem(Common.cat3StartIndex);
				updateCategorySelection();
			}
		});
		
		girlBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(4);
				//adapter.notifyDataSetChanged();
				myPager.setCurrentItem(Common.cat4StartIndex);
				updateCategorySelection();
			}
		});
		
		powerCardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(5);
				//adapter.notifyDataSetChanged();
				myPager.setCurrentItem(Common.cat5StartIndex);
				updateCategorySelection();
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
			
			switch(Common.selectedCategory)
			{
				case 1:
					startIndex -= 6*Common.cat1StartIndex;
					break;
				case 2:
					startIndex -= 6*Common.cat2StartIndex;
					break;
				case 3:
					startIndex -= 6*Common.cat3StartIndex;
					break;
				case 4:
					startIndex -= 6*Common.cat4StartIndex;
					break;
				case 5:
					startIndex -= 6*Common.cat5StartIndex;
					break;
			}
			intent.putExtra("startIndex", startIndex);
			startActivity(intent);
			
		}
		
		public int getCount() {
			
			return (int)Math.ceil((double)Common.couponThumbList.size() / 6);
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
			int size = Common.couponThumbList.size();
			final int couponPosition = position * 6;
			// 17002262
			if(Common.couponThumbList.get(couponPosition) != null)
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition)
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
			
			if(Common.couponThumbList.get(couponPosition+1) != null)
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition+1)
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
			
			if(Common.couponThumbList.get(couponPosition+2) !=null )
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition+2)
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
			
			if(Common.couponThumbList.get(couponPosition+3) !=null )
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition+3)
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
			
			if(Common.couponThumbList.get(couponPosition+4) !=null )
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition+4)
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
			
			if(Common.couponThumbList.get(couponPosition+5) !=null )
			{
				imageLoader.DisplayImage(Common.couponThumbList.get(couponPosition+5)
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
				RightArrowBtn.setVisibility(View.GONE);
			} else if(position == 0) {
				leftArrowBtn.setVisibility(View.GONE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			} else
			{
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			}
			
			leftArrowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					index--;
					myPager.setCurrentItem(myPager.getCurrentItem()-1);
				}
			});
			RightArrowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					index++;
					myPager.setCurrentItem(myPager.getCurrentItem() + 1);
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
