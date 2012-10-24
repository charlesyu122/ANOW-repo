package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserProfile extends Activity {

	TextView tvProfName, tvProfBday, tvProfInterest;
	EditText etProfName, etProfBday, etProfInterest;
	Button btnCalendar, btnFriends, btnInvites;
	// Header
	Button btnSettings, btnSave;
	TextView headerTitle;
	int btnHeaderStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		this.setup();
	}

	private void setup() {
		// Set-up header
		headerTitle = (TextView) findViewById(R.id.tvTitle);
		btnSettings = (Button) findViewById(R.id.btnHeader);
		btnSave = (Button) findViewById(R.id.btnHeader2);
		headerTitle.setText("Profile");
		btnSettings.setText("Settings");
		btnSave.setText("Save");
		btnSettings.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(UserProfile.this, Settings.class));
			}
		});

		// Set-up profile views
		tvProfName = (TextView) findViewById(R.id.tvProfName);
		etProfName = (EditText) findViewById(R.id.etProfName);
		tvProfBday = (TextView) findViewById(R.id.tvProfBday);
		etProfBday = (EditText) findViewById(R.id.etProfBday);
		tvProfInterest = (TextView) findViewById(R.id.tvProfInterest);
		etProfInterest = (EditText) findViewById(R.id.etProfInterest);
		btnCalendar = (Button) findViewById(R.id.btnCalendar);
		btnFriends = (Button) findViewById(R.id.btnFriends);
		btnInvites = (Button) findViewById(R.id.btnInvites);

		btnCalendar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnFriends.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(UserProfile.this, Friends.class));
			}
		});
		btnInvites.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(UserProfile.this, Invites.class));
			}
		});

		View.OnLongClickListener listen = new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (((TextView) v).getId() == tvProfName.getId()) {
					tvProfName.setVisibility(View.GONE);
					etProfName.setText(tvProfName.getText());
					etProfName.setVisibility(View.VISIBLE);
					
				} else if (((TextView) v).getId() == tvProfBday.getId()) {
					tvProfBday.setVisibility(View.GONE);
					etProfBday.setText(tvProfBday.getText());
					etProfBday.setVisibility(View.VISIBLE);
					
				} else if (((TextView) v).getId() == tvProfInterest.getId()) {
					tvProfInterest.setVisibility(View.GONE);
					etProfInterest.setText(tvProfInterest.getText());
					etProfInterest.setVisibility(View.VISIBLE);
				}
				if(btnSave.getVisibility() == View.GONE){
					btnSave.setVisibility(View.VISIBLE);
					btnSettings.setVisibility(View.GONE);
				}
				return false;
			}
		};
		
		View.OnClickListener savelisten = new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etProfName.getVisibility() == View.VISIBLE) {
					tvProfName.setText(etProfName.getText());
					etProfName.setVisibility(View.GONE);
					tvProfName.setVisibility(View.VISIBLE);
				}
				if (etProfBday.getVisibility() == View.VISIBLE) {
					tvProfBday.setText(etProfBday.getText());
					etProfBday.setVisibility(View.GONE);
					tvProfBday.setVisibility(View.VISIBLE);
				}
				if (etProfInterest.getVisibility() == View.VISIBLE) {
					tvProfInterest.setText(etProfInterest.getText());
					etProfInterest.setVisibility(View.GONE);
					tvProfInterest.setVisibility(View.VISIBLE);
				}
				btnSave.setVisibility(View.GONE);
				btnSettings.setVisibility(View.VISIBLE);
			}
		};

		tvProfName.setOnLongClickListener(listen);
		tvProfBday.setOnLongClickListener(listen);
		tvProfInterest.setOnLongClickListener(listen);
		btnSave.setOnClickListener(savelisten);
	}

}
