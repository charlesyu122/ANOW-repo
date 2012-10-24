package com.alslimsibqueryu.anow;

public class Invite {
	
	public String inviteeName;
	public String inviteEvent;
	public int inviteePic;
	public char inviteStatus;
	
	//constructor
	public Invite(String iName, String iEvent, int iPic, char iStatus ){
		this.inviteeName = iName;
		this.inviteEvent = iEvent;
		this.inviteePic = iPic;
		this.inviteStatus = iStatus;
	}
	
	public void setStatus(char stat){
		this.inviteStatus = stat;
	}
	
}
