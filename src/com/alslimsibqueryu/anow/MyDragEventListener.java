package com.alslimsibqueryu.anow;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyDragEventListener implements OnDragListener {
	
	Context context;
	String selectedMonth;
	
	public MyDragEventListener(Context c, String selectedMonth){
		this.context = c;
		this.selectedMonth = selectedMonth;
	}

	public boolean onDrag(final View v, DragEvent event) {
		// TODO Auto-generated method stub
		
		final int action = event.getAction();
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				return true;
			} else
				return false;

		case DragEvent.ACTION_DRAG_ENTERED:
			((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.GRAY);
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			return true;

		case DragEvent.ACTION_DRAG_EXITED:
			((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
			return true;

		case DragEvent.ACTION_DROP:
			// Gets the item containing the dragged data
			ClipData.Item item = event.getClipData().getItemAt(0);
			String res = item.getText().toString();
			String date = ((TextView)v.findViewById(R.id.tvDateCell)).getText().toString();
			
			
			//Toast.makeText(context, res + " "+ date , Toast.LENGTH_SHORT).show();
			//Confirm attendance
			LayoutInflater factory = LayoutInflater.from(this.context);
			View confirmView = factory.inflate(R.layout.alert_attend, null);
			AlertDialog.Builder alertEventConfirm = new AlertDialog.Builder(this.context);
			alertEventConfirm.setTitle("Join Event");
			alertEventConfirm.setView(confirmView);

			//Set-up view for alert view
			TextView tvAlertEventName = (TextView)confirmView.findViewById(R.id.tvAlertEventName);
			TextView tvAlertEventDate = (TextView)confirmView.findViewById(R.id.tvAlertEventDate);
			tvAlertEventName.setText("Attend " + res );
			tvAlertEventDate.setText("on "+ date + " of "+ this.selectedMonth);
			
			alertEventConfirm.setPositiveButton("Confirm",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//Toast confirm
							Toast.makeText(context, "Event successfully added to your calendar", Toast.LENGTH_SHORT).show();
							//Set item bg with event
							Drawable withEventBg = context.getResources().getDrawable(R.drawable.witheventcell);
							((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundDrawable(withEventBg);
						}
					});
			alertEventConfirm.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							((TextView)v.findViewById(R.id.tvDateCell)).setBackgroundColor(Color.BLACK);
						}
					});
			alertEventConfirm.show();
			
			return true;

		case DragEvent.ACTION_DRAG_ENDED:
			return true;
		default:
			return false;

		}
	}
}
