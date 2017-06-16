package com.ps.coupon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.ps.db.DbHelper;
import com.ps.favbean.Favorite;
import com.ps.favoritelist.FavoriteListView;
import com.ps.helper.ResponceParser;
import com.ps.helper.WebServices;
import com.ps.helper.WebserviceHelper;
import com.ps.map.MapView;
import com.ps.util.Common;
import com.ps.util.Data;
import com.ps.util.ImageLoader;
import com.ps.util.RequestReceiver;

public class MainView extends FragmentActivity implements RequestReceiver {

	Button policyBtn, favListBtn, mShopOnlineBtn, mshareBtn, mMapBtn, mSortBtn;
	Button assortedBtn, babyBtn, boyBtn, girlBtn, powerCardBtn;
	ImageView backBtn;
	
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
	int startIndex;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		
		
		startIndex = getIntent().getIntExtra("startIndex", 0);
		
		
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
		updateCategorySelection();
		Common.globalContext = this;
		Common.mactivity = this;
		try {
			loadPager();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	@SuppressLint("NewApi")
	private void init() throws IOException {
		backBtn = (ImageView) findViewById(R.id.btn_back);
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

		/*if (b) {

			Async sync = new Async();
			sync.execute(WebServices.COUPON_SHABATURL);

		} else {
			Toast.makeText(Common.globalContext,
					"Internet Connection Not Present", Toast.LENGTH_SHORT)
					.show();
		}
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

	private void loadWebService() {

			
			WebserviceHelper helper = new WebserviceHelper(this);

			helper.addParam("action", "getCoupan");
			helper.addParam("coupan", "Yes");
			helper.addParam("version", "v2");
			helper.addParam("device_id", Data.DEVICE_ID);
			helper.execute(helper.buildUrl(WebServices.COUPON_URL));
	}
	
	void loadPager() {

		adapter = new ViewPagerAdapter(this, Common.couponList1);
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(startIndex);

	}
	
	
	private void clickListener() {

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WebserviceHelper helper = new WebserviceHelper(MainView.this);

				helper.addParam("action", "getCoupan");
				helper.addParam("coupan", "Yes");
				helper.addParam("version", "v2");
				helper.addParam("device_id", Data.DEVICE_ID);
				helper.addParam("page", "0");
				helper.addParam("items", String.format("%d", Common.totalCouponCount));
				helper.execute(helper.buildUrl(WebServices.COUPON_URL));
				
			}
		});
		
		
		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WebserviceHelper helper = new WebserviceHelper(MainView.this);

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
				Intent policy = new Intent(MainView.this, policy_Activity.class);
				startActivityForResult(policy, CommonUtilities.REQUEST_OTHER);
			}
		});

		favListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.gc();
				Intent favintent = new Intent(MainView.this,
						FavoriteListView.class);
				startActivityForResult(favintent, CommonUtilities.REQUEST_OTHER);
				

			}
		});

		mShopOnlineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.gc();
				Intent shoponline = new Intent(MainView.this, StoreOnline.class);
				startActivityForResult(shoponline, CommonUtilities.REQUEST_OTHER);
				
			}
		});

		mMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				System.gc();
				Intent map = new Intent(MainView.this, MapView.class);
				startActivityForResult(map, CommonUtilities.REQUEST_OTHER);

			}
		});

		
		assortedBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(1);
				//adapter.notifyDataSetChanged();
				finish();
			}
		});
		
		
		babyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(2);
				//adapter.notifyDataSetChanged();
				finish();
			}
		});
		
		boyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(3);
				//adapter.notifyDataSetChanged();
				finish();
			}
		});
		
		girlBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(4);
				//adapter.notifyDataSetChanged();
				finish();
			}
		});
		
		powerCardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.setCategory(5);
				//adapter.notifyDataSetChanged();
				finish();
			}
		});
	}
	
	

	@Override
	public void requestFinished(String result) {

		mresult = result;
		
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute();

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

	public class ViewPagerAdapter extends PagerAdapter {

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

		public int getCount() {
			
				return (int)Math.ceil((double)Common.couponList.size());
			
		}
		
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View container, final int position) {

			View vi = activity.getLayoutInflater().inflate(R.layout.list_row,
					null);
			ImageView img;

			TextView text, couponNumber,pageNo;

			Button mAddToFav, mShareBtn, mSendBtn, mLikeBtn;
			ImageLoader imageLoader;
			imageLoader = new ImageLoader(Common.globalContext);
			leftArrowBtn = (ImageView) vi.findViewById(R.id.leftarrow);
			RightArrowBtn = (ImageView) vi.findViewById(R.id.rightarrow);
			img = (ImageView) vi.findViewById(R.id.list_image);
			text = (TextView) vi.findViewById(R.id.usercomment);
			pageNo = (TextView) vi.findViewById(R.id.pageNoText);
			Common.userlike = (TextView) vi.findViewById(R.id.liketext);
			mAddToFav = (Button) vi.findViewById(R.id.addtofav);
			mShareBtn = (Button) vi.findViewById(R.id.share);
			mSendBtn = (Button) vi.findViewById(R.id.send);
			mLikeBtn = (Button) vi.findViewById(R.id.like);
			
			
			
		
			int size = Common.couponList.size();
			

			couponNumber = (TextView) vi.findViewById(R.id.couponnumber);
			
			//pageInfo
			pageNo.setText(String.format("%d/%d",position+1, size));

			// fill data
			text.setText(Common.couponList.get(position).getsComment());
			// 17002262
			couponNumber.setText(Common.couponList.get(position)
					.getiCouponNumber());
			imageLoader.DisplayImage(Common.couponList.get(position)
					.getsImgPath(), img);
			Common.userlike.setText(Integer.toString(Common.couponList.get(
					position).getiTotalCount()));

			if (size == 1) {
				leftArrowBtn.setVisibility(View.INVISIBLE);
				RightArrowBtn.setVisibility(View.INVISIBLE);
			} else if (position == 0) {
				leftArrowBtn.setVisibility(View.INVISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			} else if (position ==size - 1) {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.INVISIBLE);
			} else {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			}

			// myPager.setOnPageChangeListener(new OnPageChangeListener() {
			//
			// @Override
			// public void onPageSelected(int arg0) {
			//
			// couponindex++;
			//
			// if (couponindex % 9 == 0) {
			// isLastItem = true;
			// DownloadWebPageTask task = new DownloadWebPageTask();
			// task.execute();
			// }
			//
			// }
			//
			// @Override
			// public void onPageScrolled(int arg0, float arg1, int arg2) {
			//
			// }
			//
			// @Override
			// public void onPageScrollStateChanged(int arg0) {
			//
			// }
			// });

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

			mSendBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dbIndex = position;
					isSendClicked = true;
					imgName = Common.couponList.get(dbIndex)
							.getsCouponShareImg();
					imgName = imgName.substring(imgName.lastIndexOf("/") + 1,
							imgName.lastIndexOf("."));

					if (impath.contains(imgName)) {

						sendMail(impath);
					} else {
						UserFunctions u = new UserFunctions();
						u.setAction(Data.SEND_MAIL);
						u.execute(Common.couponList.get(position)
								.getsCouponShareImg());
					}

				}
			});

			mLikeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Common.isFavorMainListClicked = false;
					Data.likeValues = String.valueOf(Common.couponList.get(
							position).getId());
					Common.Index = position;

					ResponceParser res = new ResponceParser();
					res.setAction(Data.LIKE);
					res.execute(WebServices.LIKE_URL);

				}
			});

			mShareBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Common.isFavFacebookClicked = false;
					Common.IMAGE_PATH = Common.couponList.get(position)
							.getsCouponShareImg();

					Common.IMAGE_DESCRIPTION =Common.couponList.get(position)
							.getsComment();

					callFacebook();

				}
			});

			mAddToFav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					boolean isSDCardAvailable = isSdPresent();
					if (isSDCardAvailable) {

						isFavClicked = true;
						dbIndex = position;

						imgName = Common.couponList.get(position).getsImgPath();
						imgName = imgName.substring(
								imgName.lastIndexOf("/") + 1,
								imgName.lastIndexOf("."));

						UserFunctions u = new UserFunctions();
						u.setAction(Data.ADDTOFAV);
						u.execute(Common.couponList.get(position).getsImgPath());
					} else {
						// Sorry
						Toast.makeText(Common.globalContext, "insert sd card",
								Toast.LENGTH_SHORT).show();
					}

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

		public class UserFunctions extends AsyncTask<String, Integer, String> {

			public int action;

			@Override
			protected String doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoOutput(true);
					urlConnection.connect();

					File sdCardDirectory = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "Images");

					if (!sdCardDirectory.exists()) {
						sdCardDirectory.mkdirs();
					}
					String filename = imgName + ".jpg";
					impath = sdCardDirectory.getAbsolutePath() + "/" + filename;

					File file = new File(sdCardDirectory, filename);

					FileOutputStream fileOutput = new FileOutputStream(file);
					InputStream inputStream = urlConnection.getInputStream();

					byte[] buffer = new byte[1024 * 1024];
					int bufferLength = 0;
					while ((bufferLength = inputStream.read(buffer)) != -1) {
						fileOutput.write(buffer, 0, bufferLength);

					}

					fileOutput.close();

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();

				mProgressDialog = new ProgressDialog(Common.mactivity);
				mProgressDialog.setMessage("Wait..");

				mProgressDialog.setCancelable(false);
				try {
					if (!Common.mactivity.isFinishing()) {
						mProgressDialog.show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onPostExecute(String result) {

				super.onPostExecute(result);
				try {
					mProgressDialog.dismiss();
				} catch (Exception e) {
					// TODO: handle exception
				}

				switch (action) {
				case Data.ADDTOFAV:

					DbHelper db = new DbHelper(Common.globalContext);

					db.addInfo(new Favorite(Common.couponList.get(dbIndex)
							.getId(), 1, 1, Common.couponList.get(dbIndex)
							.getsCouponName(), Common.couponList.get(dbIndex)
							.getfDate(), Common.couponList.get(dbIndex)
							.gettDate(), impath, Common.couponList.get(dbIndex)
							.getsComment(), Common.couponList.get(dbIndex)
							.getiTotalCount(), Common.couponList.get(dbIndex)
							.getsImageUrl(), Common.couponList.get(dbIndex)
							.getiCouponNumber(), Common.couponList.get(dbIndex)
							.getsCouponShareImg()));

					break;
				case Data.SEND_MAIL:

					sendMail(impath);
					break;
				default:

					break;
				}

			}

			public void setAction(int action) {
				this.action = action;
			}
		}

		public void sendMail(String path) {

			String link_val = "https://play.google.com/store/apps/details?id=com.ps.coupon";
			String body = "<a href=\"" + link_val + "\">" + link_val + "</a>";

			String link_val1 = "https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=915787141&mt=8";
			String body1 = "<a href=\"" + link_val1 + "\">" + link_val1
					+ "</a>";

			String androidtext = "להורדה למכשירי   Androidלחצו כאן:";
			String iostext = "להורדה למכשירי iPhone לחצו כאן:";
			
			
			PackageManager pm = getPackageManager();
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);

			for (int i = 0; i < resInfo.size(); i++) {
				// Extract the label, append it, and repackage it in a
				// LabeledIntent
				ResolveInfo ri = resInfo.get(i);
				String packageName = ri.activityInfo.packageName;
				if (packageName.contains("gmail") | packageName.contains(".gm")) {
					emailIntent.setPackage(packageName);

				}

			}

			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					"רוצים גם אתם ליהנות מהקופונים השווים בישראל?" + "\n\n"
							+ "הורידו את אפליקציית טויס אר אס!" + "\n\n"
							+ androidtext + "\n\n" + Html.fromHtml(body) + "\n"
							+ iostext + "\n\n" + Html.fromHtml(body1));
			emailIntent.setType("image/png");
			Uri myUri = Uri.parse("file://" + path);
			emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
			Common.globalContext.startActivity(Intent.createChooser(
					emailIntent, "Send mail..."));
		}

		public boolean isSdPresent() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}
	}

	public void callFacebook() {

		Session currentSession = Session.getActiveSession();
		if (currentSession == null || currentSession.getState().isClosed()) {
			Session session = new Session.Builder(Common.mactivity).build();
			Session.setActiveSession(session);
			currentSession = session;
		}

		if (currentSession.isOpened()) {
			// Do whatever u want. User has logged in
			Session.openActiveSession(Common.mactivity, true,
					new Session.StatusCallback() {

						// callback when session changes state
						@Override
						public void call(Session session, SessionState state,
								Exception exception) {
							if (session.isOpened()) {
								share(session);
							}
						}
					});
		} else if (!currentSession.isOpened()) {
			// Ask for username and password
			Session.OpenRequest op = new Session.OpenRequest(Common.mactivity);
			op.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			op.setCallback(null);
			List<String> permissions = new ArrayList<String>();
			permissions.add("publish_stream");
			permissions.add("user_likes");
			permissions.add("email");
			permissions.add("user_birthday");
			op.setPermissions(permissions);
			Session session = new Session.Builder(Common.mactivity).build();
			Session.setActiveSession(session);
			session.openForPublish(op);
		}

	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mcontext);
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

			MainView.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (b) {
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
					} else {
						Toast.makeText(Common.globalContext,
								"Internet Connection Not Present",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

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
			loadPager();

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if(requestCode == CommonUtilities.REQUEST_OTHER)
			return;
		
		
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		
		
		if (Session.getActiveSession() != null)
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
		Session currentSession = Session.getActiveSession();
		if (currentSession == null || currentSession.getState().isClosed()) {
			Session session = new Session.Builder(this).build();
			Session.setActiveSession(session);
			currentSession = session;
		}

		if (currentSession.isOpened()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					if (state.isOpened()) {
						share(session);
					}
				}
			});
		}
		
		
		
	}

	// function for share items data...on facebook
	private static void share(Session session) {

		String description1 = "רוצים גם אתם ליהנות מהקופונים השווים בישראל?";
		String description2 = "הורידו את אפליקציית טויס אר אס!";

		String androidtextLink = "להורדה למכשירי   Androidלחצו כאן:";
		String iostextLink = "להורדה למכשירי iPhone לחצו כאן:";

		String link_val = "\n https://play.google.com/store/apps/details?id=com.ps.coupon";
		String body = "<a href=\"" + link_val + "\">" + link_val + "</a> \n";

		String link_val1 = "\n https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=915787141&mt=8";
		String body1 = "<a href=\"" + link_val1 + "\">" + link_val1 + "</a> \n";

		Bundle params = new Bundle();
		params.putString("name", "Name" + "\n" + "name");

		// StringBuilder messageData = new StringBuilder("title").append('\n')
		// .append("message").append('\n').append("description");

		params.putString(
				"description",
				description1 + description2 + androidtextLink
						+ Html.fromHtml(body) + iostextLink
						+ Html.fromHtml(body1)/* +""+messageData */);

		params.putString("picture", Common.IMAGE_PATH);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				Common.mactivity, session, params))
				.setOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(Common.globalContext,
										"coupon posted", Toast.LENGTH_SHORT)
										.show();
								// finish();
								// do some stuff
							} else {// User clicked the Cancel button
								Toast.makeText(Common.globalContext,
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
								// finish();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(Common.globalContext,
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
							// finish();
						} else {
							// Generic, ex: network error
							Toast.makeText(Common.globalContext,
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
							// finish();
						}
					}

				}).setFrom("").build();
		feedDialog.show();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		Intent intent = new Intent(MainView.this, HomeView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();

	}

	public class Async extends AsyncTask<String, Integer, String> {

		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
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
		}

		@Override
		protected String doInBackground(String... params) {

			URL url = null;
			try {
				url = new URL(WebServices.COUPON_COUNT_URL);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			InputStream i = null;

			try {
				i = url.openStream();
			} catch (UnknownHostException ex) {

				status = false;

				System.out.println("THIS URL IS NOT VALID");
			} catch (Exception e) {
				status = false;
				System.out.println("exception " + e.getMessage());
			}

			if (i != null) {
				status = true;
				System.out.println("Its working");
			}

			return null;
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
			
			
			loadWebService();
		}

	}

}
