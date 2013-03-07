package anow.datamodels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import anow.adapters.UserAdapter;
import anow.views.FriendsInvite.UserInviteAdapter;

public class User {

	// Attributes
	public String userId;
	public String status; // friends or strangers or newly-connected
	public String username;
	public String name;
	public String birthday;
	public String hobbies;
	public int eventCount;
	public boolean invited; // for invite friends
	// For lazy loading
	private String imgUrl;
	private UserAdapter uAdp;
	private UserInviteAdapter uiAdp;
	public Bitmap profPic;
	private boolean checkForInvite = false;

	// constructors
	public User() {
	}

	public User(String userId, String uname, String name, String bday,
			String hobbies, String eventCount, String imgUrl,
			String status) {
		this.userId = userId;
		this.username = uname;
		this.name = name;
		this.birthday = bday;
		this.hobbies = hobbies;
		this.eventCount = Integer.parseInt(eventCount);
		this.imgUrl = imgUrl;
		this.status = status;
	}

	public void setInvited(boolean invite) {
		this.invited = invite;
	}

	public void connectUser() {
		this.status = "newly-connected";
	}

	public static Comparator<User> UserNameComparator = new Comparator<User>() {

		public int compare(User lhs, User rhs) {
			// TODO Auto-generated method stub
			String name1 = lhs.name.toUpperCase();
			String name2 = rhs.name.toUpperCase();
			return name1.compareTo(name2);
		}
	};

	public void loadImage(UserAdapter adp) {
		this.uAdp = adp;
		if (imgUrl != null && !imgUrl.equals("")) {
			new ImageLoadTask().execute(imgUrl);
		}
	}
	
	public void loadImageForInvite(UserInviteAdapter adp) {
		this.uiAdp = adp;
		this.checkForInvite = true;
		if (imgUrl != null && !imgUrl.equals("")) {
			new ImageLoadTask().execute(imgUrl);
		}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Async task to load lazy load images
	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Bitmap b = getBitmapFromURL(params[0]);
				return b;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result != null) {
				profPic = result;
				if(checkForInvite == true){
					if (uiAdp != null) {
						// When the image is loaded notify the adapter
						uiAdp.notifyDataSetChanged();
					}
				} else{
					if (uAdp != null) {
						// When the image is loaded notify the adapter
						uAdp.notifyDataSetChanged();
					}
				}
			} else {
				Log.d("ImageLoadTask", "Failed to load " + name + " image");
			}
		}

	}
}
