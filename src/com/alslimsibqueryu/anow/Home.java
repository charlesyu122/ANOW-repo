package com.alslimsibqueryu.anow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alslimsibqueryu.anow.ActivityRow.AViewHolder;
import com.alslimsibqueryu.anow.EventRow.EViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Home extends TabActivity implements OnClickListener {

	// Attributes
	TabHost tabHost;
	String username;
	Boolean firstLoad = true;

	// Calendar
	GridView calendar;
	String[] dates;
	TextView tvCurMonth;
	ImageButton btnPrev, btnNext;
	Calendar curDate, today;
	DateFormat monthYrFormat = new SimpleDateFormat("MMMM yyyy");
	DateFormat dateFormat = new SimpleDateFormat("dd");
	DateFormat monthDateYrFormat = new SimpleDateFormat("MMM dd yyyy");
	DateFormat yrMonthDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String todayDate;
	// Events tab
	ListView lvEvents;
	// NOW tab
	ListView lvActivities;
	Event[] activities;
	TextView tvDate, tvNoEvent;
	// Header Views
	TextView tvHTitle;
	Button btnProfile, btnSettings, btnAddActivity;

	// Attributes for Database manipulation
	// Progress Dialog
	private ProgressDialog pDialog;

	// Create JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<Event> eventsList;
	ArrayList<Event> eventsListForMonth;
	ArrayList<String> attendsListForMonth;

	// URLS
	private static String url_all_events = "http://10.0.2.2/ANowPhp/get_recent_events.php";
	private static String url_attended_events = "http://10.0.2.2/ANowPhp/get_attended_events.php";

	// products JSONArray
	JSONArray events = null;
	JSONArray attends = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		// Initialize attributes
		eventsList = new ArrayList<Event>();
		eventsListForMonth = new ArrayList<Event>();
		attendsListForMonth = new ArrayList<String>();

		// Get username from Application Controller
		ApplicationController AP = (ApplicationController)getApplicationContext();
		this.username = AP.getUsername();
		
		this.setup();
	}

	private void setup() {
		// Setup Header
		tvHTitle = (TextView) findViewById(R.id.tvTitle);
		btnProfile = (Button) findViewById(R.id.btnHeaderProfile);
		btnSettings = (Button) findViewById(R.id.btnHeaderSetting);
		btnAddActivity = (Button) findViewById(R.id.btnHeaderAddActivity);
		btnProfile.setOnClickListener(this);
		btnAddActivity.setOnClickListener(this);
		btnSettings.setOnClickListener(this);

		// Setup calendar
		calendar = (GridView) findViewById(R.id.gridViewCurDates);
		tvCurMonth = (TextView) findViewById(R.id.tvCurMonth);
		btnPrev = (ImageButton) findViewById(R.id.btnPrev);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		// Get current date
		curDate = Calendar.getInstance();
		today = Calendar.getInstance();
		this.todayDate = yrMonthDateFormat.format(today.getTime());
		tvCurMonth.setText(monthYrFormat.format(curDate.getTime()));
		// Load Events on the Calendar
		loadEventsOnCalendar(1);

		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);

		// Setup ListViews
		// Load upcoming events in Background Thread
		lvEvents = (ListView) findViewById(R.id.lvEvents);
		lvActivities = (ListView) findViewById(R.id.lvActivities);
		
		lvEvents.setOnItemLongClickListener(eventLongClickListener);
		lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int arg2,long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Home.this, EventProfile.class);
				Event eventObj = (Event) v.getTag();
				i.putExtra("eventObject", eventObj);
				i.putExtra("type", "advertised");
				startActivity(i);
			}
		});
		lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent i = null;
				Event activityObj = null;
				try {
					AViewHolder aObj = (AViewHolder) v.getTag();
					activityObj = aObj.activityObj;
					i = new Intent(Home.this, ActivityProfile.class);
					i.putExtra("activityObject", activityObj);
					i.putExtra("type", "attended");
					startActivityForResult(i, 1);
				} catch (Exception e) {
					try {
						EViewHolder eObj = (EViewHolder) v.getTag();
						activityObj = eObj.eventObj;
						i = new Intent(Home.this, EventProfile.class);
						i.putExtra("eventObject", activityObj);
						i.putExtra("type", "attended");
						startActivity(i);
					} catch (Exception ex) {
					}
				}

			}
		});

		// Setup Tabhost
		tabHost = getTabHost();

		// Setup tabs
		// Events Tab
		TabSpec specs = tabHost.newTabSpec("tag1");
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText("Events");
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.eventtab);

		specs.setIndicator(tabIndicator);
		specs.setContent(R.id.Events);

		tabHost.addTab(specs);

		// @NOW Tab
		specs = tabHost.newTabSpec("tag2");
		tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText("NOW");
		icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.anowtab);

		specs.setIndicator(tabIndicator);
		specs.setContent(R.id.NOW);

		tabHost.addTab(specs);

		// NOW Tab Setup
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvNoEvent = (TextView)findViewById(R.id.tvNoActivities);
		tvDate.setText(dateFormat.format(today.getTime()) + " " + tvCurMonth.getText());
	}

	AdapterView.OnItemLongClickListener eventLongClickListener = new AdapterView.OnItemLongClickListener() {
		@SuppressLint({ "NewApi", "NewApi" })
		public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			// TODO Auto-generated method stub

			String event = (String) ((TextView) v.findViewById(R.id.tvEName))
					.getText().toString();
			v.setTag(event);

			ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
			String[] clipDescription = { ClipDescription.MIMETYPE_TEXT_PLAIN };
			ClipData dragData = new ClipData((CharSequence) v.getTag(),clipDescription, item);
			View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
			v.startDrag(dragData, myShadow, null, 0);
			lvEvents.setAdapter(new EventAdapter(Home.this, eventsList));
			return true;
		}
	};

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnHeaderProfile:
			Intent iProfile = new Intent(Home.this, UserProfile.class);
			iProfile.putExtra("type", "user");
			startActivity(iProfile);
			break;
		case R.id.btnHeaderAddActivity:
			Intent iAdd = new Intent(Home.this, ActivityRegistration.class);
			iAdd.putExtra("username", username);
			startActivityForResult(iAdd, 1);
			break;
		case R.id.btnHeaderSetting:
			startActivity(new Intent(Home.this, Settings.class));
			break;
		case R.id.btnPrev:
			Calendar prevMonth = (Calendar) curDate.clone();
			prevMonth.add(Calendar.MONTH, -1);
			tvCurMonth.setText(monthYrFormat.format(prevMonth.getTime()));
			curDate = prevMonth;
			firstLoad = false;

			if (monthYrFormat.format(curDate.getTime()).equals(monthYrFormat.format(Calendar.getInstance().getTime())))
				loadEventsOnCalendar(1);
			else
				loadEventsOnCalendar(0);
			break;
		case R.id.btnNext:
			Calendar nextMonth = (Calendar) curDate.clone();
			nextMonth.add(Calendar.MONTH, 1);
			tvCurMonth.setText(monthYrFormat.format(nextMonth.getTime()));
			curDate = nextMonth;
			firstLoad = false;

			if (monthYrFormat.format(curDate.getTime()).equals(monthYrFormat.format(Calendar.getInstance().getTime())))
				loadEventsOnCalendar(1);
			else
				loadEventsOnCalendar(0);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data.getExtras().containsKey("addActivity")) {
			if (data.getBooleanExtra("addActivity", false) == true)
				Toast.makeText(Home.this, "Activity has been added to your calendar!", Toast.LENGTH_SHORT).show();
		}
		if(data.getExtras().containsKey("reloadHome")){
			if(data.getBooleanExtra("reloadHome", false) == true) {
				loadEventsOnCalendar(1);
				// set current tab to events
				tabHost.setCurrentTab(0);
			}
		}
	}
	
	private void updateArrayDates(int numOfDays, int firstDay, int prevDays, int current) {
		this.dates = new String[42];
		int ndx, ctr;
		
		// currentmonth
		for (ndx = firstDay - 1, ctr = 1; ctr <= numOfDays; ctr++, ndx++) {

			String date = dateFormat.format(today.getTime());
			if (date.charAt(0) == '0')
				date = Character.toString(date.charAt(1));
			if (current == 1 && date.equals(Integer.toString(ctr))){
				String dateString = Integer.toString(ctr);
				dateString += "!";
				if(checkForEvent(ctr) == true)
					dateString += "*";
				this.dates[ndx] = dateString;
			} else if (checkForEvent(ctr) == true)
				this.dates[ndx] = Integer.toString(ctr) + "*";
			else
				this.dates[ndx] = Integer.toString(ctr);
		}
		// nextmonth
		for (ctr = 1; ndx < this.dates.length; ndx++, ctr++)
			this.dates[ndx] = Integer.toString(ctr) + "#";
		// prevmonth
		for (ndx = firstDay - 2, ctr = prevDays; ndx >= 0; ndx--, ctr--)
			this.dates[ndx] = Integer.toString(ctr) + "#";
	}

	private boolean checkForEvent(int traversedDate) {
		// TODO Auto-generated method stub
		Boolean check = false;
		int eventDate;
		String temp = "", listDate = ""; 
		for(int i=0; check == false && i<attendsListForMonth.size(); i++, temp = ""){
			listDate = attendsListForMonth.get(i);
			if(listDate.charAt(8) != '0')
				temp+= listDate.charAt(8);
			temp+= listDate.charAt(9);
			eventDate = Integer.parseInt(temp);
			if(traversedDate == eventDate){
				check = true;
			}
		}
		return check;
	}

	private void loadEventsOnCalendar(int current){
		DateFormat yrMonthFormat = new SimpleDateFormat("yyyy-MM");
		String selectectedMonthYr = yrMonthFormat.format(curDate.getTime());
		String firstDate = selectectedMonthYr+"-01";
		String lastDate = selectectedMonthYr+"-31";
		new LoadEventsForCalendar(firstDate, lastDate, current).execute();
	}
	
	private void updateDatesDisplayed(int current) {
		int days = curDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		curDate.set(Calendar.DAY_OF_MONTH, 1);
		int first = curDate.get(Calendar.DAY_OF_WEEK);
		Calendar prev = (Calendar) curDate.clone();
		prev.add(Calendar.MONTH, -1);
		int prevDays = prev.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.updateArrayDates(days, first, prevDays, current);
		calendar.setAdapter(new DateAdapter(tvCurMonth.getText().toString()));
		calendar.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				String date = ((TextView) v.findViewById(R.id.tvDateCell)).getText().toString();
				String monthYr = tvCurMonth.getText().toString();
				tvDate.setText( date + " " + monthYr);
				// Retrieve events on specific day
				activities = getActivities(formatDate(monthYr, date));
				if(activities.length > 0)
					tvNoEvent.setVisibility(View.GONE);
				else
					tvNoEvent.setVisibility(View.VISIBLE);
				Home.this.lvActivities.setAdapter(new EventActivityAdapter(Home.this, activities));
				tabHost.setCurrentTab(1);
			}
		});
	}
	
	private Event[] getActivities(String selectedDate){
		ArrayList<Event> activities = new ArrayList<Event>();
		for(int i=0; i < attendsListForMonth.size(); i++){
			if(attendsListForMonth.get(i).equals(selectedDate)){
				activities.add(eventsListForMonth.get(i));
			}
		}
		return activities.toArray(new Event[activities.size()]);
	}
	
	private String formatDate(String monthYr, String date){
		// Parse for year-month-date format
		String delim = "[ ]+";
		String[] monthYrArr = monthYr.split(delim);
		String monthName = monthYrArr[0];
		String year = monthYrArr[1];
		Date d = null;
		try {
			d = new SimpleDateFormat("MMMMM", Locale.ENGLISH).parse(monthName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int monthNumInt = cal.get(Calendar.MONTH) + 1;
		String monthNum = Integer.toString(monthNumInt);
		if(monthNum.length() == 1)
			monthNum = "0"+monthNum;
		if(date.length() == 1)
			date = "0"+date;
		Log.d("HERE", year+"-"+monthNum+"-"+date);
		return year+"-"+monthNum+"-"+date;
	}
	
	/**
	 * Background Async Task to Load all events by making HTTP Request
	 * */
	class LoadAllEvents extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(Home.this);
			pDialog.setMessage("Loading Upcoming Events. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("today", todayDate));
			params.add(new BasicNameValuePair("username", username));

			// Getting JSONString from url
			JSONObject json = jParser.makeHttpRequest(url_all_events, params);

			try {
				// Check success return
				int success = json.getInt("success");
				if (success == 1) {
					// Upcoming events are found
					// Get array of events
					events = json.getJSONArray("events");

					// Loop through all events
					for (int i = 0; i < events.length(); i++) {
						JSONObject c = events.getJSONObject(i);

						// Store each json item in variable
						int id = c.getInt("event_id");
						String name = c.getString("event_name");
						String tStart = c.getString("time_start");
						String dStart = c.getString("date_start");
						String dEnd = c.getString("date_end");
						String loc = c.getString("location");
						String desc = c.getString("description");
						String imgUrl = c.getString("image");
						int img = getResources().getIdentifier(imgUrl, null,
								getPackageName());

						// Create new Event object
						Event e = new Event(id, name, tStart, dStart, dEnd, loc, desc, "E", img);

						// Add event to arraylist of events
						eventsList.add(e);
					}
				} else {
					// no upcoming events found
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			// Update UI from background thread
			runOnUiThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					lvEvents.setAdapter(new EventAdapter(Home.this, eventsList));
				}
			});
		}

	}

	// Date Adapter Class
	public class DateAdapter extends BaseAdapter{
		
		private String[] gridDates;
		MyDragEventListener dragListener;
		
		public DateAdapter(String selectedMonth){
			this.gridDates = dates;
			dragListener = new MyDragEventListener(selectedMonth);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return this.gridDates.length;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressLint("NewApi") public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) Home.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View gridView = inflater.inflate(R.layout.date_gridcell, parent, false);
			TextView tvDate = (TextView) gridView.findViewById(R.id.tvDateCell);
			if(gridDates[position].charAt(gridDates[position].length()-1) == '#'){ //not in current month
				String date = gridDates[position];
				date = date.substring(0, date.length()-1);
				tvDate.setText(date);
				tvDate.setTextColor(Color.GRAY);
			} else if(gridDates[position].charAt(gridDates[position].length()-1) == '*'){ //date with event
				String date = gridDates[position];
				date = date.substring(0, date.length()-1);
				tvDate.setBackgroundDrawable(Home.this.getResources().getDrawable(R.drawable.witheventcell));
				if(date.charAt(date.length()-1) == '!'){ // date today with event
					date = date.substring(0, date.length()-1);
					tvDate.setTextColor(Color.RED);
				}
				tvDate.setText(date);
			} else if(gridDates[position].charAt(gridDates[position].length()-1) == '!'){ //date today with no event
				String date = gridDates[position];
				date = date.substring(0, date.length()-1);
				tvDate.setText(date);
				tvDate.setTextColor(Color.RED);
			}
			else tvDate.setText(gridDates[position]);
			tvDate.setOnDragListener(dragListener);
			
			return gridView;
		}

	}

	// MyDragEventListener Class
	public class MyDragEventListener extends Activity implements OnDragListener {

		String selectedMonthName;
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
		private String url_attend_event = "http://10.0.2.2/ANowPhp/attend_event.php";
		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		

		public MyDragEventListener(String selectedMonthYr) {
			// Parse month and year received
			String delim = "[ ]+";
			String[] monthYr = selectedMonthYr.split(delim);
			this.selectedMonthName = monthYr[0];
			this.selectedYear = monthYr[1];
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
				LayoutInflater factory = LayoutInflater.from(Home.this);
				View confirmView = factory.inflate(R.layout.alert_attend, null);
				AlertDialog.Builder alertEventConfirm = new AlertDialog.Builder(Home.this);
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
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date selectedDateF = format.parse(selectedYear+"-"+selectedMonthInt+"-"+selectedDate);
							Date startDateF = format.parse(eventDates[0]);
							Date endDateF = format.parse(eventDates[1]);	
							if(selectedDateF.compareTo(startDateF) >= 0)
								Log.d("STATUS", "PASS1");
							if(selectedDateF.compareTo(endDateF) <= 0)
								Log.d("STATUS", "PASS2");
							if(selectedDateF.compareTo(startDateF) >= 0 && selectedDateF.compareTo(endDateF) <= 0){
								// Add Attendance to chosen event
								new AddAttendance().execute();
								// Toast confirm
								Toast.makeText(Home.this,"Event successfully added to your calendar",Toast.LENGTH_SHORT).show();
								// Set item background with event
								Drawable withEventBg = Home.this.getResources().getDrawable(R.drawable.witheventcell);
								((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundDrawable(withEventBg);
							}
							else{
								Toast.makeText(Home.this, "Unable to attend event on chosen date. Please re-check the date of chosen event.", Toast.LENGTH_LONG).show();
								((TextView) v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
							}
							// Refresh Home page
							Intent i = new Intent((Activity)Home.this, Home.class);
							Home.this.startActivity(i);
							((Activity)Home.this).finish();
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
			for (int i = 0; id == -1 && i < eventsList.size(); i++) {
				if (eventsList.get(i).eventName.matches(eventName))
					id = eventsList.get(i).eventId;
			}
			return id;
		}
		
		private String getEventDates(String eventName){
			String dates = "";
			for (int i = 0; dates.matches("") && i < eventsList.size(); i++) {
				if (eventsList.get(i).eventName.matches(eventName))
					dates = eventsList.get(i).eventDateStart + "*" + eventsList.get(i).eventDateEnd;
			}
			return dates;
		}

		
		
		class AddAttendance extends AsyncTask<String, String, String> {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pDialog = new ProgressDialog(Home.this);
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
				params.add(new BasicNameValuePair("private", privateOption));
				params.add(new BasicNameValuePair("attend_date", selectedYear+"-"+selectedMonthInt+"-"+selectedDate));

				// Getting JSON object
				JSONObject json = jsonParser.makeHttpRequest(url_attend_event,params);

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

	/**
	 * Background Async Task to Load all events for calendar display by making HTTP Request
	 * */
	class LoadEventsForCalendar extends AsyncTask<String, String, String> {

		String beginDate, endDate;
		int current;
		
		public LoadEventsForCalendar(String begin, String end, int current){
			this.beginDate = begin;
			this.endDate = end;
			this.current = current;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Refresh lists
			eventsListForMonth.clear();
			attendsListForMonth.clear();
			pDialog = new ProgressDialog(Home.this);
			pDialog.setMessage("Loading your calendar..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("begin_date", beginDate));
			params.add(new BasicNameValuePair("end_date", endDate));
			params.add(new BasicNameValuePair("username", username));

			// Getting JSONString from url
			JSONObject json = jParser.makeHttpRequest(url_attended_events, params);

			try {
				// Check success return
				int success = json.getInt("success");
				if (success == 1) {
					// Upcoming events are found
					// Get array of events and attends
					events = json.getJSONArray("events");
					attends = json.getJSONArray("attends");

					// Loop through all events
					for (int i = 0; i < events.length(); i++) {
						JSONObject c = events.getJSONObject(i);

						// Store each json item in variable
						int id = c.getInt("event_id");
						String name = c.getString("event_name");
						String tStart = c.getString("time_start");
						String dStart = c.getString("date_start");
						String dEnd = c.getString("date_end");
						String loc = c.getString("location");
						String desc = c.getString("description");
						String type = c.getString("type");
						String imgUrl = c.getString("image");
						int img = getResources().getIdentifier(imgUrl, null, getPackageName());

						// Create new Event object
						Event e = new Event(id, name, tStart, dStart, dEnd, loc, desc, type, img);

						// Add info to list of events and attends
						eventsListForMonth.add(e);
						attendsListForMonth.add(attends.getString(i));
					}
				} else {
					// no upcoming events found
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
			// Retrieve activities for list current date
			activities = getActivities(yrMonthDateFormat.format(curDate.getTime()));
			if(activities.length > 0)
				tvNoEvent.setVisibility(View.GONE);
			else
				tvNoEvent.setVisibility(View.VISIBLE);
			lvActivities.setAdapter(new EventActivityAdapter(Home.this, activities));
			// Retrieve events for calendar
			updateDatesDisplayed(current);
			if(firstLoad == true)
				new LoadAllEvents().execute();
		}
	}

	
}
