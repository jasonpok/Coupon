package com.ps.coupon;

import android.app.Application;

import org.acra.*;
import org.acra.annotation.*;


@ReportsCrashes(formKey = "", mailTo = "parkhya.developer@gmail.com")
public class MyApps extends Application {

	

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		
	
	}

	

}
