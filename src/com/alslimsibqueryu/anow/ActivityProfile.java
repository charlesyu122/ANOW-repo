package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProfile extends Activity{

	Event receivedActivity;
	TextView tvActName, tvActDate, tvActLoc, tvActTime, tvActDesc;
	Button btnEdit, btnInvite, btnDelete;
	EditText etActName, etActLoc, etActDesc;
	//Header Views
	TextView tvTitle;
	Button btnBack, btnSave;
	
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
				finish();
			}
		});
		btnSave.setText("Save");
		btnSave.setOnClickListener(saveListen);
		
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
		btnInvite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ActivityProfile.this, Friends.class));
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
						Toast.makeText(ActivityProfile.this, "Successfully deleted!", Toast.LENGTH_SHORT).show();
						finish();
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
	
	private void receiveData(){
		Intent i = getIntent();
		receivedActivity = (Event)i.getSerializableExtra("activityObject");
	}
	
	View.OnClickListener saveListen = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Set Activity name
			tvActName.setText(etActName.getText());
			tvActName.setVisibility(View.VISIBLE);
			etActName.setVisibility(View.GONE);
			//Set Activity location
			tvActLoc.setText(etActLoc.getText());
			tvActLoc.setVisibility(View.VISIBLE);
			etActLoc.setVisibility(View.GONE);
			//Set Activity description
			tvActDesc.setText(etActDesc.getText());
			tvActDesc.setVisibility(View.VISIBLE);
			etActDesc.setVisibility(View.GONE);
			//Buttons
			btnSave.setVisibility(View.GONE);
			btnBack.setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.GONE);
			//Update database based on tv's
		}
	};
}
