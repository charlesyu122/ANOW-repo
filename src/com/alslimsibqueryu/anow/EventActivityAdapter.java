package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EventActivityAdapter extends BaseAdapter{
	
	private Context context;
	final List<Row> rows;
	
	public EventActivityAdapter(Context context, Event[] values) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.rows = new ArrayList<Row>();//member variable

        for (int i=0; i < values.length; i++) {
            //if it has an image, use an ImageRow
            if (values[i].type.equals("E")) {
                rows.add(new EventRow(LayoutInflater.from(this.context), values[i]));
            } else {//otherwise use a DescriptionRow
                rows.add(new ActivityRow(LayoutInflater.from(this.context), values[i]));
            }
        }
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return RowType.values().length;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return rows.get(position).getViewType();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return rows.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return rows.get(position).getView(convertView);
	}
	
	
}


