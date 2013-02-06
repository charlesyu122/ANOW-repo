package anow.datamodels;

import java.util.Comparator;

import android.graphics.Bitmap;

public class User {

	public String userId;
	public String status; //friends or strangers or newly-connected
	public String username;
	public String name;
	public String birthday;
	public String hobbies;
	public int eventCount;
	public Bitmap profPic;
	public boolean invited; // for invite friends 
	
	//constructors
	public User(){}

	public User(String userId, String uname, String name, String bday, String hobbies, String eventCount, Bitmap profPic, String status){
		this.userId = userId;
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
		this.status = "newly-connected";
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
