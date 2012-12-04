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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Friends extends Activity{

	ListView lvFriends;
	TextView tvNoFriends;
	private String type; // user or friend
	private int countOfFriends;
	
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Database Connectivity attributes
	private String username;
	ArrayList<User> friendsList;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_friends = "http://10.0.2.2/ANowPhp/get_friends.php";
	JSONArray users = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		// Retrieve type
		Intent i = getIntent();
		this.type = i.getStringExtra("type");
		if(type.equals("user")){
			ApplicationController AC = (ApplicationController)getApplicationContext();
			this.username = AC.getUsername();
		} else if(type.equals("friend")){
			this.username = i.getStringExtra("friend_username");
		}
		
		tvNoFriends = (TextView)findViewById(R.id.tvNoFriends);
		// Load friends
		new LoadAllFriends().execute();
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		tvTitle.setText("Connections");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//Set-up views
		lvFriends = (ListView) findViewById(R.id.lvFriends);
		if(type.equals("user"))
			lvFriends.setAdapter(new UserAdapter(Friends.this, friendsList, 'F'));
		else
			lvFriends.setAdapter(new UserAdapter(Friends.this, friendsList, 'L'));
		lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(type.equals("user")){
					User friend = (User)v.getTag();
					Intent i = new Intent(Friends.this, UserProfile.class);
					i.putExtra("type", "friend");
					i.putExtra("username", friend.username);
					startActivity(i);
				}
			}
		});
	}
	
	// Methods for database query
	class LoadAllFriends extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
    		pDialog = new ProgressDialog(Friends.this);
    		pDialog.setMessage("Loading Friends. Please wait...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Build parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));

			// Getting JSON object
			JSONObject json = jParser.makeHttpRequest(url_get_friends, params);

			// Check for success tag
			try {
				int success = json.getInt("success");
				if (success == 1) {

					//Get array of products
					users = json.getJSONArray("friends");
					countOfFriends = users.length();
					friendsList = new ArrayList<User>();
					
					//Looping through all products
					for(int i=0; i < users.length(); i++){
						JSONObject c  = users.getJSONObject(i);
						
						//Storing each json item in variable
						String username = c.getString("username");
						String name = c.getString("name");
						String birthday = c.getString("birthday");
						String hobbies = c.getString("hobbies");
						String eventCount = c.getString("event_count");
						String uri = c.getString("profile_image");
						int profPic = getResources().getIdentifier(uri, null, getPackageName());
						
						// Create new user object
						User friend = new User(username, name, birthday, hobbies, eventCount, profPic);
						
						//Adding friends to list of friends to display
						friendsList.add(friend);
					}
				} else {
					// failed to retrieved
					countOfFriends = 0;
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
			if(countOfFriends == 0){
				tvNoFriends.setVisibility(View.VISIBLE);
				// Set-up header views
				tvTitle = (TextView)findViewById(R.id.tvTitle);
				btnBack = (Button)findViewById(R.id.btnHeader);
				tvTitle.setText("Friends");
				btnBack.setText("Back");
				btnBack.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else{
				// Set-up list
				setup();
			}
		}
	}	
}
