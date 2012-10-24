package com.alslimsibqueryu.anow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InviteAdapter extends ArrayAdapter<Invite>{

	private Context context;
	Invite[] values = null; //value statues should all be 'I'
	
	public InviteAdapter(Context context, Invite[] values) {
		super(context, R.layout.single_invite, values);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = values;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView = convertView;
		if(rowView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.single_invite, parent, false);
		}
		ImageView ivPic = (ImageView)rowView.findViewById(R.id.ivInviteProfPic);
		TextView tvInviteeName = (TextView)rowView.findViewById(R.id.tvInviteeName);
		TextView tvInviteEvent = (TextView)rowView.findViewById(R.id.tvInviteEventName);
		final Button btnAttend = (Button)rowView.findViewById(R.id.btnAttendInvite);
		
		ivPic.setImageResource(values[position].inviteePic);
		tvInviteeName.setText(values[position].inviteeName+ " invited you to");
		tvInviteEvent.setText(values[position].inviteEvent);
		
		btnAttend.setOnClickListener(new View.OnClickListener() {
				
			public void onClick(View v) {
				// TODO Auto-generated method stub
				values[position].setStatus('C');
				btnAttend.setText("Confirmed");
				btnAttend.setEnabled(false);
			}
		});
		return rowView;
	}
}
