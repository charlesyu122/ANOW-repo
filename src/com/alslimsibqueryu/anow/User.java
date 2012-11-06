package com.alslimsibqueryu.anow;

import java.util.Comparator;

public class User {
	public int status; //friend or not
	public String username;
	public String password;
	public String name;
	public String birthday;
	public int profPic;
	
	//constructors
	public User(){}
	public User(int status, String name, int image){
		this.status = status;
		this.name = name;
		this.profPic = image;
	}
	
	public void setStatus(int stat){
		this.status = stat;
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
