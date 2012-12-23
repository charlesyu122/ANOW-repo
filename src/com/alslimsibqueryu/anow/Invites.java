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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Invites extends Activity{

	ListView lvInvites;
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Database Connectivity attributes
	private String username;
	ArrayList<Invite> invitationList;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_invites = "http://10.0.2.2/ANowPhp/get_invites.php";
	JSONArray invites = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invites);
		// Retrieve user name
		ApplicationController AC = (ApplicationController)getApplicationContext();
		this.username = AC.getUsername();
		// Load invites
		new LoadAllInvitations().execute();
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		tvTitle.setText("Invites");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//Set-up views
		lvInvites = (ListView)findViewById(R.id.lvInvites);
		lvInvites.setAdapter(new InviteAdapter(this, invitationList.toArray(new Invite[invitationList.size()])));
	}

	// Methods for database query
	class LoadAllInvitations extends AsyncTask<String, String, String> {
			
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
	    	pDialog = new ProgressDialog(Invites.this);
	    	pDialog.setMessage("Loading Invitations. Please wait...");
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
			JSONObject json = jParser.makeHttpRequest(url_get_invites, params);

			// Check for success tag
			try {
				int success = json.getInt("success");
				if (success == 1) {

					//Get array of products
					invites = json.getJSONArray("invitations");
					invitationList = new ArrayList<Invite>();
						
					//Looping through all products
					for(int i=0; i < invites.length(); i++){
						JSONObject c  = invites.getJSONObject(i);
							
						//Storing each json item in variable
						String attendId = c.getString("attend_id");
						String event = c.getString("event_name");
						String invitee = c.getString("invitee_name");
						String uri = c.getString("event_image");
						int eventPic = getResources().getIdentifier(uri, null, getPackageName());
							
						// Create new invite object
						Invite invite = new Invite(attendId, invitee, event, eventPic);
							
						//Adding invites to list of invites to display
						invitationList.add(invite);
					}
				} else {
					// failed to retrieved
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
			setup();
		}	
	}
}
