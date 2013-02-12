package anow.views;

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

import com.alslimsibqueryu.anow.ApplicationController;
import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import anow.datamodels.User;

public class FriendsInvite extends Activity{
	
	private String server = "http://atnow.net84.net/";
	ListView lvFriendsToInvite;
	CheckBox cbSelectAll;
	Button btnInvite;
	TextView tvNoFriends;
	private int countOfFriends;
	private String eventId; 
	ApplicationController AC;
	
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Database Connectivity attributes
	private String userId;
	ArrayList<User> friendsToInviteList;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_get_friends = "http://atnow.net84.net/ANowPhp/get_friends.php";
	private static String url_invite_friends = "http://atnow.net84.net/ANowPhp/invite_friends.php";
	JSONArray users = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		// Retrieve user_id and event_id
		AC = (ApplicationController)getApplicationContext();
		this.userId = AC.getUserId();
		Intent i = getIntent();
		this.eventId = i.getStringExtra("event_id");
		
		tvNoFriends = (TextView)findViewById(R.id.tvNoFriends);
		// Load friends
		new LoadAllFriendsForInvite().execute();
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
				
		tvTitle.setText("Invite Connections");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
					
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				}
		});
		
		//Set-up listview
		lvFriendsToInvite = (ListView) findViewById(R.id.lvFriends);
		View headerView =  ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.invitelist_header, null, false);
        lvFriendsToInvite.addHeaderView(headerView);
		lvFriendsToInvite.setAdapter(new UserInviteAdapter(FriendsInvite.this, friendsToInviteList));
		btnInvite = (Button)findViewById(R.id.btnInviteFriends);
		
		btnInvite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AC.isOnline(FriendsInvite.this)){
					new SendInvite().execute();
					Toast.makeText(FriendsInvite.this, "Successfully invited selected connections!", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(FriendsInvite.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
			}
		});

		cbSelectAll = (CheckBox)findViewById(R.id.cbSelectAll);
		cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				for(int i=1; i< lvFriendsToInvite.getCount(); i++){
					CheckBox cb = (CheckBox)lvFriendsToInvite.getChildAt(i).findViewById(R.id.cbInviteUser);
					cb.setChecked(isChecked);
				}
			}
		});
		
		
	}
	
	// Classes for database query
	class LoadAllFriendsForInvite extends AsyncTask<String, String, String> {
			
		Bitmap bitmap = null;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
	    	pDialog = new ProgressDialog(FriendsInvite.this);
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

			// Getting JSON object
			JSONObject json = jParser.makeHttpRequest(url_get_friends, params);

			// Check for success tag
			try {
				int success = json.getInt("success");
				if (success == 1) {

					//Get array of products
					users = json.getJSONArray("friends");
					countOfFriends = users.length();
					friendsToInviteList = new ArrayList<User>();
						
					//Looping through all users
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
						
						// Create new user object
						User friend = new User(userId, username, name, birthday, hobbies, eventCount, bitmap, "friends");
							
						//Adding friends to list of friends to display
						friendsToInviteList.add(friend);
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
				tvTitle.setText("Friends to Invite");
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

	class SendInvite extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
	    	pDialog = new ProgressDialog(FriendsInvite.this);
	    	pDialog.setMessage("Inviting Friends...");
	    	pDialog.setIndeterminate(false);
	    	pDialog.setCancelable(false);
	    	pDialog.show();
		}
			
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Retrieve friends that are invited
			ArrayList<String> invitedUserIds = new ArrayList<String>();
			for(int i=0; i < friendsToInviteList.size(); i++){
				if(friendsToInviteList.get(i).invited == true)
					invitedUserIds.add(friendsToInviteList.get(i).userId);		
			}
			JSONArray jsonArr = new JSONArray(invitedUserIds);
			// Build parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("event_id", eventId));
			params.add(new BasicNameValuePair("user_id", userId));
			params.add(new BasicNameValuePair("invited", jsonArr.toString()));

			// Getting JSON object
			JSONObject json = jParser.makeHttpRequest(url_invite_friends, params);

			// Check for success tag
			try {
				int success = json.getInt("success");
				if(success == 1){
					// successfully invited
				} else {
					// failed sending invitations
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
			FriendsInvite.this.finish();
		}
	}
	
	// View Holder
	private class UserCBViewHolder{
		public CheckBox cbInviteUser;
		@SuppressWarnings("unused")
		public User user;
		public ImageView ivUserProfPic;
	}
	
	
	// Adapter Class
	public class UserInviteAdapter extends ArrayAdapter<User>{
		
		Context context;
		ArrayList<User> values = null;
		
		public UserInviteAdapter(Context context, ArrayList<User> objects){
			super(context, R.layout.single_usertoinvite, objects);
			this.context = context;
			this.values = objects;
		}
		
		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			final UserCBViewHolder holder;

			if(v == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.single_usertoinvite, parent, false);

				holder = new UserCBViewHolder();
				holder.ivUserProfPic = (ImageView) v.findViewById(R.id.ivListProfPic);
				holder.cbInviteUser = (CheckBox) v.findViewById(R.id.cbInviteUser);
				holder.user = values.get(position);
				holder.cbInviteUser.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						friendsToInviteList.get(position).setInvited(isChecked);
					}
				});
				
				v.setTag(holder);
			} else{
				holder = (UserCBViewHolder) v.getTag();
			}
			
			holder.ivUserProfPic.setImageBitmap(values.get(position).profPic);
			holder.cbInviteUser.setText(values.get(position).name);

			return v;
		}

	}
}
