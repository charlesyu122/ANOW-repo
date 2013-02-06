package anow.adapters;

import com.alslimsibqueryu.anow.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import anow.datamodels.Event;
import anow.datamodels.EventWithImage;
import anow.datamodels.Row;
import anow.datamodels.RowType;

public class ActivityRow implements Row{
	private final Event activity;
	private final LayoutInflater inflater;
	
	public ActivityRow(LayoutInflater inflater, EventWithImage activityWithImage){
		this.activity = new Event(activityWithImage.eventId, activityWithImage.eventName, activityWithImage.eventTimeStart, activityWithImage.eventDateStart, activityWithImage.eventDateEnd, activityWithImage.eventLocation, activityWithImage.eventDescription, activityWithImage.type);
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

        rowView.setTag(holder);
        return rowView;
	}

	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.ACTIVITY_ROW.ordinal();
	}
	
	public static class AViewHolder {
        public final TextView activityName;
        public final TextView activityTime;
        public final TextView activityLocation;
        public Event activityObj;

        private AViewHolder(TextView aName, TextView aTime, TextView aLoc) {
            this.activityName = aName;
            this.activityTime = aTime;
            this.activityLocation = aLoc;
        }
        
    }

}
