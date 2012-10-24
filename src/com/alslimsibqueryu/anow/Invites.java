package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Invites extends Activity{

	ListView lvInvites;
	Invite[] dummyInvites;
	//Header views
	TextView tvTitle;
	Button btnBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invites);
		this.populateDummies();
		this.setup();
	}
	
	private void populateDummies() {
		dummyInvites = new Invite[] {
				new Invite("Fealrone Alajas", "Inom" ,R.drawable.ic_launcher, 'I'),
				new Invite("Juan de la Cruz", "Meeting" ,R.drawable.ic_launcher, 'I'),
				new Invite("Erwin Lim", "Ayala Sale" ,R.drawable.ic_launcher, 'I'),
				new Invite("Henry Sabarre", "Starbucks promo" ,R.drawable.ic_launcher, 'I')
		};
	}
	
	private void setup(){
		//Set-up header views
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		btnBack = (Button)findViewById(R.id.btnHeader);
		tvTitle.setText("Invites");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//Set-up views
		lvInvites = (ListView)findViewById(R.id.lvInvites);
		lvInvites.setAdapter(new InviteAdapter(this, dummyInvites));
	}
}
