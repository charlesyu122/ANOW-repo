package com.alslimsibqueryu.anow;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class UserInviteAdapter extends ArrayAdapter<User>{
	
	@SuppressWarnings("unused")
	private Context context;
	User[] values = null;
	
	public UserInviteAdapter(Context context, ArrayList<User> objects){
		super(context, R.layout.single_user, objects);
		this.context = context;
		this.values = objects.toArray(new User[objects.size()]);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}

}
