package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ActivityRegistration extends Activity{
	// Header Views
	TextView tvHeaderTitle;
	Button btnHeaderBackBut;
	
	// Activity Views
	String username;
	EditText etActName, etActLoc, etActDesc;
	DatePicker dpStartDate, dpEndDate;
	TimePicker tpStartTime;
	Button btnAddActivity;
	CheckBox cbSetPrivacy;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	int successLog; // checks if the user successfully logs in
	String privateOption = "N"; // sets the added activity to private
	
	// url to create a new account for user
	private static String url_create_activity = "http://10.0.2.2/ANowPhp/create_activity.php";
		
	// JSON tags
	private static final String TAG_SUCCESS = "success";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.setup();
	}
	
	private void setup(){
		// Set-up Header Views
		tvHeaderTitle = (TextView)findViewById(R.id.tvTitle);
		tvHeaderTitle.setText("Add an Activity");
		btnHeaderBackBut = (Button) findViewById(R.id.btnHeader);
		btnHeaderBackBut.setText("Back");
		btnHeaderBackBut.setOnClickListener(new View.OnClickListener() {
					
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = getIntent();
				i.putExtra("addActivity", false);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		
		//Set-up views
		etActName = (EditText)findViewById(R.id.etActName);
		etActLoc = (EditText)findViewById(R.id.etActLoc);
		etActDesc = (EditText)findViewById(R.id.etActDesc);
		dpStartDate = (DatePicker)findViewById(R.id.dpStartDate);
		dpEndDate = (DatePicker)findViewById(R.id.dpEndDate);
		tpStartTime = (TimePicker)findViewById(R.id.tpStartTime);
		btnAddActivity = (Button) findViewById(R.id.btnActRegSubmit);
		cbSetPrivacy = (CheckBox)findViewById(R.id.cbSetActivityPrivate);
		
		cbSetPrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//set private to yes
					privateOption = "Y";
				}
			}
		});
		
		btnAddActivity.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				successLog = 0;
				if(checkFields() == 1)
					new CreateNewActivity().execute();
				else{
					Toast.makeText(ActivityRegistration.this, "Please fill up missing fields", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	private int checkFields(){
		int ret = 1;
		if(etActName.getText().toString().matches("") || etActLoc.getText().toString().matches("") || etActDesc.getText().toString().matches(""))
			ret = 0;
		return ret;
	}
	
	// Background Async Task to Create New User
	class CreateNewActivity extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ActivityRegistration.this);
			pDialog.setMessage("Organizing your activity...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			// Get values
			String name = etActName.getText().toString();
			String location = etActLoc.getText().toString();
			String description = etActDesc.getText().toString();
			
			int date = dpStartDate.getDayOfMonth();
			int month = dpStartDate.getMonth() + 1;
			int year = dpStartDate.getYear();
			String dateStart = year + "-" + month + "-" + date;
			
			date = dpEndDate.getDayOfMonth();
			month = dpEndDate.getMonth() + 1;
			year = dpEndDate.getYear();
			String dateEnd = year + "-" + month + "-" + date;
			
			int hour = tpStartTime.getCurrentHour();
			int min = tpStartTime.getCurrentMinute();
			String timeStart = hour + ":" + min;
			
			// Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eventName", name));
			params.add(new BasicNameValuePair("timeStart", timeStart));
			params.add(new BasicNameValuePair("dateStart", dateStart));
			params.add(new BasicNameValuePair("dateEnd", dateEnd));
			params.add(new BasicNameValuePair("location", location));
			params.add(new BasicNameValuePair("description", description));
			params.add(new BasicNameValuePair("private", privateOption));
			params.add(new BasicNameValuePair("username", username));
			
			// Getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_create_activity, params);
			
			// Check for success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					// account successfully created
					successLog = 1;
				}else{
					//failed to create
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(successLog == 1){
				Intent i = getIntent();
				i.putExtra("addActivity", true);
				i.putExtra("reloadHome", true);
				setResult(RESULT_OK, i);
				finish();
			}
		}
	}
}
