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
	public static ArrayList<Favorite> couponThumbList = new ArrayList<Favorite>();
	
	public static int cat1StartIndex,cat2StartIndex,cat3StartIndex,cat4StartIndex,cat5StartIndex;
	
	
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
	
	public static ArrayList<Favorite> getThumbArrayList()
	{
		
		couponThumbList  = new ArrayList<Favorite>();
		
		for(int i = 0;i<couponList1.size();i++)
		{
			couponThumbList.add(couponList1.get(i));
		}
		
		int cat1NullCount = (6 - couponList1.size() % 6) % 6;
		for(int i=0;i<cat1NullCount; i++)
		{
			couponThumbList.add(null);
		}
		cat1StartIndex = 0;
		
		
		for(int i = 0;i<couponList2.size();i++)
		{
			couponThumbList.add(couponList2.get(i));
		}
		cat1NullCount = (6 - couponList2.size() % 6) % 6;
		for(int i=0;i<cat1NullCount; i++)
		{
			couponThumbList.add(null);
		}
		cat2StartIndex = cat1StartIndex + (int) Math.ceil((double) couponList1.size() / 6);
		
		for(int i = 0;i<couponList3.size();i++)
		{
			couponThumbList.add(couponList3.get(i));
		}
		 cat1NullCount = (6 - couponList3.size() % 6) % 6;
		for(int i=0;i<cat1NullCount; i++)
		{
			couponThumbList.add(null);
		}
		cat3StartIndex = cat2StartIndex + (int) Math.ceil((double) couponList2.size() / 6);
		
		for(int i = 0;i<couponList4.size();i++)
		{
			couponThumbList.add(couponList4.get(i));
		}
		cat1NullCount = (6 - couponList4.size() % 6) % 6;
		for(int i=0;i<cat1NullCount; i++)
		{
			couponThumbList.add(null);
		}
		
		cat4StartIndex =cat3StartIndex + (int) Math.ceil((double) couponList3.size() / 6);
		for(int i = 0;i<couponList5.size();i++)
		{
			couponThumbList.add(couponList5.get(i));
		}
		cat1NullCount = (6 - couponList5.size() % 6) % 6;
		for(int i=0;i<cat1NullCount; i++)
		{
			couponThumbList.add(null);
		}
		cat5StartIndex = cat4StartIndex + (int) Math.ceil((double) couponList4.size() / 6);
		
		return couponThumbList;
	}
	
	
}
