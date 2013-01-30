package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InviteAdapter extends ArrayAdapter<Invite>{

	private Context context;
	Invite[] values = null; 
	
	// Attributes for Database Interaction
	private ProgressDialog pDialog;
	private JSONParser jParser = new JSONParser();
	private static String url_accept_invite = "http://10.0.2.2/ANowPhp/accept_invite.php";
	
	public InviteAdapter(Context context, Invite[] values) {
		super(context, R.layout.single_invite, values);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = values;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.single_invite, parent, false);
		
		ImageView ivPic = (ImageView)rowView.findViewById(R.id.ivInviteProfPic);
		TextView tvInviteeName = (TextView)rowView.findViewById(R.id.tvInviteeName);
		TextView tvInviteEvent = (TextView)rowView.findViewById(R.id.tvInviteEventName);
		final Button btnAttend = (Button)rowView.findViewById(R.id.btnAttendInvite);
		
		ivPic.setImageBitmap(values[position].invitedEventPic);
		tvInviteeName.setText(values[position].inviteeName+ " invited you to");
		tvInviteEvent.setText(values[position].invitedEvent);
		if(values[position].status.equals("invite"))
			btnAttend.setText("Attend");
		else{
			btnAttend.setText("Confirmed");
			btnAttend.setEnabled(false);
		}
		
		btnAttend.setOnClickListener(new View.OnClickListener() {
				
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AcceptInvite(values[position].attendId).execute();
				values[position].confirmInvite();
				btnAttend.setText("Confirmed");
				btnAttend.setEnabled(false);
			}
		});
		
		return rowView;
	}

	
	public class AcceptInvite extends AsyncTask<String, String, String>{
		
		String attendToConfirm;
		
		public AcceptInvite(String id){
			this.attendToConfirm = id;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Processing...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("attend_id", attendToConfirm));
			
			// Get JSON object
			JSONObject json = jParser.makeHttpRequest(url_accept_invite, params);
			
			// Check for success tag
			try{
				int success = json.getInt("success");
				if(success == 1){
					// successfully connected user
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			Toast.makeText(context, "Invitation accepted!", Toast.LENGTH_SHORT).show();
		}
		
	}

}
