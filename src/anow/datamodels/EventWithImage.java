package anow.datamodels;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class EventWithImage extends Event{
	
	public Bitmap eventImage;

	public EventWithImage(int id, String name, String tStart, String dStart,String dEnd, String loc, String desc, String type, Bitmap img) {
		super(id, name, tStart, dStart, dEnd, loc, desc, type);
		// TODO Auto-generated constructor stub
		this.eventImage = img;
	}
	
	public Bitmap getImage(){
		return this.eventImage;
	}

}
