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


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UserCalendar extends Activity {

	// Attributes
	private String username, name;
	Boolean firstLoad = true;

	// Header Views
	TextView tvTitle;
	Button btnProfile, btnBack;

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
	String todayDate; // for query of recent events

	// Other views
	TextView tvSelectedDate, tvNoEventsForUser;
	ListView lvActivities;
	Event[] activities;

	// Database Interaction Attributes
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	ArrayList<Event> eventsListForMonth;
	ArrayList<String> attendsListForMonth;
	private static String url_user_attended_events = "http://10.0.2.2/ANowPhp/get_user_attended_events.php";
	JSONArray events = null;
	JSONArray attends = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_calendar);
		// Initialize attributes
		eventsListForMonth = new ArrayList<Event>();
		attendsListForMonth = new ArrayList<String>();
		
		// Retrieve extras
		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.name = i.getStringExtra("name");

		// Set-up views
		setup();
	}

	private void setup() {
		// Set-up header views
		tvTitle = (TextView) findViewById(R.id.tvUserTitle);
		btnProfile = (Button) findViewById(R.id.btnHeaderProfile);
		btnBack = (Button) findViewById(R.id.btnHeaderBack);
		tvTitle.setText(name + "'s Calendar");
		btnProfile.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(2);
				finish();
			}
		});
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		// Setup calendar
		calendar = (GridView) findViewById(R.id.gridViewUserCurDates);
		tvCurMonth = (TextView) findViewById(R.id.tvUserCurMonth);
		btnPrev = (ImageButton) findViewById(R.id.btnUserPrev);
		btnNext = (ImageButton) findViewById(R.id.btnUserNext);
		btnPrev.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar prevMonth = (Calendar) curDate.clone();
				prevMonth.add(Calendar.MONTH, -1);
				tvCurMonth.setText(monthYrFormat.format(prevMonth.getTime()));
				curDate = prevMonth;
				firstLoad = false;

				if (monthYrFormat.format(curDate.getTime()).equals(monthYrFormat.format(Calendar.getInstance().getTime())))
					loadEventsOnCalendar(1);
				else
					loadEventsOnCalendar(0);
				String monthYr = tvCurMonth.getText().toString();
				tvSelectedDate.setText( "1" + " " + monthYr);
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar nextMonth = (Calendar) curDate.clone();
				nextMonth.add(Calendar.MONTH, 1);
				tvCurMonth.setText(monthYrFormat.format(nextMonth.getTime()));
				curDate = nextMonth;
				firstLoad = false;

				if (monthYrFormat.format(curDate.getTime()).equals(monthYrFormat.format(Calendar.getInstance().getTime())))
					loadEventsOnCalendar(1);
				else
					loadEventsOnCalendar(0);
				String monthYr = tvCurMonth.getText().toString();
				tvSelectedDate.setText( "1" + " " + monthYr);
			}
		});
		// Get current date
		curDate = Calendar.getInstance();
		today = Calendar.getInstance();
		this.todayDate = yrMonthDateFormat.format(today.getTime());
		tvCurMonth.setText(monthYrFormat.format(curDate.getTime()));

		// Set-up views on right side
		tvSelectedDate = (TextView) findViewById(R.id.tvUserSelectedDate);
		tvNoEventsForUser = (TextView) findViewById(R.id.tvNoUserEvents);
		lvActivities = (ListView) findViewById(R.id.lvUserActivities);
		tvSelectedDate.setText(dateFormat.format(today.getTime()) + " "+ tvCurMonth.getText());
		
		// Load Events on the Calendar
		loadEventsOnCalendar(1);
	}

	// Helper methods
	private void updateArrayDates(int numOfDays, int firstDay, int prevDays, int current) {
		this.dates = new String[42];
		int ndx, ctr;

		// currentmonth
		for (ndx = firstDay - 1, ctr = 1; ctr <= numOfDays; ctr++, ndx++) {

			String date = dateFormat.format(today.getTime());
			if (date.charAt(0) == '0')
				date = Character.toString(date.charAt(1));
			if (current == 1 && date.equals(Integer.toString(ctr))) {
				String dateString = Integer.toString(ctr);
				dateString += "!";
				if (checkForEvent(ctr) == true)
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
		for (int i = 0; check == false && i < attendsListForMonth.size(); i++, temp = "") {
			listDate = attendsListForMonth.get(i);
			if (listDate.charAt(8) != '0')
				temp += listDate.charAt(8);
			temp += listDate.charAt(9);
			eventDate = Integer.parseInt(temp);
			if (traversedDate == eventDate) {
				check = true;
			}
		}
		return check;
	}

	private void loadEventsOnCalendar(int current) {
		DateFormat yrMonthFormat = new SimpleDateFormat("yyyy-MM");
		String selectectedMonthYr = yrMonthFormat.format(curDate.getTime());
		String firstDate = selectectedMonthYr + "-01";
		String lastDate = selectectedMonthYr + "-31";
		new LoadUserEventsForCalendar(firstDate, lastDate, current).execute();
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
				String date = ((TextView) v.findViewById(R.id.tvUserDateCell)).getText().toString();
				String monthYr = tvCurMonth.getText().toString();
				tvSelectedDate.setText(date + " " + monthYr);
				// Retrieve events on specific day
				activities = getActivities(formatDate(monthYr, date));
				if (activities.length > 0)
					tvNoEventsForUser.setVisibility(View.GONE);
				else
					tvNoEventsForUser.setVisibility(View.VISIBLE);
				lvActivities.setAdapter(new EventActivityAdapter(UserCalendar.this, activities));
			}
		});
	}

	private Event[] getActivities(String selectedDate) {
		ArrayList<Event> activities = new ArrayList<Event>();
		for (int i = 0; i < attendsListForMonth.size(); i++) {
			if (attendsListForMonth.get(i).equals(selectedDate))
				activities.add(eventsListForMonth.get(i));
		}
		return activities.toArray(new Event[activities.size()]);
	}

	private String formatDate(String monthYr, String date) {
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
		if (date.length() == 1)
			date = "0" + date;
		return year + "-" + monthNum + "-" + date;
	}

	// Date Adapter Class
	public class DateAdapter extends BaseAdapter {

		private String[] gridDates;

		public DateAdapter(String selectedMonth) {
			this.gridDates = dates;
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

		@SuppressLint("NewApi")
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) UserCalendar.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View gridView = inflater.inflate(R.layout.grid_cell_user, parent,false);
			TextView tvDate = (TextView) gridView.findViewById(R.id.tvUserDateCell);
			if (gridDates[position].charAt(gridDates[position].length() - 1) == '#') { // not in current month
				String date = gridDates[position];
				date = date.substring(0, date.length() - 1);
				tvDate.setText(date);
				tvDate.setTextColor(Color.GRAY);
			} else if (gridDates[position].charAt(gridDates[position].length() - 1) == '*') { // date with event
				String date = gridDates[position];
				date = date.substring(0, date.length() - 1);
				tvDate.setBackgroundDrawable(UserCalendar.this.getResources().getDrawable(R.drawable.witheventcell));
				if (date.charAt(date.length() - 1) == '!') { // date today with event
					date = date.substring(0, date.length() - 1);
					tvDate.setTextColor(Color.RED);
				}
				tvDate.setText(date);
			} else if (gridDates[position]
					.charAt(gridDates[position].length() - 1) == '!') { // date today with no event
				String date = gridDates[position];
				date = date.substring(0, date.length() - 1);
				tvDate.setText(date);
				tvDate.setTextColor(Color.RED);
			} else
				tvDate.setText(gridDates[position]);

			return gridView;
		}

	}

	/**
	 * Background Async Task to Load all events for calendar display by making
	 * HTTP Request
	 * */
	class LoadUserEventsForCalendar extends AsyncTask<String, String, String>{

		String beginDate, endDate;
		int current;
		
		public LoadUserEventsForCalendar(String begin, String end, int cur){
			this.beginDate = begin;
			this.endDate = end;
			this.current = cur;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Refresh lists
			eventsListForMonth.clear();
			attendsListForMonth.clear();
			pDialog = new ProgressDialog(UserCalendar.this);
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
			JSONObject json = jParser.makeHttpRequest(url_user_attended_events, params);

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
				tvNoEventsForUser.setVisibility(View.GONE);
			else
				tvNoEventsForUser.setVisibility(View.VISIBLE);
			lvActivities.setAdapter(new EventActivityAdapter(UserCalendar.this, activities));
			// Retrieve events for calendar
			updateDatesDisplayed(current);
		}
		
	}
}
