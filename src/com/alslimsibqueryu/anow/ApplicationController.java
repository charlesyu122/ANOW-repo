package com.alslimsibqueryu.anow;

import android.app.Application;

public class ApplicationController extends Application{

	String username = "hello";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	public String getUsername(){
		return username;
	}
}
