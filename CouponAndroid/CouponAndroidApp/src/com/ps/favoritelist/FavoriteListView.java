package com.ps.favoritelist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.ps.coupon.CommonUtilities;
import com.ps.coupon.MainView;
import com.ps.coupon.R;
import com.ps.coupon.StoreOnline;
import com.ps.coupon.policy_Activity;
import com.ps.db.DbHelper;
import com.ps.favbean.Favorite;
import com.ps.helper.JSONParser;
import com.ps.helper.ResponceParser;
import com.ps.helper.WebServices;
import com.ps.map.MapView;
import com.ps.util.Common;
import com.ps.util.Data;

public class FavoriteListView extends FragmentActivity {

	Button mPolicyBtn, mShopOnlineBtn, mMapBtn, mCloseBtn;
	ImageView mLogoBack;
	public static ListView list;
	EditText edit;
	// public static FavoriteAdaptor adapter;
	Context mcontext;
	protected ProgressDialog mProgressDialog;
	String imgName = "";
	String impath = "";
	public static MyFragmentPagerAdapter pagerAdapter;
	public static ViewPager pager;
	public static ViewPager myPager;
	public static ViewPagerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.favoritelist_layout);

		mcontext = this;
		Common.mactivity = this;
		Common.globalContext = this;
		init();
		clickListener();
		loadImage();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Common.globalContext = this;
		Common.mactivity = this;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Common.mactivity = this;
	}

	void init() {

		mPolicyBtn = (Button) findViewById(R.id.policybtn);
		mShopOnlineBtn = (Button) findViewById(R.id.shoponline);
		mMapBtn = (Button) findViewById(R.id.mapbtn);
		mCloseBtn = (Button) findViewById(R.id.close);
		list = (ListView) findViewById(R.id.list);
		mLogoBack = (ImageView) findViewById(R.id.logo);

	}

	void loadPager() {

		adapter = new ViewPagerAdapter(this, Common.favList);
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);

	}

	void clickListener() {

		mPolicyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent policy = new Intent(FavoriteListView.this,
						policy_Activity.class);
				
				startActivityForResult(policy,CommonUtilities.REQUEST_OTHER);
				finish();
			}
		});

		mShopOnlineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent shoponline = new Intent(FavoriteListView.this,
						StoreOnline.class);
				
				startActivityForResult(shoponline,CommonUtilities.REQUEST_OTHER);
			}
		});
		mMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent map = new Intent(FavoriteListView.this, MapView.class);
				startActivityForResult(map,CommonUtilities.REQUEST_OTHER);
			}
		});
		mLogoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*Intent back = new Intent(FavoriteListView.this, MainView.class);
				back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(back);*/
				setResult(CommonUtilities.BACK_TO_MAIN);
				finish();
			}
		});
		
		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		
		
		if(requestCode == CommonUtilities.REQUEST_OTHER && resultCode == CommonUtilities.BACK_TO_MAIN)
		{
			setResult(CommonUtilities.BACK_TO_MAIN);
			finish();
			return;
		}
		
		
		// /////////facebook............
		Session.getActiveSession().onActivityResult(FavoriteListView.this,
				requestCode, resultCode, intent);
		if (Session.getActiveSession() != null)
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, intent);
		Session currentSession = Session.getActiveSession();
		if (currentSession == null || currentSession.getState().isClosed()) {
			Session session = new Session.Builder(this).build();
			Session.setActiveSession(session);
			currentSession = session;
		}
		if (currentSession.isOpened()) {
			// Do whatever u want. User has logged in
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				// callback when session changes state
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					if (session.isOpened()) {
						share(session);
					}
				}
			});
		}

	}

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

	void loadImage() {
		
		
		
		new AsyncTask<Void, Void, Void>(){
			
			DbHelper db = new DbHelper(mcontext);
			List<Favorite> getList = new ArrayList<Favorite>();
			
			protected void onPreExecute() {
				mProgressDialog = new ProgressDialog(Common.mactivity);
				mProgressDialog.setMessage("Wait..");

				mProgressDialog.setCancelable(false);
				if (!Common.mactivity.isFinishing()) {
					mProgressDialog.show();
				}

				
			};
			

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				getList = db.getAllContacts();
				JSONParser parser = new JSONParser();
				try {
					
					
					String response = ((JSONObject) parser.makeHttpRequest(WebServices.COUPON_STATUS_URL + "=" + Common.favCouponIds, "GET",null).get(0)).getString("status");
					
					String[] status = response.split(",");
					
					
					int removed = 0;
					for(int i=0;i<status.length-1;i++)
					{
						
						if(status[i].equalsIgnoreCase("0"))
						{
							db.deleteCoupon(getList.get(i-removed));
							getList.remove(i-removed);
							removed++;
						}
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return null;
			}
			
			protected void onPostExecute(Void result) 
			{
				if (Common.favList.size() != 0) {
					Common.favList = null;
					Common.favList = new ArrayList<Favorite>();
				}

				for (int i = 0; i < getList.size(); i++) {
					Favorite fav = new Favorite();
					fav.setId(getList.get(i).getId());
					fav.setsImgPath(getList.get(i).getsImgPath());
					fav.setsImageUrl(getList.get(i).getsImageUrl());
					fav.setsComment(getList.get(i).getsComment());
					fav.setiTotalCount(getList.get(i).getiTotalCount());
					fav.setiCouponNumber(getList.get(i).getiCouponNumber());
					fav.setsCouponShareImg(getList.get(i).getsCouponShareImg());
					Common.favList.add(fav);
				}
				

				// adapter = new FavoriteAdaptor(FavoriteListView.this, Common.favList);
				// list.setAdapter(adapter);
				loadPager();
				
				
				mProgressDialog.dismiss();
			};
			
		}.execute();
		
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
			return Common.favList.size();
		}

		@Override
		public Object instantiateItem(View container, final int position) {

			View vi = activity.getLayoutInflater().inflate(
					R.layout.fav_list_row, null);
			Button mRemoveToList, mSendMail, mShare, mLikeBtn;
			TextView userlike, couponNumber,pageNo;
			ImageView img;
			TextView comment;
			db = new DbHelper(Common.globalContext);
			img = (ImageView) vi.findViewById(R.id.list_image);
			comment = (TextView) vi.findViewById(R.id.usercomment);
			userlike = (TextView) vi.findViewById(R.id.liketext);
			pageNo = (TextView) vi.findViewById(R.id.pageNoText);
			mRemoveToList = (Button) vi.findViewById(R.id.removetofav);
			mSendMail = (Button) vi.findViewById(R.id.send);
			mShare = (Button) vi.findViewById(R.id.share);
			mLikeBtn = (Button) vi.findViewById(R.id.like);
			leftArrowBtn = (ImageView) vi.findViewById(R.id.leftarrow);
			RightArrowBtn = (ImageView) vi.findViewById(R.id.rightarrow);
			couponNumber = (TextView) vi.findViewById(R.id.couponnumber);
			
			pageNo.setText(String.format("%d/%d",position+1,getCount()));
			
			
			
			
			

			if (Common.favList.size() == 1) {
				leftArrowBtn.setVisibility(View.INVISIBLE);
				RightArrowBtn.setVisibility(View.INVISIBLE);
			} else if (position == 0) {
				leftArrowBtn.setVisibility(View.INVISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			} else if (position == Common.favList.size() - 1) {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.INVISIBLE);
			} else {
				leftArrowBtn.setVisibility(View.VISIBLE);
				RightArrowBtn.setVisibility(View.VISIBLE);
			}

			leftArrowBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					myPager.setCurrentItem(position - 1);

				}
			});
			RightArrowBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					myPager.setCurrentItem(position + 1);

				}
			});

			mLikeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Common.isFavorMainListClicked = true;
					Data.likeValues = String.valueOf(Common.favList.get(
							position).getId());
					Common.Index = position;
					ResponceParser res = new ResponceParser();
					res.setAction(Data.LIKE);
					res.execute(WebServices.LIKE_URL);

				}
			});

			mShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Common.IMAGE_PATH = Common.favList.get(position)
							.getsCouponShareImg();
					Common.IMAGE_DESCRIPTION = Common.favList.get(position)
							.getsComment();

					callFacebook();
				}
			});

			try {
				Bitmap bitmap = BitmapFactory.decodeFile(Common.favList.get(
						position).getsImgPath());
				img.setImageBitmap(bitmap);

				userlike.setText(Integer.toString(Common.favList.get(position)
						.getiTotalCount()));
				comment.setText(Common.favList.get(position).getsComment());

				couponNumber.setText(Common.favList.get(position)
						.getiCouponNumber());
			} catch (Exception e) {
				// TODO: handle exception
			}

			mSendMail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dbIndex = position;

					imgName = Common.favList.get(position).getsCouponShareImg();
					imgName = imgName.substring(imgName.lastIndexOf("/") + 1,
							imgName.lastIndexOf("."));
					UserFunctions1 u = new UserFunctions1();
					u.setAction(Data.SEND_MAIL);
					u.execute(Common.favList.get(position).getsCouponShareImg());

				}
			});

			// click listener
			mRemoveToList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Favorite fav = new Favorite();
					fav.setId(Common.favList.get(position).getId());
					db.deleteCoupon(fav);

					Common.favList.remove(position);

					myPager.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					if (Common.favList.size() == 1) {
						leftArrowBtn.setVisibility(View.INVISIBLE);
						RightArrowBtn.setVisibility(View.INVISIBLE);
					}

					// FavoriteListView.adapter.notifyDataSetChanged();

				}
			});

			((ViewPager) container).addView(vi, 0);
			return vi;

		}

		void callFacebook() {

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
							public void call(Session session,
									SessionState state, Exception exception) {
								if (session.isOpened()) {
									share(session);
								}
							}
						});
			} else if (!currentSession.isOpened()) {
				// Ask for username and password
				Session.OpenRequest op = new Session.OpenRequest(
						Common.mactivity);
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

		@SuppressWarnings("unused")
		public void sendMail(String path, int i) {

			String link_val = "https://play.google.com/store/apps/details?id=com.ps.coupon";
			String body = "<a href=\"" + link_val + "\">" + link_val+ "</a>";
			
			String link_val1 = "https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=915787141&mt=8";
			String body1 = "<a href=\"" + link_val1 + "\">" + link_val1+ "</a>";
			
			String androidtext = "×œ×”×•×¨×“×” ×œ×ž×›×©×™×¨×™   Android×œ×—×¦×• ×›×�×Ÿ:";
			String iostext = "×œ×”×•×¨×“×” ×œ×ž×›×©×™×¨×™ iPhone ×œ×—×¦×• ×›×�×Ÿ:";
			
			Resources resources = getResources();
			 PackageManager pm = getPackageManager();
			    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
			    sendIntent.setType("text/plain");
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			 Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

			    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
			    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
			    for (int k = 0; k < resInfo.size(); k++) {
			        // Extract the label, append it, and repackage it in a LabeledIntent
			        ResolveInfo ri = resInfo.get(k);
			        String packageName = ri.activityInfo.packageName;
			        if(packageName.contains("gmail")|packageName.contains(".gm")) {
			            emailIntent.setPackage(packageName);
			            
			        }
			    }			
			
			
			
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "" });
			emailIntent
					.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
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

		class UserFunctions1 extends AsyncTask<String, Integer, String> {

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

					byte[] buffer = new byte[1024];
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
				if (!Common.mactivity.isFinishing()) {
					mProgressDialog.show();
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

				case Data.SEND_MAIL:

					sendMail(impath, dbIndex);
					break;
				default:

					break;
				}

			}

			public void setAction(int action) {
				this.action = action;
			}
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

		public void sendMail(String path) {

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "" });
			emailIntent
					.putExtra(android.content.Intent.EXTRA_SUBJECT, "Coupon");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					Common.favList.get(dbIndex).getsComment());
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

	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		final int PAGE_COUNT = Common.favList.size();

		/** Constructor of the class */
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem(int arg0) {

			MyFragment myFragment = new MyFragment();
			Bundle data = new Bundle();
			data.putInt("current_page", arg0 + 1);
			myFragment.setArguments(data);

			return myFragment;
		}

		/** Returns the number of pages */
		@Override
		public int getCount() {

			return PAGE_COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return "Page #" + (position + 1);
		}

	}
	
	

	public static class MyFragment extends Fragment {

		int mCurrentPage;
		String imgName = "";
		boolean isSendClicked, isFavClicked;
		String impath = "";
		int dbIndex;
		DbHelper db;
		protected ProgressDialog mProgressDialog;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Common.isFavorMainListClicked = true;
			/** Getting the arguments to the Bundle object */
			Bundle data = getArguments();
			db = new DbHelper(Common.globalContext);
			/** Getting integer data of the key current_page from the bundle */
			mCurrentPage = data.getInt("current_page", 0);

		}

		public MyFragment() {

		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View vi = inflater.inflate(R.layout.fav_list_row, container, false);
			Button mRemoveToList, mSendMail, mShare, mLikeBtn;
			TextView userlike, couponNumber;
			ImageView img;
			TextView comment;
			db = new DbHelper(Common.globalContext);
			img = (ImageView) vi.findViewById(R.id.list_image);
			comment = (TextView) vi.findViewById(R.id.usercomment);
			userlike = (TextView) vi.findViewById(R.id.liketext);
			mRemoveToList = (Button) vi.findViewById(R.id.removetofav);
			mSendMail = (Button) vi.findViewById(R.id.send);
			mShare = (Button) vi.findViewById(R.id.share);
			mLikeBtn = (Button) vi.findViewById(R.id.like);
			couponNumber = (TextView) vi.findViewById(R.id.couponnumber);

			mLikeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Common.isFavorMainListClicked = true;
					Data.likeValues = String.valueOf(Common.favList.get(
							mCurrentPage - 1).getId());
					Common.Index = mCurrentPage - 1;
					ResponceParser res = new ResponceParser();
					res.setAction(Data.LIKE);
					res.execute(WebServices.LIKE_URL);

				}
			});

			mShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Common.IMAGE_PATH = Common.favList.get(mCurrentPage - 1)
							.getsCouponShareImg();
					Common.IMAGE_DESCRIPTION = Common.favList.get(
							mCurrentPage - 1).getsComment();

				}
			});

			try {
				Bitmap bitmap = BitmapFactory.decodeFile(Common.favList.get(
						mCurrentPage - 1).getsImgPath());
				img.setImageBitmap(bitmap);

				userlike.setText(Integer.toString(Common.favList.get(
						mCurrentPage - 1).getiTotalCount()));
				comment.setText(Common.favList.get(mCurrentPage - 1)
						.getsComment());

				couponNumber.setText(Common.favList.get(mCurrentPage - 1)
						.getiCouponNumber());
			} catch (Exception e) {
				// TODO: handle exception
			}

			mSendMail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dbIndex = mCurrentPage - 1;

					imgName = Common.favList.get(mCurrentPage - 1)
							.getsCouponShareImg();
					imgName = imgName.substring(imgName.lastIndexOf("/") + 1,
							imgName.lastIndexOf("."));
					UserFunctions1 u = new UserFunctions1();
					u.setAction(Data.SEND_MAIL);
					u.execute(Common.favList.get(mCurrentPage - 1)
							.getsCouponShareImg());

				}
			});

			// click listener
			mRemoveToList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Favorite fav = new Favorite();
					fav.setId(Common.favList.get(mCurrentPage - 1).getId());
					db.deleteCoupon(fav);

					Common.favList.remove(mCurrentPage - 1);

					FavoriteListView.pager
							.setAdapter(FavoriteListView.pagerAdapter);
					FavoriteListView.pagerAdapter.notifyDataSetChanged();

					// FavoriteListView.adapter.notifyDataSetChanged();

				}
			});

			return vi;
		}
		
		
		
		

		class UserFunctions1 extends AsyncTask<String, Integer, String> {

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

					byte[] buffer = new byte[1024];
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
				if (!Common.mactivity.isFinishing()) {
					mProgressDialog.show();
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

				case Data.SEND_MAIL:

					sendMail(impath, dbIndex);
					break;
				default:

					break;
				}

			}

			public void setAction(int action) {
				this.action = action;
			}
		}

		public void sendMail(String path, int i) {

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "" });
			emailIntent
					.putExtra(android.content.Intent.EXTRA_SUBJECT, "Coupon");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,/* "test" */
					Common.favList.get(i).getsComment());
			emailIntent.setType("image/png");
			Uri myUri = Uri.parse("file://" + path);
			emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
			Common.globalContext.startActivity(Intent.createChooser(
					emailIntent, "Send mail..."));
		}

	}
	
	

	/*
	 * public class FavoriteAdaptor extends BaseAdapter {
	 * 
	 * private Activity activity; private ArrayList<Favorite> data; private
	 * LayoutInflater inflater = null; public ImageLoader imageLoader; DbHelper
	 * db; ImageView img; TextView comment; int dbIndex;
	 * 
	 * public FavoriteAdaptor(Activity a, ArrayList<Favorite> d) { activity = a;
	 * data = d; inflater = (LayoutInflater) activity
	 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE); imageLoader = new
	 * ImageLoader(activity.getApplicationContext()); db = new
	 * DbHelper(Common.globalContext); Common.isFavorMainListClicked = true; }
	 * 
	 * public FavoriteAdaptor(Activity a) { activity = a; db = new
	 * DbHelper(Common.globalContext); Common.isFavorMainListClicked = true; }
	 * 
	 * public int getCount() { return data.size(); }
	 * 
	 * public Object getItem(int position) { return position; }
	 * 
	 * public long getItemId(int position) { return position; }
	 * 
	 * public View getView(final int position, View convertView, ViewGroup
	 * parent) {
	 * 
	 * View vi = convertView;
	 * 
	 * Button mRemoveToList, mSendMail, mShare, mLikeBtn; TextView userlike,
	 * couponNumber; if (convertView == null) vi =
	 * inflater.inflate(R.layout.fav_list_row, parent, false); img = (ImageView)
	 * vi.findViewById(R.id.list_image); comment = (TextView)
	 * vi.findViewById(R.id.usercomment); userlike = (TextView)
	 * vi.findViewById(R.id.liketext); mRemoveToList = (Button)
	 * vi.findViewById(R.id.removetofav); mSendMail = (Button)
	 * vi.findViewById(R.id.send); mShare = (Button)
	 * vi.findViewById(R.id.share); mLikeBtn = (Button)
	 * vi.findViewById(R.id.like); couponNumber = (TextView)
	 * vi.findViewById(R.id.couponnumber);
	 * 
	 * mLikeBtn.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * Common.isFavorMainListClicked = true; Data.likeValues =
	 * String.valueOf(Common.favList.get( position).getId()); Common.Index =
	 * position; ResponceParser res = new ResponceParser();
	 * res.setAction(Data.LIKE); res.execute(WebServices.LIKE_URL);
	 * 
	 * } });
	 * 
	 * mShare.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * Common.IMAGE_PATH = Common.favList.get(position) .getsCouponShareImg();
	 * Common.IMAGE_DESCRIPTION = Common.favList.get(position) .getsComment();
	 * 
	 * callFacebook(); } });
	 * 
	 * Bitmap bitmap = BitmapFactory.decodeFile(Common.favList.get(
	 * position).getsImgPath()); img.setImageBitmap(bitmap);
	 * 
	 * userlike.setText(Integer.toString(Common.favList.get(position)
	 * .getiTotalCount()));
	 * comment.setText(Common.favList.get(position).getsComment());
	 * 
	 * couponNumber.setText(Common.favList.get(position) .getiCouponNumber());
	 * 
	 * mSendMail.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { dbIndex = position;
	 * 
	 * imgName = Common.favList.get(position).getsCouponShareImg(); imgName =
	 * imgName.substring(imgName.lastIndexOf("/") + 1,
	 * imgName.lastIndexOf(".")); UserFunctions1 u = new UserFunctions1();
	 * u.setAction(Data.SEND_MAIL);
	 * u.execute(Common.favList.get(position).getsCouponShareImg());
	 * 
	 * } });
	 * 
	 * // click listener mRemoveToList.setOnClickListener(new OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(View v) { Favorite fav = new Favorite();
	 * fav.setId(Common.favList.get(position).getId()); db.deleteCoupon(fav);
	 * 
	 * Common.favList.remove(position);
	 * FavoriteListView.adapter.notifyDataSetChanged();
	 * 
	 * } });
	 * 
	 * return vi; }
	 * 
	 * void callFacebook() {
	 * 
	 * Session currentSession = Session.getActiveSession(); if (currentSession
	 * == null || currentSession.getState().isClosed()) { Session session = new
	 * Session.Builder(FavoriteListView.this) .build();
	 * Session.setActiveSession(session); currentSession = session; } if
	 * (currentSession.isOpened()) { // Do whatever u want. User has logged in
	 * Session.openActiveSession(FavoriteListView.this, true, new
	 * Session.StatusCallback() {
	 * 
	 * // callback when session changes state
	 * 
	 * @Override public void call(Session session, SessionState state, Exception
	 * exception) { if (session.isOpened()) { share(session); } } }); } else if
	 * (!currentSession.isOpened()) { // Ask for username and password
	 * Session.OpenRequest op = new Session.OpenRequest( FavoriteListView.this);
	 * op.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
	 * op.setCallback(null); List<String> permissions = new ArrayList<String>();
	 * permissions.add("publish_stream"); permissions.add("user_likes");
	 * permissions.add("email"); permissions.add("user_birthday");
	 * op.setPermissions(permissions); Session session = new
	 * Session.Builder(FavoriteListView.this) .build();
	 * Session.setActiveSession(session); session.openForPublish(op); }
	 * 
	 * }
	 * 
	 * public void sendMail(String path, int i) {
	 * 
	 * Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	 * emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
	 * "" }); emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT,
	 * "Coupon"); emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "test"
	 * Common.favList.get(i).getsComment()); emailIntent.setType("image/png");
	 * Uri myUri = Uri.parse("file://" + path);
	 * emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
	 * Common.globalContext.startActivity(Intent.createChooser( emailIntent,
	 * "Send mail...")); }
	 * 
	 * class UserFunctions1 extends AsyncTask<String, Integer, String> {
	 * 
	 * public int action;
	 * 
	 * @Override protected String doInBackground(String... params) { try { URL
	 * url = new URL(params[0]);
	 * 
	 * HttpURLConnection urlConnection = (HttpURLConnection) url
	 * .openConnection(); urlConnection.setRequestMethod("GET");
	 * urlConnection.setDoOutput(true); urlConnection.connect();
	 * 
	 * File sdCardDirectory = new File(
	 * Environment.getExternalStorageDirectory() + File.separator + "Images");
	 * 
	 * if (!sdCardDirectory.exists()) { sdCardDirectory.mkdirs(); } String
	 * filename = imgName + ".jpg"; impath = sdCardDirectory.getAbsolutePath() +
	 * "/" + filename;
	 * 
	 * File file = new File(sdCardDirectory, filename);
	 * 
	 * FileOutputStream fileOutput = new FileOutputStream(file); InputStream
	 * inputStream = urlConnection.getInputStream();
	 * 
	 * byte[] buffer = new byte[1024]; int bufferLength = 0; while
	 * ((bufferLength = inputStream.read(buffer)) != -1) {
	 * fileOutput.write(buffer, 0, bufferLength);
	 * 
	 * }
	 * 
	 * fileOutput.close();
	 * 
	 * } catch (MalformedURLException e) { e.printStackTrace(); } catch
	 * (IOException e) {
	 * 
	 * e.printStackTrace(); } return null; }
	 * 
	 * @Override protected void onPreExecute() { // TODO Auto-generated method
	 * stub super.onPreExecute();
	 * 
	 * mProgressDialog = new ProgressDialog(mcontext);
	 * mProgressDialog.setMessage("Wait..");
	 * 
	 * mProgressDialog.setCancelable(false); if (!isFinishing()) {
	 * mProgressDialog.show(); }
	 * 
	 * }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * 
	 * super.onPostExecute(result); try { mProgressDialog.dismiss(); } catch
	 * (Exception e) { // TODO: handle exception }
	 * 
	 * switch (action) {
	 * 
	 * case Data.SEND_MAIL:
	 * 
	 * sendMail(impath, dbIndex); break; default:
	 * 
	 * break; }
	 * 
	 * }
	 * 
	 * public void setAction(int action) { this.action = action; } } }
	 */

}