package anow.views;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alslimsibqueryu.anow.ApplicationController;
import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Activity Views
	EditText etUsername, etPassword;
	Button btnLogin;
	Button btnRegister;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	int successIn; // to check if user successfully logged in for notifications
	ApplicationController AC;
	
	// url to log in user
	private static String url_login_user = "http://atnow.net84.net/ANowPhp/login_user.php";
	
	// JSON tags
	private static final String TAG_SUCCESS = "success";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.AC = (ApplicationController)getApplicationContext();
        this.setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void setup(){
    	// Setup views
    	etUsername = (EditText)findViewById(R.id.etLoginUsername);
    	etPassword = (EditText)findViewById(R.id.etLoginPassword);
		btnRegister = (Button)findViewById(R.id.btnRegister);
		btnLogin = (Button)findViewById(R.id.btnLogin);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AC.isOnline(MainActivity.this)){
					successIn = 0;
					new LoginUser().execute();
				} else{
					Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, Registration.class));
				finish();
			}
		});
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
    
    // Background Async Task to Login User
    class LoginUser extends AsyncTask<String, String, String>{

    	String userId;
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		pDialog = new ProgressDialog(MainActivity.this);
    		pDialog.setMessage("Logging in...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
    	
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Get values
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			password = hashPassword(password);
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			
			// Getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_login_user, params);
			
			// Check for success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					// account successfully logged in
					successIn = 1;
					userId = json.getString("user_id");
				}else{
					// fail to login
					successIn = 0;
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
			if(successIn == 0)
				Toast.makeText(MainActivity.this, "Invalid username/password", Toast.LENGTH_SHORT).show();
			else{
				Intent i = new Intent(MainActivity.this, Home.class);
				// Store user id in App Controller
				ApplicationController AP = (ApplicationController)getApplicationContext();
				AP.setUserId(userId);		
				startActivity(i);
			}
		}
    }
}
