package com.alslimsibqueryu.anow;

import android.app.Activity;
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

	EditText etActName, etActLoc, etActDesc;
	DatePicker dpStartDate;
	TimePicker tpStartTime;
	Button btnAddActivity;
	CheckBox cbSetPrivacy;
	//Header
	TextView tvHeaderTitle;
	Button btnHeaderBackBut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		this.setup();
	}
	
	private void setup(){
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
				}
			}
		});
		
		btnAddActivity.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(ActivityRegistration.this, "Activity successfully added!", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		//Header Views
		tvHeaderTitle = (TextView)findViewById(R.id.tvTitle);
		tvHeaderTitle.setText("Add an Activity");
		btnHeaderBackBut = (Button) findViewById(R.id.btnHeader);
		btnHeaderBackBut.setText("Back");
		btnHeaderBackBut.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
