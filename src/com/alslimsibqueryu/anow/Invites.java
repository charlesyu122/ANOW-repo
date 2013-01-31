package com.alslimsibqueryu.anow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Invites extends Activity{

	private String server = "http://10.0.2.2/";
	ListView lvInvites;
	TextView tvNoInvites;
	private int countOfInvites;
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
		//Setup header
		setupHeader();
		// Load invites
		new LoadAllInvitations().execute();
	}
	
	private void setupHeader(){
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
	}
	
	private void setup(){
		//Set-up views
		lvInvites = (ListView)findViewById(R.id.lvInvites);
		lvInvites.setAdapter(new InviteAdapter(this, invitationList.toArray(new Invite[invitationList.size()])));
	}

	// Methods for database query
	class LoadAllInvitations extends AsyncTask<String, String, String> {
			
		Bitmap bitmap = null;
		
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
					countOfInvites = invites.length();
					invitationList = new ArrayList<Invite>();
						
					//Looping through all products
					for(int i=0; i < invites.length(); i++){
						JSONObject c  = invites.getJSONObject(i);
							
						//Storing each json item in variable
						String attendId = c.getString("attend_id");
						String event = c.getString("event_name");
						String invitee = c.getString("invitee_name");
						String imgDir = c.getString("event_image");
	
						if(!imgDir.equals("null")){
							// Retrieve image from directory
							try {
						        URL urlImage = new URL(server + parseDir(imgDir));
						        HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();
						        InputStream inputStream = connection.getInputStream();
						        bitmap = BitmapFactory.decodeStream(inputStream);
						    } catch (MalformedURLException e) {
						        e.printStackTrace();
						    } catch (IOException e) {
						        e.printStackTrace();
						    }
						} else{
							bitmap = null;
						}
						// Create new invite object
						Invite invite = new Invite(attendId, invitee, event, bitmap);
							
						//Adding invites to list of invites to display
						invitationList.add(invite);
					}
				} else {
					// failed to retrieved
					countOfInvites = 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("HERE", ""+countOfInvites);
			return null;
		}
		
		private String parseDir(String dir){
			String ret = "", delim = "/";
			String[] folders = dir.split(delim);
			ret += folders[3]+"/"+folders[4]+"/"+folders[5]+"/"+folders[6];
			return ret; 
		}
			
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(countOfInvites != 0)
				setup();
			else{
				tvNoInvites = (TextView)findViewById(R.id.tvNoInvites);
				tvNoInvites.setVisibility(View.VISIBLE);
			}
		}	
	}
}
