package anow.adapters;

import com.alslimsibqueryu.anow.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import anow.datamodels.Event;
import anow.datamodels.EventWithImage;
import anow.datamodels.Row;
import anow.datamodels.RowType;

public class EventRow implements Row{
	private final EventWithImage event;
	private final LayoutInflater inflater;
	
	public EventRow(LayoutInflater inflater, EventWithImage event){
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
        Log.d("EVENTROW", ""+event.getImage());
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventTimeStart());
        holder.eventPic.setVisibility(View.VISIBLE);
        holder.eventPic.setImageBitmap(event.getImage());
        holder.eventObj = new Event(event.eventId, event.eventName, event.eventTimeStart, event.eventDateStart, event.eventDateEnd, event.eventLocation, event.eventDescription, event.type);
        
        rowView.setTag(holder);
        return rowView;
	}

	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.EVENT_ROW.ordinal();
	}
	
	public static class EViewHolder {
        public final TextView eventName;
        public final TextView eventDate;
        public final ImageView eventPic;
        public Event eventObj;

        private EViewHolder(TextView eName, TextView eDate, ImageView ePic) {
            this.eventName = eName;
            this.eventDate = eDate;
            this.eventPic = ePic;
        }
        
    }
}
