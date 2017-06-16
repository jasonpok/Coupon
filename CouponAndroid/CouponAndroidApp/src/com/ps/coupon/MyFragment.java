package com.ps.coupon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import com.ps.db.DbHelper;
import com.ps.favbean.Favorite;
import com.ps.helper.ResponceParser;
import com.ps.helper.WebServices;
import com.ps.util.Common;
import com.ps.util.Data;
import com.ps.util.ImageLoader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment {

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

		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();
		db = new DbHelper(Common.globalContext);
		/** Getting integer data of the key current_page from the bundle */
		mCurrentPage = data.getInt("current_page", 0);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vi = inflater.inflate(R.layout.list_row, container, false);
		ImageView img;
		TextView text, couponNumber,pageNo;

		Button mAddToFav, mShareBtn, mSendBtn, mLikeBtn;
		ImageLoader imageLoader;
		imageLoader = new ImageLoader(Common.globalContext);
		img = (ImageView) vi.findViewById(R.id.list_image);
		text = (TextView) vi.findViewById(R.id.usercomment);
		pageNo = (TextView) vi.findViewById(R.id.pageNoText);
		Common.userlike = (TextView) vi.findViewById(R.id.liketext);
		mAddToFav = (Button) vi.findViewById(R.id.addtofav);
		mShareBtn = (Button) vi.findViewById(R.id.share);
		mSendBtn = (Button) vi.findViewById(R.id.send);
		mLikeBtn = (Button) vi.findViewById(R.id.like);

		couponNumber = (TextView) vi.findViewById(R.id.couponnumber);

		//pageInfo
		pageNo.setText(String.format("%d/%d",mCurrentPage, Common.totalCouponCount));
		
		// fill data
		text.setText(Common.couponList.get(mCurrentPage - 1).getsComment());
		// 17002262
		couponNumber.setText(Common.couponList.get(mCurrentPage - 1)
				.getiCouponNumber());
		imageLoader.DisplayImage(Common.couponList.get(mCurrentPage - 1)
				.getsImgPath(), img);
		Common.userlike.setText(Integer.toString(Common.couponList.get(
				mCurrentPage - 1).getiTotalCount()));

		mSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dbIndex = mCurrentPage - 1;
				isSendClicked = true;
				imgName = Common.couponList.get(mCurrentPage - 1)
						.getsCouponShareImg();
				imgName = imgName.substring(imgName.lastIndexOf("/") + 1,
						imgName.lastIndexOf("."));

				if (impath.contains(imgName)) {

					sendMail(impath);
				} else {
					UserFunctions u = new UserFunctions();
					u.setAction(Data.SEND_MAIL);
					u.execute(Common.couponList.get(mCurrentPage - 1)
							.getsCouponShareImg());
				}

			}
		});

		mLikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.isFavorMainListClicked = false;

				Data.likeValues = String.valueOf(Common.couponList.get(
						mCurrentPage - 1).getId());
				Common.Index = mCurrentPage - 1;

				ResponceParser res = new ResponceParser();
				res.setAction(Data.LIKE);
				res.execute(WebServices.LIKE_URL);

			}
		});

		mShareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.isFavFacebookClicked = false;
				Common.IMAGE_PATH = Common.couponList.get(mCurrentPage - 1)
						.getsCouponShareImg();
				Common.IMAGE_DESCRIPTION = Common.couponList.get(
						mCurrentPage - 1).getsComment();

				// callFacebook();

			}
		});

		mAddToFav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isSDCardAvailable = isSdPresent();
				if (isSDCardAvailable) {

					isFavClicked = true;
					dbIndex = mCurrentPage - 1;
					imgName = Common.couponList.get(mCurrentPage - 1)
							.getsImgPath();
					imgName = imgName.substring(imgName.lastIndexOf("/") + 1,
							imgName.lastIndexOf("."));

					// List<Favorite> checkimg = db.getImgPath();

					UserFunctions u = new UserFunctions();
					u.setAction(Data.ADDTOFAV);
					u.execute(Common.couponList.get(mCurrentPage - 1)
							.getsImgPath());
				} else {
					// Sorry
					Toast.makeText(Common.globalContext, "insert sd card",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return vi;
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

			mProgressDialog = new ProgressDialog(Common.globalContext);
			mProgressDialog.setMessage("Wait..");

			mProgressDialog.setCancelable(false);
			try {
				// if (!Common.mactivity.isFinishing()) {
				mProgressDialog.show();
				// }
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

				db.addInfo(new Favorite(Common.couponList.get(dbIndex).getId(),
						1, 1, Common.couponList.get(dbIndex).getsCouponName(),
						Common.couponList.get(dbIndex).getfDate(),
						Common.couponList.get(dbIndex).gettDate(), impath,
						Common.couponList.get(dbIndex).getsComment(),
						Common.couponList.get(dbIndex).getiTotalCount(),
						Common.couponList.get(dbIndex).getsImageUrl(),
						Common.couponList.get(dbIndex).getiCouponNumber(),
						Common.couponList.get(dbIndex).getsCouponShareImg()));

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

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Coupon");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				Common.couponList.get(dbIndex).getsComment());
		emailIntent.setType("image/png");
		Uri myUri = Uri.parse("file://" + path);
		emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
		Common.globalContext.startActivity(Intent.createChooser(emailIntent,
				"Send mail..."));
	}

	public boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}
