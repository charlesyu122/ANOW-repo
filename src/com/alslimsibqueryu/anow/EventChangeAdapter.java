package com.alslimsibqueryu.anow;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventChangeAdapter extends ArrayAdapter<EventWithImage>{
	
	private final Context context;
	private final ArrayList<EventWithImage> values;
	
	public EventChangeAdapter(Context context, ArrayList<EventWithImage> values){
		super(context, R.layout.single_event_change, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View rowView = convertView;
		if(rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.single_event_change, parent, false);
		}
		ImageView ivEPic = (ImageView)rowView.findViewById(R.id.ivEventChangePic);
		TextView tvEName = (TextView)rowView.findViewById(R.id.tvEventChangeName);
		TextView tvEDate = (TextView)rowView.findViewById(R.id.tvEventChangeDate);
		
		ivEPic.setImageBitmap(values.get(position).eventImage);
		tvEName.setText(values.get(position).eventName + " moved to");
		if(values.get(position).eventDateStart.matches(values.get(position).eventDateEnd))
			tvEDate.setText(values.get(position).eventDateStart);
		else{
			String start = values.get(position).eventDateStart;
			String end = values.get(position).eventDateEnd;
			String delim = "[-]+";
			String[] sDate = start.split(delim);
			String[] eDate = end.split(delim);
			tvEDate.setText(sDate[1]+"-"+ sDate[2] +"-"+ sDate[0] + " to " + eDate[1]+"-"+ eDate[2] +"-"+ eDate[0]);
		}
		
		return rowView;
	}
}
