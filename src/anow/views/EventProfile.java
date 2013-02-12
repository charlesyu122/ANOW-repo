package anow.views;

import com.alslimsibqueryu.anow.ApplicationController;
import com.alslimsibqueryu.anow.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import anow.datamodels.Event;

public class EventProfile extends Activity{

	Event receivedEvent; String eventId;
	String type; // advertised or attended
	Bitmap eventImage;
	ImageView ivEpic;
	TextView tvEname, tvEdate, tvEloc, tvEtime, tvEdesc;
	Button btnParticipants, btnInvite;
	ApplicationController AC;
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	// Tags
	private static final String TYPE_ADVERTISED = "advertised";
	private static final String TYPE_ATTENDED = "attended";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_profile);
		AC = (ApplicationController)getApplicationContext();
		this.receiveData();
		this.setup();
	}
	
	private void receiveData(){
		Intent i = getIntent();
		this.receivedEvent = (Event)i.getSerializableExtra("eventObject");
		this.eventId = Integer.toString(receivedEvent.eventId);
		this.type = i.getStringExtra("type");
		this.eventImage = i.getParcelableExtra("bmp");
	}
	
	private void setup(){
		//Set-up views
		ivEpic = (ImageView)findViewById(R.id.ivEventPic);
		tvEname = (TextView)findViewById(R.id.tvEventName);
		tvEdate = (TextView)findViewById(R.id.tvEventDate);
		tvEloc = (TextView)findViewById(R.id.tvEventLocation);
		tvEtime = (TextView)findViewById(R.id.tvEventTime);
		tvEdesc = (TextView)findViewById(R.id.tvEventDesc);
		
		ivEpic.setImageBitmap(eventImage);
		tvEname.setText(this.receivedEvent.eventName);
		tvEdate.setText(this.receivedEvent.eventDateStart);
		tvEloc.setText(this.receivedEvent.eventLocation);
		tvEtime.setText(this.receivedEvent.eventTimeStart);
		tvEdesc.setText(this.receivedEvent.eventDescription);
		
		btnParticipants = (Button)findViewById(R.id.btnParticipants);
		btnInvite = (Button)findViewById(R.id.btnEInvite);
		if(type.equals(TYPE_ADVERTISED))
			btnInvite.setText("My Calendar");
		btnParticipants.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AC.isOnline(EventProfile.this)){
					Intent i = new Intent(EventProfile.this, Participants.class);
					i.putExtra("event_id", eventId);
					startActivity(i);
				} else
					Toast.makeText(EventProfile.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
			}
		});
		btnInvite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AC.isOnline(EventProfile.this)){
					if(type.equals(TYPE_ATTENDED)){
						Intent i = new Intent(EventProfile.this, FriendsInvite.class);
						i.putExtra("event_id", eventId);
						startActivity(i);
					}else 
						finish();
				} else
					Toast.makeText(EventProfile.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
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
