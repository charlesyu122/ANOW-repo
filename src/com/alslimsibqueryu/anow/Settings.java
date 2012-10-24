package com.alslimsibqueryu.anow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {

	Button btnChangeUname, btnChangePword, btnChangeProfPic, btnLogout;
	// Header views
	TextView tvTitle;
	Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		this.setup();
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
			AlertDialog.Builder alertU = new AlertDialog.Builder(Settings.this);
			alertU.setTitle("Change email address:");
			EditText newUsername = new EditText(Settings.this);
			newUsername.setText(btnChangeUname.getText());
			alertU.setView(newUsername);

			alertU.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Toast.makeText(Settings.this,
									"Username successfully changed!",
									Toast.LENGTH_SHORT).show();
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
			LayoutInflater factory = LayoutInflater.from(Settings.this);
			View textEntryView = factory
					.inflate(R.layout.change_password, null);
			AlertDialog.Builder alertP = new AlertDialog.Builder(Settings.this);
			alertP.setTitle("Change password:");
			alertP.setView(textEntryView);

			// EditText newPassword =
			// (EditText)findViewById(R.id.etChangePassword);
			// EditText newConfirmPassword =
			// (EditText)findViewById(R.id.etChangeConfirmPassword);
			alertP.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Toast.makeText(Settings.this,
									"Password successfully changed!",
									Toast.LENGTH_SHORT).show();
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
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
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
}
