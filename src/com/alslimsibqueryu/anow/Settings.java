package com.alslimsibqueryu.anow;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.util.Log;
import android.util.Patterns;
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
	Intent i;
	
	// Upload image attribute
	int serverResponseCode = 0;
	
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
		i = getIntent();
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
            
            //new EditPicture().execute();
            // Upload picture to localhost
            startUpload(picturePath);
        }
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		i.putExtra("reloadHome", false);
		setResult(RESULT_OK, i);
		finish();
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
				i.putExtra("logOut", false);
				setResult(RESULT_OK, i);
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
			
			alertU.setPositiveButton("Save", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							updateSuccess = 0;
							AP = (ApplicationController)getApplicationContext();
							username = AP.getUsername();
							final EditText etNewU = (EditText)textEntryViewU.findViewById(R.id.etChangeUsername);
							new_username = etNewU.getText().toString();
							final EditText etP = (EditText)textEntryViewU.findViewById(R.id.etConfirmPasswordU);
							password = hashPassword(etP.getText().toString());
							
							// email validation
							if((new_username.length() == 0 || etP.getText().toString().length() == 0))
								Toast.makeText(Settings.this, "Required field(s) is missing!", Toast.LENGTH_SHORT).show();
							else if(isNotEmailValid(new_username)){
								Toast.makeText(Settings.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
							}else 
								new EditUsername().execute();
						}
					});
			alertU.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

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
			
			alertP.setPositiveButton("Save", new DialogInterface.OnClickListener() {

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
			alertP.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

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
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			
			startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
			break;
		case R.id.btnLogout:
			AlertDialog.Builder alertOut = new AlertDialog.Builder(
					Settings.this);
			alertOut.setTitle("Log out");
			alertOut.setMessage("Are you sure you want to log-out?");
			alertOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stubs
					i.putExtra("logOut", true);
					setResult(RESULT_OK, i);
					finish();
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
	
	boolean isNotEmailValid(CharSequence email){
		return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

	private void startUpload(final String imgPath){
		pDialog = ProgressDialog.show(Settings.this, "Image Upload", "Uploading image...", true);
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				int response = uploadFile(imgPath);
				Log.d("RES: ", ""+response);
			}
		}).start();
	}
	
	public int uploadFile(String sourceFileUri) {
        String upLoadServerUri = "http://10.0.2.2/ANowPhp/upload_image.php";
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        if (!sourceFile.isFile()) {
         Log.e("uploadFile", "Source File Does not exist");
         pDialog.dismiss();
         return 0;
        }
            try { // open a URL connection to the Servlet
             FileInputStream fileInputStream = new FileInputStream(sourceFile);
             URL url = new URL(upLoadServerUri);
             conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("uploaded_file", fileName); 
             dos = new DataOutputStream(conn.getOutputStream());
   
             dos.writeBytes(twoHyphens + boundary + lineEnd); 
             dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
             dos.writeBytes(lineEnd);
   
             bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
   
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];
   
             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
             while (bytesRead > 0) {
               dos.write(buffer, 0, bufferSize);
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
              }
   
             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
   
             // Responses from the server (code and message)
             serverResponseCode = conn.getResponseCode();
             String serverResponseMessage = conn.getResponseMessage();
              
             Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
             if(serverResponseCode == 200){
                 runOnUiThread(new Runnable() {
                      public void run() {
                          Toast.makeText(Settings.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                      }
                  });                
             }    
             
             //close the streams //
             fileInputStream.close();
             dos.flush();
             dos.close();
              
        } catch (MalformedURLException ex) {  
            pDialog.dismiss();  
            ex.printStackTrace();
            Toast.makeText(Settings.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
        } catch (Exception e) {
        	pDialog.dismiss();  
            e.printStackTrace();
            Toast.makeText(Settings.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
        }
        pDialog.dismiss();       
        return serverResponseCode;  
       } 
}
