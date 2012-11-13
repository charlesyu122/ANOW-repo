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
import android.widget.TextView;
import android.widget.Toast;

public class MyDragEventListener implements OnDragListener {
	
	String username;
	Context context;
	int selectedMonthInt;
	String selectedMonthName;
	ArrayList<Event> eventList;
	int selectedEventId;
	
	// Attributes for Attendance Insertion
	private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //url to create new product
    private static String url_attend_event = "http://10.0.2.2/ANowPhp/attend_event.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
	
	public MyDragEventListener(Context c, String selectedMonthName, ArrayList<Event> eventList, String username){
		this.context = c;
		this.username = username;
		this.selectedMonthName = selectedMonthName;
		this.eventList = eventList;
		Date date = null;
		try {
			date = new SimpleDateFormat("MMMMM", Locale.ENGLISH).parse(selectedMonthName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.selectedMonthInt = cal.get(Calendar.MONTH) + 1;
	}

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
			((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.GRAY);
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			return true;

		case DragEvent.ACTION_DRAG_EXITED:
			((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
			return true;

		case DragEvent.ACTION_DROP:
			// Gets the item containing the dragged data
			ClipData.Item item = event.getClipData().getItemAt(0);
			String eventNameDragged = item.getText().toString();
			int eventIdDragged = this.getIdOfEvent(eventNameDragged);
			String date = ((TextView)v.findViewById(R.id.tvDateCell)).getText().toString();
			
			//Confirm attendance
			LayoutInflater factory = LayoutInflater.from(this.context);
			View confirmView = factory.inflate(R.layout.alert_attend, null);
			AlertDialog.Builder alertEventConfirm = new AlertDialog.Builder(this.context);
			alertEventConfirm.setTitle("Join Event");
			alertEventConfirm.setView(confirmView);

			//Set-up view for alert view
			TextView tvAlertEventName = (TextView)confirmView.findViewById(R.id.tvAlertEventName);
			TextView tvAlertEventDate = (TextView)confirmView.findViewById(R.id.tvAlertEventDate);
			
			//Set the event Id of event dragged
			this.selectedEventId = eventIdDragged;
			
			tvAlertEventName.setText("Attend " + eventNameDragged );
			tvAlertEventDate.setText("on "+ date + " of "+ this.selectedMonthName);
			
			alertEventConfirm.setPositiveButton("Confirm",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							// Add Attendance to chosen event
							new AddAttendance().execute();
							// Toast confirm
							Toast.makeText(context, "Event successfully added to your calendar", Toast.LENGTH_SHORT).show();
							// Set item bg with event
							Drawable withEventBg = context.getResources().getDrawable(R.drawable.witheventcell);
							((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundDrawable(withEventBg);
						}
					});
			alertEventConfirm.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
						}
					});
			alertEventConfirm.show();
			
			return true;

		case DragEvent.ACTION_DRAG_ENDED:
			return true;
		default:
			return false;

		}
	}
	
	private int getIdOfEvent(String eventName){
		int id = -1;
		for(int i=0 ; id==-1 && i<this.eventList.size(); i++){
			if(eventList.get(i).eventName.matches(eventName))
				id = eventList.get(i).eventId;
		}
		return id;
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
			params.add(new BasicNameValuePair("event_id", Integer.toString(selectedEventId)));
			
			// Getting JSON object
            JSONObject json = jsonParser.makeHttpRequest(url_attend_event, params);
            
            // Check for success tag
            try{
            	int success = json.getInt(TAG_SUCCESS);
            	if(success == 1){
            		//successfully created product
            	} else{
            		//failed to create
            	}
            }catch (JSONException e){
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
