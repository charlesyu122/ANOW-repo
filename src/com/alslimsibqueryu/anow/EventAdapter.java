package com.alslimsibqueryu.anow;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class EventAdapter extends ArrayAdapter<Event>{
	
	private Context context;
	Event[] values = null;
	
	public EventAdapter(Context context, ArrayList<Event> eventsList) {
		super(context, R.layout.single_event, eventsList);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = eventsList.toArray(new Event[eventsList.size()]);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView = convertView;
		if(rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.single_event, parent, false);
		}
		ImageView iv = (ImageView)rowView.findViewById(R.id.ivEPic);
		TextView eName = (TextView)rowView.findViewById(R.id.tvEName);
		TextView eTime = (TextView)rowView.findViewById(R.id.tvEDate);
		
		
		iv.setImageResource(values[position].eventImage);
		eName.setText(values[position].eventName);
		if(values[position].eventDateStart.matches(values[position].eventDateEnd))
			eTime.setText(values[position].eventDateStart);
		else{
			String start = values[position].eventDateStart;
			String end = values[position].eventDateEnd;
			String delim = "[-]+";
			String[] sDate = start.split(delim);
			String[] eDate = end.split(delim);
			eTime.setText(sDate[1]+"-"+ sDate[2] + " to " + eDate[1]+"-"+ eDate[2]);
		}
			
		rowView.setTag(values[position]);
		
		return rowView;
	}
		
}