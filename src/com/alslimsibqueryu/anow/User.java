package com.alslimsibqueryu.anow;

import java.util.Comparator;

public class User {
	int stat;
	public String status; //friends or strangers
	public String username;
	public String name;
	public String birthday;
	public String hobbies;
	public int eventCount;
	public int profPic;
	
	//constructors
	public User(){}
	public User(int status, String name, int pic){ // for testing
		this.stat = status;
		this.name = name;
		this.profPic = pic;
	}
	public User(String uname, String name, String bday, String hobbies, String eventCount, int profPic, String status){
		this.username = uname;
		this.name = name;
		this.birthday = bday;
		this.hobbies = hobbies;
		this.eventCount = Integer.parseInt(eventCount);
		this.profPic = profPic;
		this.status = status;
	}
	
	public void setStatus(int stat){
		this.stat = stat;
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
