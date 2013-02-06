package anow.views;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import android.util.Patterns;
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
	int successLog; // checks if the user successfully logs in
	
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
		headerButton.setText("Cancel");
		
		headerButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Registration.this, MainActivity.class));
			}
		});
		
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
				successLog = 0;
				int check = checkFields();
				if(check == 1){
					new CreateNewUser().execute();
				} else if(check ==0){
					Toast.makeText(Registration.this, "Please fill up missing fields", Toast.LENGTH_SHORT).show();
				}	
			}
		});
	}
	
	private int checkFields(){
		int ret = 1;
		// Check for empty fields
		if(etEmail.getText().toString().length() == 0){
			ret = 0;
			etEmail.setError("Username/E-mail address is required!");
		}
		if(etFName.getText().toString().length() == 0){
			ret = 0;
			etFName.setError("First name is required!");
		}
		if(etLName.getText().toString().length() == 0){
			ret = 0;
			etLName.setError("Last name is required!");
		}
		if(etPassword.getText().toString().length() ==0){
			ret = 0;
			etPassword.setError("Password is required!");
		}
		if(etHobbies.getText().toString().length() == 0){
			ret = 0;
			etHobbies.setError("Hobbies are required!");
		}
		// Other validations
		if(!((etPassword.getText().toString()).equals(etCPassword.getText().toString())) && ret == 1){
			etPassword.setError("Passwords do not match");
			etCPassword.setError("Passwords do not match");
			Toast.makeText(Registration.this, "Please make sure the passwords match.", Toast.LENGTH_SHORT).show();
			ret = -1;
		}
		if(isNotEmailValid(etEmail.getText())){
			etEmail.setError("Email address is not valid");
			Toast.makeText(Registration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
			ret = -1;
		}

		return ret;
	}
	
	boolean isNotEmailValid(CharSequence email){
		return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
		
		return hashed.substring(0, 39);
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
			params.add(new BasicNameValuePair("image", "http://localhost/CI/images/profile_images/ic_launcher.png"));
			
			// Getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_create_user, params);
			
			// Check for success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					// account successfully created
					successLog = 1;
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
			if(successLog == 1){
				Toast.makeText(Registration.this, "You have successfully registered! Please Log-in.", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Registration.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}
		
	}
}
