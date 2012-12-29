package com.alslimsibqueryu.anow;

import java.util.Comparator;

public class User {

	public String status; //friends or strangers
	public String username;
	public String name;
	public String birthday;
	public String hobbies;
	public int eventCount;
	public int profPic;
	public boolean invited; // for invite friends 
	
	//constructors
	public User(){}

	public User(String uname, String name, String bday, String hobbies, String eventCount, int profPic, String status){
		this.username = uname;
		this.name = name;
		this.birthday = bday;
		this.hobbies = hobbies;
		this.eventCount = Integer.parseInt(eventCount);
		this.profPic = profPic;
		this.status = status;
	}
	
	public void setInvited(boolean invite){
		this.invited = invite;
	}
	
	public void connectUser(){
		this.status = "friends";
	}
	
	public static Comparator<User> UserNameComparator = new Comparator<User>() {

		public int compare(User lhs, User rhs) {
			// TODO Auto-generated method stub
			String name1 = lhs.name.toUpperCase();
			String name2 = rhs.name.toUpperCase();
			return name1.compareTo(name2);
		}
	};

}
