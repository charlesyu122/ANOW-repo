package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class EventChanges extends Activity{
	
	//Attributes
	ArrayList<String> eventIds;
	ArrayList<Event> eventsWithChanges;
	Intent i;
	// Header views
	TextView tvTitle;
	Button btnBack;
	// Activity views
	ListView lvEventChanges;
	
	// Attributes for Database manipulation
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_events = "http://10.0.2.2/ANowPhp/get_events.php";
	JSONArray events = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_changes);
		
		// Retrieve events
		i = getIntent();
		eventIds = i.getStringArrayListExtra("eventIds");
		eventsWithChanges = new ArrayList<Event>();
		
		Log.d("HERE", ""+eventIds.size());
		new LoadEvents().execute();
	}
	
	private void setup(){
		//  Setup header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		
		tvTitle.setText("Events with changes");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i.putExtra("reloadHome", true);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		
		// Setup list view
		lvEventChanges = (ListView)findViewById(R.id.lvEventChanges);		
		lvEventChanges.setAdapter(new EventChangeAdapter(EventChanges.this, eventsWithChanges));
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		i.putExtra("reloadHome", true);
		setResult(RESULT_OK, i);
		finish();
	}
	
	class LoadEvents extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(EventChanges.this);
			pDialog.setMessage("Retrieving events with changes...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			JSONArray jsonArr = new JSONArray(eventIds);
			// Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("event_ids", jsonArr.toString()));

			// Getting JSONString from url
			JSONObject json = jParser.makeHttpRequest(url_get_events, params);

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
						String type = c.getString("type");
						String imgUrl = c.getString("image");
						int img = getResources().getIdentifier(imgUrl, null, getPackageName());

						// Create new Event object
						Event e = new Event(id, name, tStart, dStart, dEnd, loc, desc, type, img);

						// Add event to arraylist of events
						eventsWithChanges.add(e);
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
			setup();
		}

	}
}
