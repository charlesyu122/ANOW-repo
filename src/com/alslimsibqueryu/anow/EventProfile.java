package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventProfile extends Activity{

	Event receivedEvent;
	ImageView ivEpic;
	TextView tvEname, tvEdate, tvEloc, tvEtime, tvEdesc;
	Button btnParticipants, btnInvite;
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_profile);
		this.receiveData();
		this.setup();
	}
	
	private void receiveData(){
		Intent i = getIntent();
		receivedEvent = (Event)i.getSerializableExtra("eventObject");
	}
	
	private void setup(){
		//Set-up views
		ivEpic = (ImageView)findViewById(R.id.ivEventPic);
		tvEname = (TextView)findViewById(R.id.tvEventName);
		tvEdate = (TextView)findViewById(R.id.tvEventDate);
		tvEloc = (TextView)findViewById(R.id.tvEventLocation);
		tvEtime = (TextView)findViewById(R.id.tvEventTime);
		tvEdesc = (TextView)findViewById(R.id.tvEventDesc);
		ivEpic.setImageResource(this.receivedEvent.eventImage);
		tvEname.setText(this.receivedEvent.eventName);
		tvEdate.setText(this.receivedEvent.eventDateStart);
		tvEloc.setText(this.receivedEvent.eventLocation);
		tvEtime.setText(this.receivedEvent.eventTimeStart);
		tvEdesc.setText(this.receivedEvent.eventDescription);
		
		btnParticipants = (Button)findViewById(R.id.btnParticipants);
		btnInvite = (Button)findViewById(R.id.btnEInvite);
		btnParticipants.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(EventProfile.this, Participants.class));
			}
		});
		btnInvite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(EventProfile.this, Friends.class));
			}
		});
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		tvTitle.setText("Event Profile");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
