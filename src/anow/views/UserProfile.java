package anow.views;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import anow.datamodels.User;

public class UserProfile extends Activity {

	// Attributes
	private String type; // user or friend
	private Boolean needToReload = false;
	private String server = "http://atnow.net84.net/";
	String userId;
	int updateSuccess;
	ApplicationController AC;

	// Header Views
	Button btnSettings, btnSave;
	TextView headerTitle;
	int btnHeaderStatus = 0;

	// Activity Views
	ImageView ivProfPic;
	TextView tvProfName, tvProfBday, tvProfInterest, tvUsername, tvEventCount;
	EditText etProfName, etProfInterest;
	Button btnCalendar, btnFriends, btnInvites;
	ProgressBar pbLoading;

	// Progress Dialog
	private ProgressDialog pDialog;

	// Create JSON Parser object
	JSONParser jParser = new JSONParser();

	// User object to store
	User myProfile;
	ArrayList<HashMap<String, String>> userMatched;

	// URLs to php files (retrieve and update)
	private static String url_get_user_profile = "http://atnow.net84.net/ANowPhp/get_user_profile.php";
	private static String url_edit_user_profile = "http://atnow.net84.net/ANowPhp/edit_profile.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";

	// products JSONArray
	JSONArray user = null;

	// Information to display
	String name, username, eventCount, birthday, hobbies, imgDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		AC = (ApplicationController) getApplicationContext();

		// Get type of profile and user id
		Intent i = getIntent();
		this.type = i.getStringExtra("type");

		if (type.equals("user")) {
			ApplicationController AC = (ApplicationController) getApplicationContext();
			this.userId = AC.getUserId();
		} else if (type.equals("friend")) {
			this.userId = i.getStringExtra("user_id");
		}

		// Initialize
		this.myProfile = new User();
		this.userMatched = new ArrayList<HashMap<String, String>>();

		// Set-up views
		this.setup();

