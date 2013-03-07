package anow.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alslimsibqueryu.anow.ApplicationController;
import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

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
import anow.adapters.UserAdapter;
import anow.datamodels.User;

public class Participants extends Activity {

	private String server = "http://atnow.net84.net/";
	ListView lvParticipants;
	TextView tvNoParticipants;
	String[] namesOfParticipants;
	String loggedInUserId;
	private int countOfParticipants;
	// Header views
	TextView tvTitle;
	Button btnBack;

	// Database Connectivity attributes
	private String eventId;
	ArrayList<User> participantsList;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_participants = "http://atnow.net84.net/ANowPhp/get_participants.php";
	JSONArray users = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participants);

		// Retrieve event id
		Intent i = getIntent();
		this.eventId = i.getStringExtra("event_id");
		ApplicationController AC = (ApplicationController) getApplicationContext();
		this.loggedInUserId = AC.getUserId();

		tvNoParticipants = (TextView) findViewById(R.id.tvNoParticipants);
		// Load participants
		new LoadAllParticipants().execute();
	}

	private void setup() {
		// Set-up header views
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnBack = (Button) findViewById(R.id.btnHeader);
		tvTitle.setText("Participants");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// Set-up views
		lvParticipants = (ListView) findViewById(R.id.lvParticipants);
		UserAdapter uAdp = new UserAdapter(Participants.this, participantsList, 'P', loggedInUserId);
		lvParticipants.setAdapter(uAdp);
		lvParticipants
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View v,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						// Do nothing as of the moment
					}
				});
		// Start loading images
		for (User u : participantsList) {
			u.loadImage(uAdp);
		}
	}

	// For database query
	class LoadAllParticipants extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Participants.this);
			pDialog.setMessage("Loading Participants. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Build parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("event_id", eventId));
			params.add(new BasicNameValuePair("user_id_loggedin", loggedInUserId));

			// Getting JSON object
			JSONObject json = jParser.makeHttpRequest(url_get_participants, params);

			// Check for success tag
			try {
				int success = json.getInt("success");
				if (success == 1) {

					// Get array of products
					users = json.getJSONArray("participants");
					countOfParticipants = users.length();
					participantsList = new ArrayList<User>();

					// Looping through all products
					for (int i = 0; i < users.length(); i++) {
						JSONObject c = users.getJSONObject(i);

						// Storing each json item in variable
						String userId = c.getString("user_id");
						String username = c.getString("username");
						String name = c.getString("name");
						String birthday = c.getString("birthday");
						String hobbies = c.getString("hobbies");
						String eventCount = c.getString("event_count");
						String imgDir = c.getString("profile_image");
						imgDir = server + parseDir(imgDir);
						String status = c.getString("status");

						// Create new user object
						User friend = new User(userId, username, name, birthday, hobbies, eventCount, imgDir, status);

						// Adding friends to list of friends to display
						participantsList.add(friend);
					}
				} else {
					// failed to retrieved
					countOfParticipants = 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		private String parseDir(String dir) {
			String ret = "", delim = "/";
			String[] folders = dir.split(delim);
			ret += folders[3] + "/" + folders[4] + "/" + folders[5] + "/"
					+ folders[6];
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if (countOfParticipants == 0) {
				tvNoParticipants.setVisibility(View.VISIBLE);
				// Set-up header views
				tvTitle = (TextView) findViewById(R.id.tvTitle);
				btnBack = (Button) findViewById(R.id.btnHeader);
				tvTitle.setText("Participants");
				btnBack.setText("Back");
				btnBack.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else {
				// Set-up list
				setup();
			}
		}
	}

}
