package com.ps.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ps.favbean.Favorite;
import com.ps.helper.JSONParser;
import com.ps.helper.WebServices;
import com.ps.util.Common;

public class DbHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "coupon.db";
	public static final String FAVORITE_TABLE = "favorite";
	public static final int VERSION = 1;

	public static final String COUPON_ID = "id";
	public static final String COUPON_NAME = "couponname";
	public static final String FROM_DATE = "fdate";
	public static final String TO_DATE = "tdate";
	public static final String IMAGE_PATH = "imgpath";
	public static final String COMMENT = "comment";
	public static final String TOTAL_COUNT = "t_count";
	public static final String IMAGE_URL = "imgurl";
	public static final String COUPON_NUMBER = "c_number";
	public static final String COUPON_SHARE_IMAGE = "c_share_img";

	public static final String FAV_TABLE = "fav";
	public static final String FAV_ID = "fav_id";
	public static final String COUPON_INDEX = "c_index";

	public static final String LOGIN_TABLE = "login";
	public static final String LOGIN_ID = "login_id";
	public static final String LOGIN_DONE = "login_done";

	// optional
	public static final String LATITUTE = "latitute";
	public static final String LONGITUTE = "longitute";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + FAVORITE_TABLE + "("
				+ COUPON_ID + " INTEGER PRIMARY KEY unique," + COUPON_NAME
				+ " TEXT ," + FROM_DATE + " TEXT," + TO_DATE + " TEXT,"
				+ IMAGE_PATH + " TEXT," + LATITUTE + " TEXT," + LONGITUTE
				+ " TEXT," + COMMENT + " TEXT," + TOTAL_COUNT + " TEXT,"
				+ IMAGE_URL + " TEXT," + COUPON_NUMBER + " TEXT," +COUPON_SHARE_IMAGE + " TEXT"+ ")";

		String CREATE_TABLE = "CREATE TABLE " + FAV_TABLE + "(" + FAV_ID
				+ " INTEGER PRIMARY KEY ," + COUPON_INDEX + " TEXT" + ")";

		String CREATE_LOGIN_TABLE = "CREATE TABLE " + LOGIN_TABLE + "("
				+ LOGIN_ID + " INTEGER PRIMARY KEY ," + LOGIN_DONE + " TEXT"
				+ ")";

		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_LOGIN_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
		// Create tables again
		onCreate(db);

	}

	public void addIntoFav(String index) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COUPON_INDEX, index);

		// Inserting Row
		db.insert(FAV_TABLE, null, values);
		db.close(); // Closing database connection
	}

	public void addInfo(Favorite fav) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(COUPON_ID, fav.getId());
		values.put(COUPON_NAME, fav.getsCouponName());
		values.put(FROM_DATE, fav.getfDate());
		values.put(TO_DATE, fav.gettDate());
		values.put(IMAGE_PATH, fav.getsImgPath());
		values.put(LATITUTE, fav.getiLatitute());
		values.put(LONGITUTE, fav.getiLongitute());
		values.put(COMMENT, fav.getsComment());
		values.put(TOTAL_COUNT, fav.getiTotalCount());
		values.put(IMAGE_URL, fav.getsImageUrl());
		
		values.put(COUPON_NUMBER, fav.getiCouponNumber());
		values.put(COUPON_SHARE_IMAGE, fav.getsCouponShareImg());
		// Inserting Row
		db.insert(FAVORITE_TABLE, null, values);
		db.close(); // Closing database connection
	}

	public void addIntoLogin(String login) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(LOGIN_DONE, login.toString());
		// Inserting Row
		db.insert(LOGIN_TABLE, null, values);
		db.close(); // Closing database connection
	}

	public String getLoginInfo() {
		String info = "";
		String selectQuery = "SELECT  * FROM " + LOGIN_TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				info = (cursor.getString(1));
				// Adding contact to list

			} while (cursor.moveToNext());
		}
		return info;
	}

	public void updateInfo(String value, String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(COUPON_ID, id);
		contentValues.put(TOTAL_COUNT, value);
		db.update(FAVORITE_TABLE, contentValues, "id = ? ",
				new String[] { (id) });
		db.close();

	}

	// Getting All
	public List<Favorite> getAllContacts() {
		List<Favorite> favList = new ArrayList<Favorite>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + FAVORITE_TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Common.favCouponIds = "";

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				try {
					Favorite fav = new Favorite();
					fav.setId(Integer.parseInt(cursor.getString(0)));
					fav.setsCouponName(cursor.getString(1));
					fav.setfDate(cursor.getString(2));
					fav.settDate(cursor.getString(3));
					fav.setsImgPath(cursor.getString(4));
				
					fav.setiLatitute(Integer.parseInt(cursor.getString(5)));
					fav.setiLongitute(Integer.parseInt(cursor.getString(6)));
					fav.setsComment(cursor.getString(7));
					fav.setiTotalCount(Integer.parseInt(cursor.getString(8)));
					fav.setsImageUrl((cursor.getString(9)));
					fav.setiCouponNumber(cursor.getString(10));
					
					fav.setsCouponShareImg(cursor.getString(11));
					Common.favCouponIds +=fav.getId() +  ",";
					favList.add(fav);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
		}

		
		// return fav list
		return favList;
	}

	// Getting All
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getImgPath() {
		List couponList = new ArrayList();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + FAVORITE_TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Favorite fav = new Favorite();
				fav.setsImgPath(cursor.getString(4));
				// Adding contact to list
				couponList.add(fav);
			} while (cursor.moveToNext());
		}

		// return fav list
		return couponList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getCouponIndex() {
		List couponList = new ArrayList();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + FAV_TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				String str = (cursor.getString(1));
				// Adding contact to list
				couponList.add(str);
			} while (cursor.moveToNext());
		}

		// return fav list
		return couponList;
	}

	// Getting All

	public void deleteCoupon(Favorite fav) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(FAVORITE_TABLE, COUPON_ID + " = ?",
				new String[] { String.valueOf(fav.getId()) });

		db.close();

	}

}
