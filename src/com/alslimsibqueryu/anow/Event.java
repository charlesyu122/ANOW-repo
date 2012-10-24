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
	public char type; //E or A --> Commercial or self-activity 
	public Boolean eventPrivacy;
	
	//constructors
	public Event(int id, String name, String tStart, String dStart, String loc, Integer image){
		this.eventId = id;
		this.eventName = name;
		this.eventTimeStart = tStart;
		this.eventDateStart = dStart;
		this.eventLocation = loc;
		this.eventDescription = "	This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. This is a very cool event. ";
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