		// Load user profile in Background Thread
		new LoadUserProfile().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 2) {
			setResult(2);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = getIntent();
		if (needToReload == true)
			i.putExtra("reloadHome", true);
		setResult(RESULT_OK, i);
		finish();
	}

	private void setup() {
		// Set-up header
		headerTitle = (TextView) findViewById(R.id.tvTitle);
		btnSettings = (Button) findViewById(R.id.btnHeader);
		btnSave = (Button) findViewById(R.id.btnHeader2);
		if (type.equals("user")) {
			btnSettings.setText("Back");
			headerTitle.setText("My Profile");
		} else {
			btnSettings.setText("Back");
			headerTitle.setText("Profile");
		}
		btnSave.setText("Save");
		btnSettings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type.equals("user")) {
					Intent i = getIntent();
					if (needToReload == true)
						i.putExtra("reloadHome", true);
					setResult(RESULT_OK, i);
					finish();
				} else if (type.equals("friend")) {
					finish();
				}
			}
		});

		// Set-up profile views
		ivProfPic = (ImageView) findViewById(R.id.ivProfPic);
		pbLoading = (ProgressBar) findViewById(R.id.pbProfPicLoading);
		tvUsername = (TextView) findViewById(R.id.tvProfUname);
		tvEventCount = (TextView) findViewById(R.id.tvEventCount);
		tvProfName = (TextView) findViewById(R.id.tvProfName);
		etProfName = (EditText) findViewById(R.id.etProfName);
		tvProfBday = (TextView) findViewById(R.id.tvProfBday);
		tvProfInterest = (TextView) findViewById(R.id.tvProfInterest);
		etProfInterest = (EditText) findViewById(R.id.etProfInterest);
		btnCalendar = (Button) findViewById(R.id.btnCalendar);
		btnFriends = (Button) findViewById(R.id.btnFriends);
		btnInvites = (Button) findViewById(R.id.btnInvites);
		if (type.equals("friend")) {
			btnInvites.setVisibility(View.GONE);
			btnCalendar.setText("View Calendar");
		}

		btnCalendar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type.equals("user")) {
					Intent i = getIntent();
					if (needToReload == true)
						i.putExtra("reloadHome", true);
					setResult(RESULT_OK, i);
					finish();
				} else if (type.equals("friend")) {
					Intent i = new Intent(UserProfile.this, UserCalendar.class);
					i.putExtra("user_id", userId);
					i.putExtra("name", name);
					startActivityForResult(i, 1);
				}
			}
		});
		btnFriends.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AC.isOnline(UserProfile.this)) {
					Intent i = new Intent(UserProfile.this, Friends.class);
					i.putExtra("type", type);
					if (type.equals("friend")) {
						i.putExtra("friend_user_id", userId);
					}
					startActivity(i);
				} else
					Toast.makeText(UserProfile.this,
							"Please connect to the internet",
							Toast.LENGTH_SHORT).show();
			}
		});
		btnInvites.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AC.isOnline(UserProfile.this)) {
					needToReload = true;
					startActivity(new Intent(UserProfile.this, Invites.class));
				} else
					Toast.makeText(UserProfile.this,
							"Please connect to the internet",
							Toast.LENGTH_SHORT).show();
			}
		});

		View.OnLongClickListener listen = new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (AC.isOnline(UserProfile.this)) {
					if (((TextView) v).getId() == tvProfName.getId()) {
						tvProfName.setVisibility(View.GONE);
						etProfName.setText(tvProfName.getText());
						etProfName.setVisibility(View.VISIBLE);
					} else if (((TextView) v).getId() == tvProfInterest.getId()) {
						tvProfInterest.setVisibility(View.GONE);
						etProfInterest.setText(tvProfInterest.getText());
						etProfInterest.setVisibility(View.VISIBLE);
					}
					if (btnSave.getVisibility() == View.GONE) {
						btnSave.setVisibility(View.VISIBLE);
						btnSettings.setVisibility(View.GONE);
					}
				} else
					Toast.makeText(UserProfile.this,
							"Please connect to the internet",
							Toast.LENGTH_SHORT).show();
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

		if (type.equals("user")) {
			tvProfName.setOnLongClickListener(listen);
			tvProfBday.setOnLongClickListener(listen);
			tvProfInterest.setOnLongClickListener(listen);
			btnSave.setOnClickListener(savelisten);
		}
	}

	class LoadUserProfile extends AsyncTask<String, String, String> {

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

			// Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", userId));

			// Getting JSONObject from url
			JSONObject json = jParser.makeHttpRequest(url_get_user_profile, params);

			// Check log cat for JSON response
			Log.d("User received:", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// User is found
					// Get user
					user = json.getJSONArray(TAG_USER);

					if (user.length() == 1) {
						JSONObject temp = user.getJSONObject(0);
						// Store each json item in variable
						name = temp.getString("name");
						username = temp.getString("username");
						birthday = temp.getString("birthday");
						hobbies = temp.getString("hobbies");
						eventCount = temp.getString("event_count");
						imgDir = temp.getString("profile_image");
					}
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
			ivProfPic.setVisibility(View.INVISIBLE);
			pbLoading.setVisibility(View.VISIBLE);
			tvUsername.setText(username);
			tvEventCount.setText(eventCount);
			tvProfName.setText(name);
			tvProfBday.setText(birthday);
			tvProfInterest.setText(hobbies);
			new ImageLoadTask().execute(server + parseDir(imgDir));
		}

	}

	// Async task to load lazy load images
	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Bitmap b = getBitmapFromURL(params[0]);
				return b;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public Bitmap getBitmapFromURL(String src) {
			try {
				URL url = new URL(src);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result != null) {
				ivProfPic.setImageBitmap(result);
				ivProfPic.setVisibility(View.VISIBLE);
				pbLoading.setVisibility(View.GONE);
			} else {
				Log.d("ImageLoadTask", "Failed to load " + name + " image");
			}
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
			params.add(new BasicNameValuePair("user_id", userId));
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("hobbies", hobbies));

			// Send modified data through HTTP request
			JSONObject json = jParser.makeHttpRequest(url_edit_user_profile,
					params);

			// Check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					updateSuccess = 1;
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
			if (updateSuccess == 1)
				Toast.makeText(UserProfile.this,
						"Profile successfully edited!", Toast.LENGTH_SHORT)
						.show();
		}
	}
}
