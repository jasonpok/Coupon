package com.ps.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.ps.favbean.Citybean;
import com.ps.favbean.CitybeanAfterParse;
import com.ps.favbean.Favorite;
import com.ps.favbean.Mapbean;

public class Common {

	public static ArrayList<Favorite> couponList = new ArrayList<Favorite>();
	public static ArrayList<Favorite> couponList1 = new ArrayList<Favorite>();
	public static ArrayList<Favorite> couponList2 = new ArrayList<Favorite>();
	public static ArrayList<Favorite> couponList3 = new ArrayList<Favorite>();
	public static ArrayList<Favorite> couponList4 = new ArrayList<Favorite>();
	public static ArrayList<Favorite> couponList5 = new ArrayList<Favorite>();
	
	
	public static ArrayList<Favorite> favList = new ArrayList<Favorite>();
	public static ArrayList<Mapbean> mapList = new ArrayList<Mapbean>();
	public static ArrayList<Citybean> cityList = new ArrayList<Citybean>();
	public static ArrayList<CitybeanAfterParse> cityListAfterParse = new ArrayList<CitybeanAfterParse>();
	
	//public static final int PAGE_COUNT = couponList.size();//favList.size();
	
	public static final int COUPON_INDEX=0;
	public static Context globalContext=null;
	public static String COUPON_ID;
	public static String CITY_INDEX;
	public static String favCouponIds;
	public static int Index;
	public static boolean isFavorMainListClicked;
	public static String vStatus;
	public static TextView userlike;
	public static EditText mUserComment;
	public static String comment;
	public static Activity mactivity;
	public static boolean isFavFacebookClicked;
	public static String IMAGE_PATH;
	public static String IMAGE_DESCRIPTION;
	public static String POLICY_DESCRIPTION="";
	public static int totalCouponCount;
	public static int selectedCategory;	
	
	public static void setCategory(int category)
	{
		selectedCategory = category;
		
		switch(category)
		{
			case 1:
				couponList = couponList1;
				break;
			case 2:
				couponList = couponList2;
				break;
			case 3:
				couponList = couponList3;
				break;
			case 4:
				couponList = couponList4;
				break;
			case 5:
				couponList = couponList5;
				break;
			default:
				couponList = couponList1;
				break;
		
		}
		
	}
	
	
}
