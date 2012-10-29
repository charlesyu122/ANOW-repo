package com.alslimsibqueryu.anow;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends Activity{

	//Header Views
	TextView headerTitle;
	Button headerButton;
	
	//Activity Views
	EditText etEmail, etFName, etLName, etPassword, etCPassword, etHobbies;
	DatePicker dpBirthday;
	Button regButton;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	// url to create a new account for user
	private static String url_create_user = "http://10.0.2.2/ANowPhp/create_user.php";
	
	// JSON tags
	private static final String TAG_SUCCESS = "success";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		this.setup();
	}
	
	private void setup(){
		// Setup header views
		headerTitle = (TextView)findViewById(R.id.tvTitle);
		headerButton = (Button)findViewById(R.id.btnHeader);
		headerTitle.setText("Registration");
		headerButton.setVisibility(View.INVISIBLE);
		
		// Setup Activity Views
		etEmail = (EditText)findViewById(R.id.etEmail);
		etFName = (EditText)findViewById(R.id.etFname);
		etLName = (EditText)findViewById(R.id.etLname);
		etPassword = (EditText)findViewById(R.id.etPass);
		etCPassword = (EditText)findViewById(R.id.etConPass);
		etHobbies = (EditText)findViewById(R.id.etHobbies);
		dpBirthday = (DatePicker)findViewById(R.id.dPicker);
		
		regButton = (Button)findViewById(R.id.btnRegSubmit);
		regButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(checkFields()){
				case 1: 
					// No missing fields and passwords match
					Toast.makeText(Registration.this, "You have successfully registered! Please Log-in.", Toast.LENGTH_SHORT).show();
					new CreateNewUser().execute();
					break;
				case 2:
					// Missing field
					Toast.makeText(Registration.this, "Please fill up missing fields", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					// Passwords doesn't match
					Toast.makeText(Registration.this, "Passwords entered do not match", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		});
	}
	
	private int checkFields(){
		int ret = 0;
		if(etEmail.getText().toString().matches("") || etPassword.getText().toString().matches("") || etFName.getText().toString().matches("") || 
		   etLName.getText().toString().matches("") || etHobbies.getText().toString().matches("") || etCPassword.getText().toString().matches(""))
			ret = 2;
		else if( !(etPassword.getText().toString().matches(etCPassword.getText().toString())) )
			ret = 3;
		else 
			ret = 1;
		return ret;
	}
	
	private String hashPassword(String password){
		String hashed = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte byteData[] = md.digest();
			
			StringBuffer sb = new StringBuffer();
			for(int i=0; i < byteData.length; i++)
				 sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			hashed = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hashed;
	}
	
	// Background Async Task to Create New User
	class CreateNewUser extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Registration.this);
			pDialog.setMessage("Creating your account...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Get values
			String username = etEmail.getText().toString();
			String password = hashPassword(etPassword.getText().toString());
			String name = etFName.getText().toString() + " " + etLName.getText().toString();
			int day = dpBirthday.getDayOfMonth();
			int month = dpBirthday.getMonth() + 1;
			int year = dpBirthday.getYear();
			String birthday = month + "." + day + "." + year;
			String hobbies = etHobbies.getText().toString();
			
			// Building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("birthday", birthday));
			params.add(new BasicNameValuePair("hobbies", hobbies));
			
			// Getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_create_user, params);
			
			// Check for success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					// account successfully created
					Intent i = new Intent(Registration.this, MainActivity.class);
					startActivity(i);
					finish();
				}else{
					//failed to create
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}
		
	}
}
