package com.alslimsibqueryu.anow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivityRow implements Row{
	private final Event activity;
	private final LayoutInflater inflater;
	
	public ActivityRow(LayoutInflater inflater, Event activity){
		this.activity = activity;
		this.inflater = inflater;
	}

	public View getView(View convertView) {
		// TODO Auto-generated method stub
		AViewHolder holder;
        View rowView;
        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.single_activity, null);
            holder = new AViewHolder((TextView)viewGroup.findViewById(R.id.tvAName),
                    (TextView)viewGroup.findViewById(R.id.tvATime),
                    (TextView)viewGroup.findViewById(R.id.tvALocation));
            viewGroup.setTag(holder);
            rowView = viewGroup;
        } else {
        	rowView = convertView;
            holder = (AViewHolder)convertView.getTag();
        }

        holder.activityName.setText(activity.getEventName());
        holder.activityTime.setText(activity.getEventTimeStart());
        holder.activityLocation.setText(activity.getEventLocation());
        holder.activityObj = activity;

        //rowView.setTag(activity);
        return rowView;
	}

	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.ACTIVITY_ROW.ordinal();
	}
	
	public static class AViewHolder {
        final TextView activityName;
        final TextView activityTime;
        final TextView activityLocation;
        Event activityObj;

        private AViewHolder(TextView aName, TextView aTime, TextView aLoc) {
            this.activityName = aName;
            this.activityTime = aTime;
            this.activityLocation = aLoc;
        }
        
    }

}
