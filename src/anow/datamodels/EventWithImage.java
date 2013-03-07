package anow.datamodels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import anow.adapters.EventAdapter;

@SuppressWarnings("serial")
public class EventWithImage extends Event {

	// Attributes
	public Bitmap eventImage;
	private String imgUrl;
	private EventAdapter eventAdp;

	// Constructor
	public EventWithImage(int id, String name, String tStart, String dStart,
			String dEnd, String loc, String desc, String type, Bitmap eventImg) {
		super(id, name, tStart, dStart, dEnd, loc, desc, type);
		// TODO Auto-generated constructor stub
		this.eventImage = eventImg;
	}

	public EventWithImage(int id, String name, String tStart, String dStart,
			String dEnd, String loc, String desc, String type, String imgUrl) {
		super(id, name, tStart, dStart, dEnd, loc, desc, type);
		// TODO Auto-generated constructor stub
		this.imgUrl = imgUrl;
	}

	public Bitmap getImage() {
		return this.eventImage;
	}

	public void loadImage(EventAdapter adp) {
		this.eventAdp = adp;
		if (imgUrl != null && !imgUrl.equals("")) {
			new ImageLoadTask().execute(imgUrl);
		}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
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
				eventImage = result;
				if (eventAdp != null) {
					// When the image is loaded notify the adapter
					eventAdp.notifyDataSetChanged();
				}
			} else {
				Log.d("ImageLoadTask", "Failed to load image");
			}
		}
	}
}
