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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import anow.adapters.UserAdapter;
import anow.datamodels.User;

public class Friends extends Activity{

	private String server = "http://atnow.net84.net/";
	ListView lvFriends;
	TextView tvNoFriends;
	private String type; // user or friend
	private int countOfFriends;
	ApplicationController AC;
	
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Database Connectivity attributes
	private String userId;
	private String loggedInUserId; // for type friend
	ArrayList<User> friendsList;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_friends = "http://atnow.net84.net/ANowPhp/get_friends.php";
	JSONArray users = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		AC = (ApplicationController)getApplicationContext();
		// Retrieve type
		Intent i = getIntent();
		this.type = i.getStringExtra("type");
		if(type.equals("user")){
			ApplicationController AC = (ApplicationController)getApplicationContext();
			this.userId = AC.getUserId();
		} else if(type.equals("friend")){
			this.userId = i.getStringExtra("friend_user_id");
		}
		
		tvNoFriends = (TextView)findViewById(R.id.tvNoFriends);
		// Load friends
		new LoadAllFriends().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 2)
			finish();
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		if(type.equals("user"))
			tvTitle.setText("My Connections");
		else
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
		UserAdapter userAdpt;
		if(type.equals("user"))
			userAdpt = new UserAdapter(Friends.this, friendsList, 'F', userId);
		else
			userAdpt = new UserAdapter(Friends.this, friendsList, 'L', loggedInUserId);
		lvFriends.setAdapter(userAdpt);
		lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(type.equals("user")){
					if(AC.isOnline(Friends.this)){
						User friend = (User)v.getTag();
						Intent i = new Intent(Friends.this, UserProfile.class);
						i.putExtra("type", "friend");
						i.putExtra("user_id", friend.userId);
						startActivityForResult(i, 1);
					} else
						Toast.makeText(Friends.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
        // Start loading images
        for(User u : friendsList){
        	u.loadImage(userAdpt);
        }
	}
	
	// Methods for database query
	class LoadAllFriends extends AsyncTask<String, String, String> {
		
		Bitmap bitmap = null;
		
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
			params.add(new BasicNameValuePair("user_id", userId));

			if(type.equals("friend")){
				ApplicationController AC = (ApplicationController)getApplicationContext();
				loggedInUserId = AC.getUserId();
				params.add(new BasicNameValuePair("loggedInUserId", loggedInUserId));
			}

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
						String userId = c.getString("user_id");
						String username = c.getString("username");
						String name = c.getString("name");
						String birthday = c.getString("birthday");
						String hobbies = c.getString("hobbies");
						String eventCount = c.getString("event_count");
						String imgDir = c.getString("profile_image");
						imgDir = server + parseDir(imgDir);
						String status = "friends";
						if(type.equals("friend")){
							status = c.getString("status");
						}
						
						// Create new user object
						User friend = new User(userId, username, name, birthday, hobbies, eventCount, imgDir, status);
						
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
