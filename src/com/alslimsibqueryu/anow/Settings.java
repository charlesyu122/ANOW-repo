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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {

	Button btnChangeUname, btnChangePword, btnChangeProfPic, btnLogout;
	View textEntryViewU, textEntryViewP;
	int updateSuccess;
	String username, new_username, new_password, password, picturePath;
	ApplicationController AP;
	
	// Header views
	TextView tvTitle;
	Button btnBack;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	// url to create a new account for user
	private static String url_edit_username_password = "http://10.0.2.2/ANowPhp/edit_username_password.php";
	private static String url_edit_picture = "http://10.0.2.2/ANowPhp/edit_picture.php";
	
	//JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final int RESULT_LOAD_IMAGE = 1;
		
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		this.setup();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
         
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            new EditPicture().execute();
        }
	}


	private void setup() {
		// Set-up views
		btnChangeUname = (Button) findViewById(R.id.btnChangeUname);
		btnChangePword = (Button) findViewById(R.id.btnChangePassword);
		btnChangeProfPic = (Button) findViewById(R.id.btnChangeProfPic);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnChangeUname.setOnClickListener(this);
		btnChangePword.setOnClickListener(this);
		btnChangeProfPic.setOnClickListener(this);
		btnLogout.setOnClickListener(this);

		// Set-up header views
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnBack = (Button) findViewById(R.id.btnHeader);
		tvTitle.setText("Settings");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnChangeUname:
			LayoutInflater factoryU = LayoutInflater.from(Settings.this);
			textEntryViewU = factoryU.inflate(R.layout.change_username, null);
			AlertDialog.Builder alertU = new AlertDialog.Builder(Settings.this);
			alertU.setTitle("Change email address:");
			alertU.setView(textEntryViewU);
			
			alertU.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							updateSuccess = 0;
							AP = (ApplicationController)getApplicationContext();
							username = AP.getUsername();
							final EditText etNewU = (EditText)textEntryViewU.findViewById(R.id.etChangeUsername);
							new_username = etNewU.getText().toString();
							final EditText etP = (EditText)textEntryViewU.findViewById(R.id.etConfirmPasswordU);
							password = hashPassword(etP.getText().toString());
							
							if((new_username.length() == 0 || etP.getText().toString().length() == 0))
								Toast.makeText(Settings.this, "Required field(s) is missing!", Toast.LENGTH_SHORT).show();
							else
								new EditUsername().execute();
						}
					});
			alertU.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
			alertU.show();
			break;
		case R.id.btnChangePassword:
			LayoutInflater factoryP = LayoutInflater.from(Settings.this);
			textEntryViewP = factoryP.inflate(R.layout.change_password, null);
			AlertDialog.Builder alertP = new AlertDialog.Builder(Settings.this);
			alertP.setTitle("Change password:");
			alertP.setView(textEntryViewP);
			
			alertP.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							updateSuccess = 0;
							AP = (ApplicationController)getApplicationContext();
							username = AP.getUsername();
							final EditText etNewP = (EditText)textEntryViewP.findViewById(R.id.etChangePassword);
							new_password = hashPassword(etNewP.getText().toString());
							final EditText etP = (EditText)textEntryViewP.findViewById(R.id.etConfirmPasswordP);
							password = hashPassword(etP.getText().toString());
							
							if((new_password.length() == 0 || etP.getText().toString().length() == 0))
								Toast.makeText(Settings.this, "Required field(s) is missing!", Toast.LENGTH_SHORT).show();
							else
								new EditPassword().execute();
						}
					});
			alertP.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
			alertP.show();
			break;
		case R.id.btnChangeProfPic:
			updateSuccess = 0;
			AP = (ApplicationController)getApplicationContext();
			username = AP.getUsername();
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, 
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			
			//photoPickerIntent.setType("image/*");
			//startActivityForResult(photoPickerIntent, 1);
			startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
			break;
		case R.id.btnLogout:
			AlertDialog.Builder alertOut = new AlertDialog.Builder(
					Settings.this);
			alertOut.setTitle("Log out");
			alertOut.setMessage("Are you sure you want to log-out?");
			alertOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					Intent i = new Intent(Settings.this, MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			});
			alertOut.setNegativeButton("No", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				}
			});
			alertOut.show();
			break;
		}
		
	}
	
	class EditUsername extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Settings.this);
			pDialog.setMessage("Updating Username...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("new_username", new_username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("username", username));
			
			// Send modified data through HTTP request
			JSONObject json = jsonParser.makeHttpRequest(url_edit_username_password, params);
						
			// Check json success tag
			try
			{
				if(json.getInt(TAG_SUCCESS) == 1)
					updateSuccess = 1;
				
			} 
			catch(JSONException e){
				e.printStackTrace();
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			
			if(updateSuccess == 1)
			{
				Toast.makeText(Settings.this, "Username successfully edited!", Toast.LENGTH_SHORT).show();
				AP.setUsername(new_username);
			}
			else
				Toast.makeText(Settings.this, "Confirm password is incorrect!", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	class EditPassword extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Settings.this);
			pDialog.setMessage("Updating Password...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("new_password", new_password));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("username", username));
			
			// Send modified data through HTTP request
			JSONObject json = jsonParser.makeHttpRequest(url_edit_username_password, params);
						
			// Check json success tag
			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				if(success == 1)
					updateSuccess = 1;
				
			} 
			catch(JSONException e){
				e.printStackTrace();
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(updateSuccess == 1)
				Toast.makeText(Settings.this, "Password successfully edited!", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(Settings.this, "Confirm password is incorrect!", Toast.LENGTH_SHORT).show();
		}
		
	}	

	class EditPicture extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Settings.this);
			pDialog.setMessage("Processing your Profile Picture...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("picturePath", picturePath));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("username", username));
			
			// Send modified data through HTTP request
			JSONObject json = jsonParser.makeHttpRequest(url_edit_picture, params);
			
			// Check json success tag
			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				if(success == 1)
					updateSuccess = 1;
				
			} 
			catch(JSONException e){
				e.printStackTrace();
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			
			if(updateSuccess == 1)
				Toast.makeText(Settings.this, "Picture successfully uploaded! Path is "+picturePath, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(Settings.this, "Cannot upload your photo. Path is "+picturePath, Toast.LENGTH_LONG).show();
		}
		
	}	
}
