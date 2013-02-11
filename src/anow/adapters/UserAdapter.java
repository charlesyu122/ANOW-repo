package anow.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alslimsibqueryu.anow.JSONParser;
import com.alslimsibqueryu.anow.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import anow.datamodels.User;

public class UserAdapter extends ArrayAdapter<User> {

	private Context context;
	private String loggedInUserId;
	User[] values = null;
	char type; // F for friends list P for participants list
	
	// Attributes for Database Interaction
	private ProgressDialog pDialog;
	private JSONParser jParser = new JSONParser();
	private static String url_connect_users = "http://atnow.net84.net/ANowPhp/connect_users.php";

	public UserAdapter(Context context, ArrayList<User> objects, char type, String loggedIn) {
		super(context, R.layout.single_user, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = objects.toArray(new User[objects.size()]);
		this.type = type;
		this.loggedInUserId = loggedIn;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.single_user, parent, false);
		
		ImageView userProfPic = (ImageView) rowView.findViewById(R.id.ivListProfPic);
		TextView userName = (TextView) rowView.findViewById(R.id.tvUserName);
		final Button connect = (Button) rowView.findViewById(R.id.btnConnect);
		ImageView ivInfo = (ImageView) rowView.findViewById(R.id.ivUInfo);
		userProfPic.setImageBitmap(values[position].profPic);
		userName.setText(values[position].name);
		
		if(values[position].status.equals("strangers")){
			connect.setText("Connect");
			connect.setVisibility(View.VISIBLE);
			connect.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new ConnectUser(values[position].userId).execute();
					values[position].connectUser();
					connect.setText("Connected");
					connect.setEnabled(false);
				}
			});
		} else if(values[position].status.equals("newly-connected")){ // Newly-connected user
			connect.setText("Connected");
			connect.setVisibility(View.VISIBLE);
			connect.setEnabled(false);
		}
		
		if(type == 'L' || type == 'P')
			ivInfo.setVisibility(View.INVISIBLE);
	
		rowView.setTag(values[position]);
		return rowView;
	}
	
	public class ConnectUser extends AsyncTask<String, String, String>{

		private String connectToUserId;
		
		public ConnectUser(String userId){
			this.connectToUserId = userId;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Connecting to user...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("logged_in", loggedInUserId));
			params.add(new BasicNameValuePair("user_id", connectToUserId));
			
			// Get JSON object
			JSONObject json = jParser.makeHttpRequest(url_connect_users, params);
			
			// Check for success tag
			try{
				int success = json.getInt("success");
				if(success == 1){
					// successfully connected user
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}
		
	}

}
