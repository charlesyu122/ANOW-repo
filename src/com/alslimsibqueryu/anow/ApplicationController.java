package com.alslimsibqueryu.anow;

import android.app.Application;

public class ApplicationController extends Application{

	String userId;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	//setter getters
	public void setUserId(String uid){
		this.userId = uid;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
}
