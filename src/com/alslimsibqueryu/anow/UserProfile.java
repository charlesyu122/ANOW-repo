package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends Activity {

	// Header Views
	Button btnSettings, btnSave;
	TextView headerTitle;
	int btnHeaderStatus = 0;
		
	// Activity Views
	ImageView ivProfPic;
	TextView tvProfName, tvProfBday, tvProfInterest, tvUsername, tvEventCount;
	EditText etProfName, etProfBday, etProfInterest;
	Button btnCalendar, btnFriends, btnInvites;
	String username;
	int updateSuccess; 
	
	// Progress Dialog
	private ProgressDialog pDialog;	
	
	// Create JSON Parser object
	JSONParser jParser = new JSONParser();
	
	// User object to store
	User myProfile;
	ArrayList<HashMap<String, String>> userMatched;

	//URLs to php files (retrieve and update)
	private static String url_get_user_profile = "http://10.0.2.2/ANowPhp/get_user_profile.php";
	private static String url_edit_user_profile = "http://10.0.2.2/ANowPhp/edit_profile.php";
	
	//JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";
	
	//products JSONArray
	JSONArray user = null;
	
	// Information to display
	String name, eventCount, birthday, hobbies;
	int profPic;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		
		// Initialize
		this.myProfile = new User();
		this.userMatched = new ArrayList<HashMap<String, String>>();
		
		// Set-up views
		this.setup();
		
		// Load user profile in Background Thread
		new LoadUserProfile().execute();
	}

	private void setup() {
		// Set-up header
		headerTitle = (TextView) findViewById(R.id.tvTitle);
		btnSettings = (Button) findViewById(R.id.btnHeader);
		btnSave = (Button) findViewById(R.id.btnHeader2);
		headerTitle.setText("Profile");
		btnSettings.setText("Settings");
		btnSave.setText("Save");
		btnSettings.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(UserProfile.this, Settings.class));
			}
		});

		// Set-up profile views
		ivProfPic = (ImageView) findViewById(R.id.ivProfPic);
		tvUsername = (TextView) findViewById(R.id.tvProfUname);
		tvEventCount = (TextView) findViewById(R.id.tvEventCount); 
		tvProfName = (TextView) findViewById(R.id.tvProfName);
		etProfName = (EditText) findViewById(R.id.etProfName);
		tvProfBday = (TextView) findViewById(R.id.tvProfBday);
		etProfBday = (EditText) findViewById(R.id.etProfBday);
		tvProfInterest = (TextView) findViewById(R.id.tvProfInterest);
		etProfInterest = (EditText) findViewById(R.id.etProfInterest);
		btnCalendar = (Button) findViewById(R.id.btnCalendar);
		btnFriends = (Button) findViewById(R.id.btnFriends);
		btnInvites = (Button) findViewById(R.id.btnInvites);
		
		btnCalendar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnFriends.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserProfile.this, Friends.class);
				i.putExtra("username", username);
				startActivity(i);
			}
		});
		btnInvites.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserProfile.this, Invites.class);
				i.putExtra("username", username);
				startActivity(i);
			}
		});

		View.OnLongClickListener listen = new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (((TextView) v).getId() == tvProfName.getId()) {
					tvProfName.setVisibility(View.GONE);
					etProfName.setText(tvProfName.getText());
					etProfName.setVisibility(View.VISIBLE);
					
				} else if (((TextView) v).getId() == tvProfBday.getId()) {
					tvProfBday.setVisibility(View.GONE);
					etProfBday.setText(tvProfBday.getText());
					etProfBday.setVisibility(View.VISIBLE);
					
				} else if (((TextView) v).getId() == tvProfInterest.getId()) {
					tvProfInterest.setVisibility(View.GONE);
					etProfInterest.setText(tvProfInterest.getText());
					etProfInterest.setVisibility(View.VISIBLE);
				}
				if(btnSave.getVisibility() == View.GONE){
					btnSave.setVisibility(View.VISIBLE);
					btnSettings.setVisibility(View.GONE);
				}
				return false;
			}
		};
		
		View.OnClickListener savelisten = new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (etProfName.getVisibility() == View.VISIBLE) {
					name = etProfName.getText().toString();
					tvProfName.setText(etProfName.getText());
					etProfName.setVisibility(View.GONE);
					tvProfName.setVisibility(View.VISIBLE);
				}
				if (etProfBday.getVisibility() == View.VISIBLE) {
					birthday = etProfBday.getText().toString();
					tvProfBday.setText(etProfBday.getText());
					etProfBday.setVisibility(View.GONE);
					tvProfBday.setVisibility(View.VISIBLE);
				}
				if (etProfInterest.getVisibility() == View.VISIBLE) {
					hobbies = etProfInterest.getText().toString();
					tvProfInterest.setText(etProfInterest.getText());
					etProfInterest.setVisibility(View.GONE);
					tvProfInterest.setVisibility(View.VISIBLE);
				}
				
				// Update database
				updateSuccess = 0;
				new UpdateUserProfile().execute();
				btnSave.setVisibility(View.GONE);
				btnSettings.setVisibility(View.VISIBLE);
			}
		};

		tvProfName.setOnLongClickListener(listen);
		tvProfBday.setOnLongClickListener(listen);
		tvProfInterest.setOnLongClickListener(listen);
		btnSave.setOnClickListener(savelisten);
	}

	class LoadUserProfile extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(UserProfile.this);
			pDialog.setMessage("Loading profile. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			//Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			
			//Getting JSONObject from url
			JSONObject json = jParser.makeHttpRequest(url_get_user_profile, params);
			
			//Check log cat for JSON response
			Log.d("User received:", json.toString());
			
			try{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					// User is found
					// Get user
					user = json.getJSONArray(TAG_USER);
					
					if(user.length() == 1){
						JSONObject temp = user.getJSONObject(0);
						// Store each json item in variable
						name = temp.getString("name");
						birthday = temp.getString("birthday");
						hobbies = temp.getString("hobbies");
						eventCount = temp.getString("event_count");
						String uri = temp.getString("profile_image");
						profPic = getResources().getIdentifier(uri, null, getPackageName());
					}
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			// update UI from Background thread
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					// Display Information
					ivProfPic.setImageResource(profPic);
					tvUsername.setText(username);
					tvEventCount.setText(eventCount);
					tvProfName.setText(name);
					tvProfBday.setText(birthday);
					tvProfInterest.setText(hobbies);
				}
			});
		}
		
	}
	
	class UpdateUserProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(UserProfile.this);
			pDialog.setMessage("Updating Profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("birthday", birthday));
			params.add(new BasicNameValuePair("hobbies", hobbies));
			
			// Send modified data through HTTP request
			JSONObject json = jParser.makeHttpRequest(url_edit_user_profile, params);
			
			// Check json success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					updateSuccess = 1;
				}
			} catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(updateSuccess == 1)
				Toast.makeText(UserProfile.this, "Profile successfully edited!", Toast.LENGTH_SHORT).show();
		}
	}
}
