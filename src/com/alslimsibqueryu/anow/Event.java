package com.alslimsibqueryu.anow;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Event implements Serializable{
	public int eventId;
	public String eventName;
	public String eventTimeStart;
	public String eventDateStart;
	public String eventLocation;
	public String eventDescription;
	public Integer eventImage;
	public String type; //E or A --> Commercial or self-activity 
	public Boolean eventPrivacy;
	
	// Constructors
	
	// Constructor for advertised events 
	public Event(int id, String name, String tStart, String dStart, String loc, String desc, String type, int image){
		this.eventId = id;
		this.eventName = name;
		this.eventTimeStart = tStart;
		this.eventDateStart = dStart;
		this.eventLocation = loc;
		this.eventDescription = desc;
		this.type = type;
		this.eventImage = image;
	}
	
	public Event(int id, String name, String tStart, String dStart, String loc){
		this.eventId = id;
		this.eventName = name;
		this.eventTimeStart = tStart;
		this.eventDateStart = dStart;
		this.eventLocation = loc;
		this.eventDescription = "	Attend this!Attend this!Attend this!Attend this!Attend this!Attend this!Attend this!Attend this!Attend this!Attend this!This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun. This is very fun.";
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
	
	public Integer getEventImage(){
		return this.eventImage;
	}

}
