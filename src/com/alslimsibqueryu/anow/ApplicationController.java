package com.alslimsibqueryu.anow;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApplicationController extends Application {

	String userId;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	// setter getters
	public void setUserId(String uid) {
		this.userId = uid;
	}

	public String getUserId() {
		return this.userId;
	}

	public boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    boolean isConnectedToNetwork = (networkInfo != null && networkInfo.isConnected());
	    return isConnectedToNetwork;
	    //return true;
	}
}
