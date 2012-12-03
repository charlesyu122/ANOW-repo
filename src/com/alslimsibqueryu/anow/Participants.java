package com.alslimsibqueryu.anow;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Participants extends Activity {

	ListView lvParticipants;
	User[] participantDummies;
	String[] namesOfParticipants;
	// Header views
	TextView tvTitle;
	Button btnBack;
	// Side index attributes
	private GestureDetector mGestureDetector;
	private static float sideIndexX;
	private static float sideIndexY;
	private int sideIndexHeight;
	private int indexListSize;
	private ArrayList<Object[]> indexList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participants);
		this.populateDummies();
		this.transferNames();
		Arrays.sort(participantDummies, User.UserNameComparator);
		Arrays.sort(namesOfParticipants);
		this.setup();
		// Side Index
		mGestureDetector = new GestureDetector(this,new SideIndexGestureListener());
	}

	private void populateDummies() {
		participantDummies = new User[] {
				new User(1, "Fealrone Alajas", R.drawable.ic_launcher),
				new User(1, "Erwin Lim", R.drawable.ic_launcher),
				new User(1, "Jullian Sibi", R.drawable.ic_launcher),
				new User(1, "Josha Querubin", R.drawable.ic_launcher),
				new User(1, "Charles Yu", R.drawable.ic_launcher),
				new User(1, "Paul Parreno", R.drawable.ic_launcher),
				new User(0, "Juan Cruz", R.drawable.ic_launcher),
				new User(0, "Kobe Bryant", R.drawable.ic_launcher),
				new User(0, "Totin Monkey", R.drawable.ic_launcher),
				new User(0, "Aferer Lim", R.drawable.ic_launcher),
				new User(0, "Bouyant Salajas", R.drawable.ic_launcher),
				new User(0, "Shang Lumpia", R.drawable.ic_launcher),
				new User(0, "Apple iSibi", R.drawable.ic_launcher), 
		};
	}
	
	private void transferNames(){
		this.namesOfParticipants = new String[participantDummies.length];
		for(int i=0; i<this.participantDummies.length; i++)
			this.namesOfParticipants[i] = this.participantDummies[i].name;
	}

	private void setup() {
		// Set-up header views
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnBack = (Button) findViewById(R.id.btnHeader);
		tvTitle.setText("Participants");
		btnBack.setText("Back");
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// Set-up views
		lvParticipants = (ListView) findViewById(R.id.lvParticipants);
		lvParticipants.setAdapter(new UserAdapter(Participants.this,participantDummies, 'P'));
		lvParticipants.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v,int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(Participants.this, "Go to profile",Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return false;
		}
	}

	private ArrayList<Object[]> createIndex(String[] strArr) {
		ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
		Object[] tmpIndexItem = null;

		int tmpPos = 0;
		String tmpLetter = "";
		String currentLetter = null;
		String strItem = null;

		for (int j = 0; j < strArr.length; j++) {
			strItem = strArr[j];
			currentLetter = strItem.substring(0, 1);

			// every time new letters comes
			// save it to index list
			if (!currentLetter.equals(tmpLetter)) {
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = tmpLetter;
				tmpIndexItem[1] = tmpPos - 1;
				tmpIndexItem[2] = j - 1;

				tmpLetter = currentLetter;
				tmpPos = j + 1;

				tmpIndexList.add(tmpIndexItem);
			}
		}

		// save also last letter
		tmpIndexItem = new Object[3];
		tmpIndexItem[0] = tmpLetter;
		tmpIndexItem[1] = tmpPos - 1;
		tmpIndexItem[2] = strArr.length - 1;
		tmpIndexList.add(tmpIndexItem);

		// and remove first temporary empty entry
		if (tmpIndexList != null && tmpIndexList.size() > 0) {
			tmpIndexList.remove(0);
		}

		return tmpIndexList;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		LinearLayout sideIndex = (LinearLayout) findViewById(R.id.participantsSideIndex);
		sideIndexHeight = sideIndex.getHeight();
		sideIndex.removeAllViews();

		// TextView for every visible item
		TextView tmpTV = null;

		// we'll create the index list
		indexList = createIndex(namesOfParticipants);

		// number of items in the index List
		indexListSize = indexList.size();

		// maximal number of item, which could be displayed
		int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);

		int tmpIndexListSize = indexListSize;

		// handling that case when indexListSize > indexMaxSize
		while (tmpIndexListSize > indexMaxSize) {
			tmpIndexListSize = tmpIndexListSize / 2;
		}

		// computing delta (only a part of items will be displayed to save a
		// place)
		double delta = indexListSize / tmpIndexListSize;

		String tmpLetter = null;
		Object[] tmpIndexItem = null;

		// show every m-th letter
		for (double i = 1; i <= indexListSize; i = i + delta) {
			tmpIndexItem = indexList.get((int) i - 1);
			tmpLetter = tmpIndexItem[0].toString();
			tmpTV = new TextView(this);
			tmpTV.setText(tmpLetter);
			tmpTV.setGravity(Gravity.CENTER);
			tmpTV.setTextSize(20);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 1);
			tmpTV.setLayoutParams(params);
			sideIndex.addView(tmpTV);
		}

		// and set a touch listener for it
		sideIndex.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();
				return false;
			}
		});
	}

	class SideIndexGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// we know already coordinates of first touch
			// we know as well a scroll distance
			sideIndexX = sideIndexX - distanceX;
			sideIndexY = sideIndexY - distanceY;

			// when the user scrolls within our side index
			// we can show for every position in it a proper
			// item in the country list
			if (sideIndexX >= 0 && sideIndexY >= 0) {
				displayListItem();
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	public void displayListItem() {
		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		// compute minimal position for the item in the list
		int minPosition = (int) (itemPosition * pixelPerIndexItem);

		// get the item (we can do it since we know item index)
		Object[] indexItem = indexList.get(itemPosition);

		// and compute the proper item in the country list
		int indexMin = Integer.parseInt(indexItem[1].toString());
		int indexMax = Integer.parseInt(indexItem[2].toString());
		int indexDelta = Math.max(1, indexMax - indexMin);

		double pixelPerSubitem = pixelPerIndexItem / indexDelta;
		int subitemPosition = (int) (indexMin + (sideIndexY - minPosition)
				/ pixelPerSubitem);

		ListView listView = (ListView) findViewById(R.id.lvParticipants);
		listView.setSelection(subitemPosition);
	}

}
