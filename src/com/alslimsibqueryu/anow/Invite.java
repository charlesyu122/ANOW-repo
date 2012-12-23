package com.alslimsibqueryu.anow;

public class Invite {
	
	public String attendId;
	public String inviteeName;
	public String invitedEvent;
	public int invitedEventPic;
	
	//constructor
	public Invite(String attendId, String name, String event, int pic){
		this.attendId = attendId;
		this.inviteeName = name;
		this.invitedEvent = event;
		this.invitedEventPic = pic;
	}
	
}
