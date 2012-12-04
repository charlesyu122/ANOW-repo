package com.alslimsibqueryu.anow;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {

	private Context context;
	User[] values = null;
	char type; // F for friends list P for participants list

	public UserAdapter(Context context, ArrayList<User> objects, char type) {
		super(context, R.layout.single_user, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = objects.toArray(new User[objects.size()]);
		this.type = type;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView = convertView;
		if(rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.single_user, parent, false);
		}
		ImageView userProfPic = (ImageView) rowView.findViewById(R.id.ivListProfPic);
		TextView userName = (TextView) rowView.findViewById(R.id.tvUserName);
		final Button connect = (Button) rowView.findViewById(R.id.btnConnect);
		ImageView ivInfo = (ImageView) rowView.findViewById(R.id.ivUInfo);
		if(type == 'L')
			ivInfo.setVisibility(View.GONE);
		
		userProfPic.setImageResource(values[position].profPic);
		userName.setText(values[position].name);
		
		if (type == 'P'){
			connect.setVisibility(View.VISIBLE);
			connect.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					values[position].setStatus(1);
					connect.setText("Connected");
					connect.setEnabled(false);
				}
			});
		}
		rowView.setTag(values[position]);
		return rowView;
	}

}
