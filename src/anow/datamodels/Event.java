package anow.datamodels;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Event implements Serializable{
	
	public int eventId;
	public String eventName;
	public String eventTimeStart;
	public String eventDateStart;
	public String eventDateEnd;
	public String eventLocation;
	public String eventDescription;
	public String type; //E or A --> Commercial or self-activity 
	public Boolean eventPrivacy;
	
	// Constructors
	
	// Constructor for advertised events 
	public Event(int id, String name, String tStart, String dStart, String dEnd, String loc, String desc, String type){
		this.eventId = id;
		this.eventName = name;
		this.eventTimeStart = tStart;
		this.eventDateStart = dStart;
		this.eventDateEnd = dEnd;
		this.eventLocation = loc;
		this.eventDescription = desc;
		this.type = type;
	}
	
	
	public String getEventName(){
		return this.eventName;
	}
	
	public String getEventTimeStart(){
		return this.eventTimeStart;
	}
	
	public String getEventLocation(){
		return this.eventLocation;
	}

}
