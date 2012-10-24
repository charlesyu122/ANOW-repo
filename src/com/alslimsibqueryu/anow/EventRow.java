package com.alslimsibqueryu.anow;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventRow implements Row{
	private final Event event;
	private final LayoutInflater inflater;
	
	public EventRow(LayoutInflater inflater, Event event){
		this.event = event;
		this.inflater = inflater;
	}

	public View getView(View convertView) {
		// TODO Auto-generated method stub
		EViewHolder holder;
        View rowView;
        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.single_event, null);
            holder = new EViewHolder((TextView)viewGroup.findViewById(R.id.tvEName),
                    (TextView)viewGroup.findViewById(R.id.tvEDate),
                    (ImageView)viewGroup.findViewById(R.id.ivEPic));
            viewGroup.setTag(holder);
            rowView = viewGroup;
        } else {
        	rowView = convertView;
            holder = (EViewHolder)convertView.getTag();
        }

        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventTimeStart());
        holder.eventPic.setImageResource(event.getEventImage());
        holder.eventObj = event;
        
        return rowView;
	}

	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.EVENT_ROW.ordinal();
	}
	
	public static class EViewHolder {
        final TextView eventName;
        final TextView eventDate;
        final ImageView eventPic;
        Event eventObj;

        private EViewHolder(TextView eName, TextView eDate, ImageView ePic) {
            this.eventName = eName;
            this.eventDate = eDate;
            this.eventPic = ePic;
        }
        
    }
	
	

}
