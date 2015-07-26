package com.example.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {
	
	static final String TAG = "YambaApplication";
	public static final String ACTION_NEW_STATUS = "com.example.yamba.NEW_STATUS";
	public static final String ACTION_REFRESH = "com.example.yamba.RefreshService";
	public static final String ACTION_REFRESH_ALARM = "com.example.yamba.RefreshAlarm";
	
	Twitter twitter; 
	SharedPreferences prefs; //shared within the context of the application (not with other people!) 
	@Override
	public void onCreate() {
		super.onCreate();
	
		
		//prefs stuff
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
	

		
		Log.d(TAG, "onCreate Complete");
		
		
		
	}
	
	public Twitter getTwitter()
	{
		Log.d(TAG,"Entered getTwitter"); //never enters getTwitter?
		
		if(twitter == null)
		{
			//twitter stuff
			String user = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("server", "");
			
			twitter = new Twitter(user, password); // create a new twitter object
			twitter.setAPIRootUrl(server); // redirecting
		}
		return twitter;
	}

	static final Intent refreshAlarm = new Intent (ACTION_REFRESH_ALARM);
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		twitter = null;
		sendBroadcast(refreshAlarm);
		Log.d(TAG, "onSharedPreferenceChanged for key: " + key);
		
	}
	
	long oldTime = 0; 
    long NewTime = 0;  //problems with count...


	public int PullAndInsert ()
	{
		 Log.d(TAG, "PullAndInsert entered");
    int count = 0;
	try {
		List<Status> timeline = twitter.getHomeTimeline();// pull //problem...
		
		for (Status status : timeline) {
			Log.d(TAG,String.format("%s: %s", status.user.name, status.text));  //%s: %s = who said what?
			getContentResolver().insert(StatusProvider.CONTENT_URI, StatusProvider.statusToValues(status)); //choosing a content provider and passing it values.  
			NewTime = status.createdAt.getTime();
	
			if(oldTime<NewTime)
			{
				count++;
				oldTime = status.createdAt.getTime();
			}
			
			
			
			}
	} catch (TwitterException e) {
		Log.e(TAG,"PullAndInsert Failed",e);
	}
	
	if(count>0)
	{
	    Log.d(TAG, "PullAndInsert entered for new status");
		sendBroadcast(new Intent(ACTION_NEW_STATUS).putExtra("count", count) );
	}

	return count; 
}

}
