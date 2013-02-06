package anow.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import anow.datamodels.Event;

public class ActivityProfile extends Activity{

	Event receivedActivity;
	private String activityId;
	TextView tvActName, tvActDate, tvActLoc, tvActTime, tvActDesc;
	Button btnEdit, btnInvite, btnDelete, btnParticipants;
	EditText etActName, etActLoc, etActDesc;
	int updateSuccess;
	String newName, newLoc, newDesc;
	
	//Header Views
	TextView tvTitle;
	Button btnBack, btnSave;
	
	// Create JSON Parser object
	JSONParser jParser = new JSONParser();
	
	//JSON Node names
	private static final String TAG_SUCCESS = "success";
		
	// Progress Dialog
	private ProgressDialog pDialog;
	
	//URLs to php files (retrieve and update)
	private static String url_edit_activity = "http://10.0.2.2/ANowPhp/edit_activity.php";
	private static String url_delete_activity = "http://10.0.2.2/ANowPhp/delete_activity.php";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		this.receiveData();
		this.setup();
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		btnSave = (Button)findViewById(R.id.btnHeader2);
		tvTitle.setText("Activity Profile");
		btnBack.setText("Back");
		
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = getIntent();
				i.putExtra("reloadHome", false);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		btnSave.setText("Save");
		btnSave.setOnClickListener(saveListener);
		
		//Set-up views
		tvActName = (TextView)findViewById(R.id.tvActivityName);
		tvActDate = (TextView)findViewById(R.id.tvActivityDate);
		tvActLoc = (TextView)findViewById(R.id.tvActivityLocation);
		tvActTime = (TextView)findViewById(R.id.tvActivityTime);
		tvActDesc = (TextView)findViewById(R.id.tvActivityDesc);
		etActName = (EditText)findViewById(R.id.etActivityName);
		etActLoc = (EditText)findViewById(R.id.etActivityLocation);
		etActDesc = (EditText)findViewById(R.id.etActivityDesc);
		tvActName.setText(this.receivedActivity.eventName);
		tvActDate.setText(this.receivedActivity.eventDateStart);
		tvActLoc.setText(this.receivedActivity.eventLocation);
		tvActTime.setText(this.receivedActivity.eventTimeStart);
		tvActDesc.setText(this.receivedActivity.eventDescription);
		
		btnInvite = (Button)findViewById(R.id.btnAInvite);
		btnDelete = (Button)findViewById(R.id.btnDeleteActivity);
		btnEdit = (Button)findViewById(R.id.btnEdit);
		btnParticipants = (Button)findViewById(R.id.btnAParticipants);

		btnParticipants.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ActivityProfile.this, Participants.class);
				i.putExtra("event_id", activityId);
				startActivity(i);
			}
		});
		btnInvite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ActivityProfile.this, FriendsInvite.class);
				i.putExtra("event_id", activityId);
				startActivity(i);
			}
		});
		btnEdit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				
				btnDelete.setVisibility(View.VISIBLE);
				
				//Activity name
				tvActName.setVisibility(View.GONE);
				etActName.setVisibility(View.VISIBLE);
				etActName.setText(tvActName.getText());
				
				//Activity location
				tvActLoc.setVisibility(View.GONE);
				etActLoc.setVisibility(View.VISIBLE);
				etActLoc.setText(tvActLoc.getText());
				
				//Activity description
				tvActDesc.setVisibility(View.GONE);
				etActDesc.setVisibility(View.VISIBLE);
				etActDesc.setText(tvActDesc.getText());
				
				//Header buttons
				btnBack.setVisibility(View.GONE);
				btnSave.setVisibility(View.VISIBLE);
			}
		});
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);
				
				alert.setTitle("Delete");
				alert.setMessage("Are you sure you want to delete this activity?");
				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new DeleteActivityProfile().execute();
					}
				});
				alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
				alert.show();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = getIntent();
		i.putExtra("reloadHome", false);
		setResult(RESULT_OK, i);
		finish();
	}
	
	private void receiveData(){
		Intent i = getIntent();
		receivedActivity = (Event)i.getSerializableExtra("activityObject");
		this.activityId = Integer.toString(receivedActivity.eventId);
	}

	View.OnClickListener saveListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			//for missing fields during editing
			if(etActName.getText().toString().isEmpty() ||
			   etActLoc.getText().toString().isEmpty() ||
			   etActDesc.getText().toString().isEmpty())
				Toast.makeText(ActivityProfile.this, "Required field(s) is missing!", Toast.LENGTH_SHORT).show();
			else
			{
				//Set Activity name				
				newName = etActName.getText().toString();
				tvActName.setText(etActName.getText());
				tvActName.setVisibility(View.VISIBLE);
				etActName.setVisibility(View.GONE);
				
				//Set Activity location
				newLoc = etActLoc.getText().toString();
				tvActLoc.setText(etActLoc.getText());
				tvActLoc.setVisibility(View.VISIBLE);
				etActLoc.setVisibility(View.GONE);
				
				//Set Activity description
				newDesc = etActDesc.getText().toString();
				tvActDesc.setText(etActDesc.getText());
				tvActDesc.setVisibility(View.VISIBLE);
				etActDesc.setVisibility(View.GONE);
			}	
			
			//Buttons
			btnSave.setVisibility(View.GONE);
			btnBack.setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.GONE);
			
			//Update database based on tv's
			updateSuccess = 0;
			new UpdateActivityProfile().execute();
		}
	};
	
	class UpdateActivityProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ActivityProfile.this);
			pDialog.setMessage("Updating Activty Profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("activity_id", activityId));
			params.add(new BasicNameValuePair("name", newName));
			params.add(new BasicNameValuePair("loc", newLoc));
			params.add(new BasicNameValuePair("desc", newDesc));
			
			// Send modified data through HTTP request
			JSONObject json = jParser.makeHttpRequest(url_edit_activity, params);
			
			// Check json success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					updateSuccess = 1;
				}
			} catch(JSONException e){
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(updateSuccess == 1)
				Toast.makeText(ActivityProfile.this, "Activity profile successfully edited!", Toast.LENGTH_SHORT).show();
		}
	}
	
	class DeleteActivityProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ActivityProfile.this);
			pDialog.setMessage("Deleting this Activity...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("activity_id", activityId));
			
			// Send modified data through HTTP request
			JSONObject json = jParser.makeHttpRequest(url_delete_activity, params);
			
			// Check json success tag
			try{
				int success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					updateSuccess = 1;
				}
			} catch(JSONException e){
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(updateSuccess == 1){
				Intent i = getIntent();
				i.putExtra("reloadHome", true);
				setResult(RESULT_OK, i);
				finish();
			}
		}
	}	
	
}
