package anow.adapters;

import java.util.ArrayList;

import com.alslimsibqueryu.anow.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import anow.datamodels.Event;
import anow.datamodels.EventWithImage;

public class EventAdapter extends ArrayAdapter<EventWithImage> {

	private Context context;
	EventWithImage[] values = null;

	public EventAdapter(Context context, ArrayList<EventWithImage> eventsList) {
		super(context, R.layout.single_event, eventsList);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = eventsList.toArray(new EventWithImage[eventsList.size()]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		EventWithImage e = values[position];
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.single_event, parent, false);
		}
		ImageView iv = (ImageView) rowView.findViewById(R.id.ivEPic);
		ProgressBar pbLoading = (ProgressBar) rowView.findViewById(R.id.pbEventPicLoading);
		TextView eName = (TextView) rowView.findViewById(R.id.tvEName);
		TextView eTime = (TextView) rowView.findViewById(R.id.tvEDate);

		// Setting of values
		eName.setText(values[position].eventName);
		if (values[position].eventDateStart.matches(values[position].eventDateEnd))
			eTime.setText(values[position].eventDateStart);
		else {
			String start = values[position].eventDateStart;
			String end = values[position].eventDateEnd;
			String delim = "[-]+";
			String[] sDate = start.split(delim);
			String[] eDate = end.split(delim);
			eTime.setText(sDate[1] + "-" + sDate[2] + " to " + eDate[1] + "-"
					+ eDate[2]);
		}

		// For lazy loading of images
		if (e.eventImage != null) {
			// Display Image
			iv.setVisibility(View.VISIBLE);
			iv.setImageBitmap(e.eventImage);
			pbLoading.setVisibility(View.GONE);
		} else {
			// Loading
			iv.setVisibility(View.GONE);
			pbLoading.setVisibility(View.VISIBLE);
		}

		// Tagging
		Event temp = (Event) values[position];
		Event tag = new Event(temp.eventId, temp.eventName,
				temp.eventTimeStart, temp.eventDateStart, temp.eventDateEnd,
				temp.eventLocation, temp.eventDescription, temp.type);
		rowView.setTag(tag);

		return rowView;
	}
}