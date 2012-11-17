package com.alslimsibqueryu.anow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MyDragEventListener extends Activity implements OnDragListener {

	String username;
	Context context;
	String selectedMonthName;
	ArrayList<Event> eventList;
	int selectedEventId;
	String privateOption = "N"; // sets the added activity to private

	// Date that is dragged
	int selectedMonthInt;
	String selectedYear;
	String selectedDate;

	// Attributes for Database Interaction
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	// urls
	private static String url_attend_event = "http://10.0.2.2/ANowPhp/attend_event.php";
	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	public MyDragEventListener(Context c, String selectedMonthYr,
			ArrayList<Event> eventList, String username) {
		this.context = c;
		this.username = username;
		this.eventList = eventList;
		// Parse month and year received
		String delim = "[ ]+";
		String[] monthYr = selectedMonthYr.split(delim);
		this.selectedMonthName = monthYr[0];
		this.selectedYear = monthYr[1];
		Date date = null;
		try {
			date = new SimpleDateFormat("MMMMM", Locale.ENGLISH)
					.parse(selectedMonthName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.selectedMonthInt = cal.get(Calendar.MONTH) + 1;
	}

	@SuppressLint("ShowToast")
	public boolean onDrag(final View v, DragEvent event) {
		// TODO Auto-generated method stub

		final int action = event.getAction();
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				return true;
			} else
				return false;

		case DragEvent.ACTION_DRAG_ENTERED:
			((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.GRAY);
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			return true;

		case DragEvent.ACTION_DRAG_EXITED:
			((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
			return true;

		case DragEvent.ACTION_DROP:
			// Gets the item containing the dragged data
			ClipData.Item item = event.getClipData().getItemAt(0);
			selectedDate = ((TextView) v.findViewById(R.id.tvDateCell)).getText().toString();
			
			// Set the event Id of event dragged
			String eventNameDragged = item.getText().toString();
			int eventIdDragged = this.getIdOfEvent(eventNameDragged);
			this.selectedEventId = eventIdDragged;
			
			// Confirm attendance
			LayoutInflater factory = LayoutInflater.from(this.context);
			View confirmView = factory.inflate(R.layout.alert_attend, null);
			AlertDialog.Builder alertEventConfirm = new AlertDialog.Builder(this.context);
			alertEventConfirm.setTitle("Please Check Event Details:");
			alertEventConfirm.setView(confirmView);

			// Set-up view for alert view
			TextView tvAlertEventName = (TextView) confirmView.findViewById(R.id.tvAlertEventName);
			TextView tvAlertEventDate = (TextView) confirmView.findViewById(R.id.tvAlertEventDate);
			TextView tvAlertSelectedDate = (TextView) confirmView.findViewById(R.id.tvAlertSelectedDate);
			CheckBox cbAlertPrivacy = (CheckBox) confirmView.findViewById(R.id.cbAlertPrivacy);

			// Set-up privacy check box for alert
			cbAlertPrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) 
						privateOption = "Y";
				}
			});
			
			// Parse start and end dates
			String delim = "[*]+";
			final String[] eventDates = this.getEventDates(eventNameDragged).split(delim);
			String dateToDisplay= "";
			if(eventDates[0].matches(eventDates[1]))
				dateToDisplay = eventDates[0];
			else 
				dateToDisplay = eventDates[0] + " to " + eventDates[1];
			
			tvAlertEventName.setText("Attend " + eventNameDragged);
			tvAlertEventDate.setText("("+dateToDisplay+")");
			tvAlertSelectedDate.setText("on " + selectedDate + " of "+ this.selectedMonthName + " " + this.selectedYear);

			alertEventConfirm.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					// Check validity of dates
					SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
					try {
						Date selectedDateF = format.parse(selectedYear+"-"+selectedMonthInt+"-"+selectedDate);
						Date startDateF = format.parse(eventDates[0]);
						Date endDateF = format.parse(eventDates[1]);	
						if(selectedDateF.compareTo(startDateF) >= 0 && selectedDateF.compareTo(endDateF) <= 0){
							// Add Attendance to chosen event
							new AddAttendance().execute();
							// Toast confirm
							Toast.makeText(context,"Event successfully added to your calendar",Toast.LENGTH_SHORT).show();
							// Set item background with event
							Drawable withEventBg = context.getResources().getDrawable(R.drawable.witheventcell);
							((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundDrawable(withEventBg);
						}
						else{
							Toast.makeText(context, "Unable to attend event on chosen date. Please re-check the date of chosen event.", Toast.LENGTH_LONG).show();
							((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			alertEventConfirm.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
						}
					});
			alertEventConfirm.show();

			return true;

		case DragEvent.ACTION_DRAG_ENDED:
			return false;
		default:
			return false;

		}
	}

	private int getIdOfEvent(String eventName) {
		int id = -1;
		for (int i = 0; id == -1 && i < this.eventList.size(); i++) {
			if (eventList.get(i).eventName.matches(eventName))
				id = eventList.get(i).eventId;
		}
		return id;
	}
	
	private String getEventDates(String eventName){
		String dates = "";
		for (int i = 0; dates.matches("") && i < this.eventList.size(); i++) {
			if (eventList.get(i).eventName.matches(eventName))
				dates = eventList.get(i).eventDateStart + "*" + eventList.get(i).eventDateEnd;
		}
		return dates;
	}

	class AddAttendance extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Adding the event to your calendar..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Build parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("event_id", Integer
					.toString(selectedEventId)));
			params.add(new BasicNameValuePair("private", privateOption));

			// Getting JSON object
			JSONObject json = jsonParser.makeHttpRequest(url_attend_event,
					params);

			// Check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// successfully created product
				} else {
					// failed to create
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}
	}

}
