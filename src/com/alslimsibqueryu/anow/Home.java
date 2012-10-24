package com.alslimsibqueryu.anow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.alslimsibqueryu.anow.ActivityRow.AViewHolder;
import com.alslimsibqueryu.anow.EventRow.EViewHolder;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class Home extends TabActivity implements OnClickListener {

	GridView calendar;
	String[] dates;
	TabHost tabHost;

	// Calendar
	TextView tvCurMonth;
	ImageButton btnPrev, btnNext;
	Calendar curDate, today;
	DateFormat monthYrFormat = new SimpleDateFormat("MMMM yyyy");
	DateFormat dateFormat = new SimpleDateFormat("dd");
	DateFormat monthDateYrFormat = new SimpleDateFormat("MMM dd yyyy");
	String todayDate;
	// Events tab
	ListView lvEvents;
	Event[] events;
	// NOW tab
	ListView lvActivities;
	Event[] activities;
	TextView tvDate;
	Button bAdd;
	// Header
	Button btnProfile, btnSettings, btnAddActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		this.populateDummy();
		this.setup();
	}

	public void populateDummy() {
		// Events Dummy
		this.events = new Event[] { 
				new Event(1, "Starbucks Promo", "2:00pm", "12.01.12", "Ayala Terraces", R.drawable.starbucks),
				new Event(2,"Ayala Sale", "10:00am", "8.15.2012", "Ayala Cebu", R.drawable.ayala),
				new Event(3,"USC Graduation", "6:00pm", "03.25.13", "USC South Campus",R.drawable.usc),
				new Event(4,"Graduation Ball", "6:00pm", "03.27.13", "Radisson Blu", R.drawable.usc),
				new Event(5,"Accenture Talk", "7:00pm", "10.27.12", "Waterfront", R.drawable.accenture),
				new Event(6,"Sunscream 2012", "7:00 am","8.27.12", "Tambuli", R.drawable.summersunscream), 
				new Event(7,"DS Acquaintance Party", "5:00pm", "8.16.12", "JCenter Mall", R.drawable.ds),
				new Event(8,"USC Prom", "8:00pm", "12.7.12", "Waterfront", R.drawable.usc) };

		// Activities Dummy
		this.activities = new Event[] {
				new Event(3,"USC Graduation", "6:00pm", "03.25.13", "USC South Campus",R.drawable.usc),
				new Event(1, "Starbucks Promo", "2:00pm", "12.01.12", "Ayala Terraces", R.drawable.starbucks),
				new Event(1, "Android Exam", "12:30pm", "10.10.12", "LB 446"),
				new Event(2, "Softeng Exam", "8:30pm","10.13.12", "LB 466"),
				new Event(3, "Project Meeting", "4:00pm", "9.30.12","Starbucks"),
				new Event(4, "Birthday", "9pm","10.13.12", "Famous Resto"),
				new Event(5, "Thesis Defense", "2pm","10.12.12", "LB467")};
	}

	private void setup() {
		// Setup Header
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
		this.todayDate = monthYrFormat.format(today.getTime());
		tvCurMonth.setText(monthYrFormat.format(curDate.getTime()));
		this.updateDatesDisplayed(1);

		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);

		// Setup ListViews
		lvEvents = (ListView) findViewById(R.id.lvEvents);
		lvActivities = (ListView) findViewById(R.id.lvActivities);
		lvEvents.setAdapter(new EventAdapter(this, events));
		lvActivities.setAdapter(new EventActivityAdapter(this, activities));
		lvEvents.setOnItemLongClickListener(eventLongClickListener);
		lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Home.this, EventProfile.class);
				Event eventObj = (Event)v.getTag();
				i.putExtra("eventObject", eventObj);
				startActivity(i);
			}
		});
		lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = null;
				Event activityObj = null;
				try{
					AViewHolder aObj = (AViewHolder) v.getTag();
					activityObj = aObj.activityObj;
					i = new Intent(Home.this, ActivityProfile.class);
					i.putExtra("activityObject", activityObj);
				}catch(Exception e){
					try{
						EViewHolder eObj = (EViewHolder) v.getTag();
						activityObj = eObj.eventObj;
						i = new Intent(Home.this, EventProfile.class);
						i.putExtra("eventObject", activityObj);
					}catch(Exception ex){
					}
				}
				startActivity(i);
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
		tvDate.setText(dateFormat.format(today.getTime()) + " "
				+ tvCurMonth.getText());

	}

	AdapterView.OnItemLongClickListener eventLongClickListener = new AdapterView.OnItemLongClickListener() {
		@SuppressLint({ "NewApi", "NewApi" }) public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			String event = (String) ((TextView) v.findViewById(R.id.tvEName))
					.getText().toString();
			v.setTag(event);

			ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
			String[] clipDescription = { ClipDescription.MIMETYPE_TEXT_PLAIN };
			ClipData dragData = new ClipData((CharSequence) v.getTag(), clipDescription, item);
			View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
			v.startDrag(dragData, myShadow, null, 0);
			return true;
		}
	};


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnHeaderProfile:
			startActivity(new Intent(Home.this, UserProfile.class));
			break;
		case R.id.btnHeaderAddActivity:
			startActivity(new Intent(Home.this, ActivityRegistration.class));
			break;
		case R.id.btnHeaderSetting:
			startActivity(new Intent(Home.this, Settings.class));
			break;
		case R.id.btnPrev:
			Calendar prevMonth = (Calendar) curDate.clone();
			prevMonth.add(Calendar.MONTH, -1);
			tvCurMonth.setText(monthYrFormat.format(prevMonth.getTime()));
			curDate = prevMonth;
			if (monthYrFormat.format(curDate.getTime()).equals(this.todayDate))
				this.updateDatesDisplayed(1);
			else
				this.updateDatesDisplayed(0);
			break;
		case R.id.btnNext:
			Calendar nextMonth = (Calendar) curDate.clone();
			nextMonth.add(Calendar.MONTH, 1);
			tvCurMonth.setText(monthYrFormat.format(nextMonth.getTime()));
			curDate = nextMonth;
			if (monthYrFormat.format(curDate.getTime()).equals(this.todayDate))
				this.updateDatesDisplayed(1);
			else
				this.updateDatesDisplayed(0);
			break;
		}
	}
	
	private void updateArrayDates(int numOfDays, int firstDay, int prevDays, int current){
		this.dates = new String[42];
		int ndx, ctr;
		//currentmonth
		for(ndx = firstDay-1, ctr = 1;ctr <= numOfDays; ctr++,ndx++){
			
			String date = dateFormat.format(today.getTime());
			if(date.charAt(0) == '0')
				date = Character.toString(date.charAt(1));
			Log.d("Date today", date);
			if(current == 1 && date.equals(Integer.toString(ctr)))
				this.dates[ndx] = Integer.toString(ctr)+"!";
			else 
				this.dates[ndx] = Integer.toString(ctr);
		}
		//nextmonth
		for(ctr = 1;ndx < this.dates.length; ndx++, ctr++)
			this.dates[ndx] = Integer.toString(ctr)+"#";
		//prevmonth
		for(ndx = firstDay-2, ctr = prevDays; ndx >= 0; ndx--, ctr--)
			this.dates[ndx] = Integer.toString(ctr)+"#";
	}
	
	private void updateDatesDisplayed(int current){
		int days = curDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		curDate.set(Calendar.DAY_OF_MONTH, 1);
		int first = curDate.get(Calendar.DAY_OF_WEEK);
		Calendar prev = (Calendar) curDate.clone();
		prev.add(Calendar.MONTH, -1);
		int prevDays = prev.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.updateArrayDates(days, first, prevDays, current);
		calendar.setAdapter(new DateAdapter(this, dates, tvCurMonth.getText().toString()));
		calendar.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), ((TextView)v.findViewById(R.id.tvDateCell)).getText(), Toast.LENGTH_SHORT).show();
				tvDate.setText(((TextView)v.findViewById(R.id.tvDateCell)).getText() + " " + tvCurMonth.getText());
			}
		});
	}
}
