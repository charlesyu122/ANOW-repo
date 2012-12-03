package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class Friends extends Activity{

	ListView lvFriends;
	String[] namesOfFriends;
	User[] arrayOfFriends;
	
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Side index attributes
	private GestureDetector mGestureDetector;
	private static float sideIndexX;
	private static float sideIndexY;
	private int sideIndexHeight;
	private int indexListSize;
	private ArrayList<Object[]> indexList = null;
	
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
		
		// Retrieve username
		ApplicationController AC = (ApplicationController)getApplicationContext();
		this.username = AC.getUsername();
		
		// Load friends
		new LoadAllFriends().execute();
	}
	
	
	private void transferNames(){
		this.namesOfFriends = new String[arrayOfFriends.length];
		for(int i=0; i<this.arrayOfFriends.length; i++)
			this.namesOfFriends[i] = this.arrayOfFriends[i].name;
	}
	
	private void setup(){
		//Set-up header views
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
		//Set-up views
		lvFriends = (ListView) findViewById(R.id.lvFriends);
		lvFriends.setAdapter(new UserAdapter(Friends.this, arrayOfFriends, 'F'));
		lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(Friends.this, "Go to profile",Toast.LENGTH_SHORT).show();
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
			
			// Convert list to array
			arrayOfFriends = friendsList.toArray(new User[friendsList.size()]);
			// Set up for Side Index
			transferNames();
			Arrays.sort(arrayOfFriends, User.UserNameComparator);
			Arrays.sort(namesOfFriends);
			setup();
			// Side Index
			mGestureDetector = new GestureDetector(Friends.this,new SideIndexGestureListener());
		}
	}
	
	
	
	// Methods for side index
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return false;
		}
	}

	private ArrayList<Object[]> createIndex(String[] strArr) {
		ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
		Object[] tmpIndexItem = null;

		int tmpPos = 0;
		String tmpLetter = "";
		String currentLetter = null;
		String strItem = null;

		for (int j = 0; j < strArr.length; j++) {
			strItem = strArr[j];
			currentLetter = strItem.substring(0, 1);

			// every time new letters comes
			// save it to index list
			if (!currentLetter.equals(tmpLetter)) {
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = tmpLetter;
				tmpIndexItem[1] = tmpPos - 1;
				tmpIndexItem[2] = j - 1;

				tmpLetter = currentLetter;
				tmpPos = j + 1;

				tmpIndexList.add(tmpIndexItem);
			}
		}

		// save also last letter
		tmpIndexItem = new Object[3];
		tmpIndexItem[0] = tmpLetter;
		tmpIndexItem[1] = tmpPos - 1;
		tmpIndexItem[2] = strArr.length - 1;
		tmpIndexList.add(tmpIndexItem);

		// and remove first temporary empty entry
		if (tmpIndexList != null && tmpIndexList.size() > 0) {
			tmpIndexList.remove(0);
		}

		return tmpIndexList;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		LinearLayout sideIndex = (LinearLayout) findViewById(R.id.friendsSideIndex);
		sideIndexHeight = sideIndex.getHeight();
		sideIndex.removeAllViews();

		// TextView for every visible item
		TextView tmpTV = null;

		// we'll create the index list
		indexList = createIndex(namesOfFriends);

		// number of items in the index List
		indexListSize = indexList.size();

		// maximal number of item, which could be displayed
		int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);

		int tmpIndexListSize = indexListSize;

		// handling that case when indexListSize > indexMaxSize
		while (tmpIndexListSize > indexMaxSize) {
			tmpIndexListSize = tmpIndexListSize / 2;
		}

		// computing delta (only a part of items will be displayed to save a
		// place)
		double delta = indexListSize / tmpIndexListSize;

		String tmpLetter = null;
		Object[] tmpIndexItem = null;

		// show every m-th letter
		for (double i = 1; i <= indexListSize; i = i + delta) {
			tmpIndexItem = indexList.get((int) i - 1);
			tmpLetter = tmpIndexItem[0].toString();
			tmpTV = new TextView(this);
			tmpTV.setText(tmpLetter);
			tmpTV.setGravity(Gravity.CENTER);
			tmpTV.setTextSize(20);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 1);
			tmpTV.setLayoutParams(params);
			sideIndex.addView(tmpTV);
		}

		// and set a touch listener for it
		sideIndex.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();
				return false;
			}
		});
	}

	class SideIndexGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// we know already coordinates of first touch
			// we know as well a scroll distance
			sideIndexX = sideIndexX - distanceX;
			sideIndexY = sideIndexY - distanceY;

			// when the user scrolls within our side index
			// we can show for every position in it a proper
			// item in the country list
			if (sideIndexX >= 0 && sideIndexY >= 0) {
				displayListItem();
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	public void displayListItem() {
		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		// compute minimal position for the item in the list
		int minPosition = (int) (itemPosition * pixelPerIndexItem);

		// get the item (we can do it since we know item index)
		Object[] indexItem = indexList.get(itemPosition);

		// and compute the proper item in the country list
		int indexMin = Integer.parseInt(indexItem[1].toString());
		int indexMax = Integer.parseInt(indexItem[2].toString());
		int indexDelta = Math.max(1, indexMax - indexMin);

		double pixelPerSubitem = pixelPerIndexItem / indexDelta;
		int subitemPosition = (int) (indexMin + (sideIndexY - minPosition)
				/ pixelPerSubitem);

		ListView listView = (ListView) findViewById(R.id.lvFriends);
		listView.setSelection(subitemPosition);
	}
}
