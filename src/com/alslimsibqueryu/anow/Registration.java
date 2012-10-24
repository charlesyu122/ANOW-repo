package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends Activity{

	TextView headerTitle;
	Button headerButton;
	Button regButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		this.setup();
	}
	
	private void setup(){
		headerTitle = (TextView)findViewById(R.id.tvTitle);
		headerTitle.setText("Registration");
		
		headerButton = (Button)findViewById(R.id.btnHeader);
		headerButton.setVisibility(View.INVISIBLE);
		
		regButton = (Button)findViewById(R.id.btnRegSubmit);
		regButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Registration.this, "You have been registered successfully!", Toast.LENGTH_SHORT).show();
				finish();
				startActivity(new Intent(Registration.this, MainActivity.class));
			}
		});
	}
}
