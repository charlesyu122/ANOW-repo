package anow.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

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
	String userId;
	EditText etActName, etActLoc, etActDesc;
	DatePicker dpStartDate;
	TimePicker tpStartTime;
	Button btnAddActivity;
	CheckBox cbSetPrivacy;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	int successLog; // checks if the user successfully logs in
	String privateOption = "N"; // sets the added activity to private
	SimpleDateFormat yearMonthDate = new SimpleDateFormat("yyyy-MM-dd");
	
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
		this.userId = i.getStringExtra("user_id");
		this.setup();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = getIntent();
		i.putExtra("reloadHome", false);
		setResult(RESULT_OK, i);
		finish();
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
				int check = checkFields();
				if(check == 1)
					new CreateNewActivity().execute();
				else if(check == 0){
					Toast.makeText(ActivityRegistration.this, "Please fill up missing fields", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	private int checkFields(){
		int ret = 1;
		if(etActName.getText().toString().length() == 0){
			etActName.setError("Activity Name required!");
			ret = 0;
		}
		if(etActLoc.getText().toString().length() == 0){
			etActLoc.setError("Activity Location required!");
			ret = 0;
		}
		if(etActDesc.getText().toString().length() == 0){
			etActDesc.setError("Activity Description required!");
			ret = 0;
		}
		// Check if date is okay
		Date eventDate = null;
		int date = dpStartDate.getDayOfMonth();
		int month = dpStartDate.getMonth() + 1;
		int year = dpStartDate.getYear();
		String eventDateStr = year + "-" + month + "-" + date;
		try {
			eventDate = yearMonthDate.parse(eventDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get date today
		Date d = new Date();
		String todayDateStr = yearMonthDate.format(d.getTime());
		Date currentDate = null;
		try {
			currentDate = yearMonthDate.parse(todayDateStr);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(eventDate.before(currentDate)){
			ret = -1;
			Toast.makeText(ActivityRegistration.this, "The date must be today onwards.", Toast.LENGTH_SHORT).show();
		}
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
			String dateEnd = dateStart;
			
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
			params.add(new BasicNameValuePair("user_id", userId));
			
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
