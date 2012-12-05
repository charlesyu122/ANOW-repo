package com.alslimsibqueryu.anow;

import android.app.Application;

public class ApplicationController extends Application{

	String username;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	//setter getters
	public void setUsername(String uname){
		this.username = uname;
	}
	
	public String getUsername(){
		return username;
	}
	
}
