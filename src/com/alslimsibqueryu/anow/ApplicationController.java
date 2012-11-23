package com.alslimsibqueryu.anow;

import android.app.Application;

public class ApplicationController extends Application{

	String username;
	String dateToday;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	//setter getters
	public void setUsername(String uname){
		this.username = uname;
	}
	
	public void setDateToday(String today){
		this.dateToday = today;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getDateToday(){
		return dateToday;
	}
	
}
