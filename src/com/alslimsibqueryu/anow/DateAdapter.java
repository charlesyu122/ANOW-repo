package com.alslimsibqueryu.anow;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DateAdapter extends BaseAdapter{
	
	private Context context;
	private String[] gridDates;
	MyDragEventListener dragListener;
	
	public DateAdapter(Context context, String[] dates, String selectedMonth, ArrayList<Event>eventList, String username){
		this.context = context;
		this.gridDates = dates;
		dragListener = new MyDragEventListener(context, selectedMonth, eventList, username);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return this.gridDates.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("NewApi") public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView = inflater.inflate(R.layout.date_gridcell, parent, false);
		TextView tvDate = (TextView) gridView.findViewById(R.id.tvDateCell);
		if(gridDates[position].charAt(gridDates[position].length()-1) == '#'){ //not in current month
			String date = gridDates[position];
			date = date.substring(0, date.length()-1);
			tvDate.setText(date);
			tvDate.setTextColor(Color.GRAY);
		}else if(gridDates[position].charAt(gridDates[position].length()-1) == '!'){ //date today
			String date = gridDates[position];
			date = date.substring(0, date.length()-1);
			tvDate.setText(date);
			tvDate.setTextColor(Color.RED);
		}
		else tvDate.setText(gridDates[position]);
		tvDate.setOnDragListener(dragListener);
		
		return gridView;
	}

}
