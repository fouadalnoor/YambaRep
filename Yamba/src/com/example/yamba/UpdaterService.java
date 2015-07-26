package com.example.yamba;

import java.util.List;


import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {

	static final String TAG = "UpdaterService"; // used for log.d
	static final int DELAY = 10; //default delay
	boolean running = false;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		running = true; 
      new Thread() {
      public void run() {
    	 // int delay = ((YambaApplication)getApplication()).prefs.getInt("delay", DELAY);
		try {
			
			while(running) {
			((YambaApplication)getApplication()).PullAndInsert(); //gets the data and inserting it into the database 
			
			int newdelay = Integer.parseInt( ((YambaApplication)getApplication()).prefs.getString("delay", "30")); 
			
			Thread.sleep(newdelay*1000);
		       }	
			
		} catch (TwitterException e) {
			e.printStackTrace();
			Log.d(TAG,"Failed due to network error");
		} catch (InterruptedException e) {
			Log.d(TAG,"Failed due to delay");
		}
      }
    }.start();
    
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false; 
		Log.d(TAG, "onDestroy");
	}

	// ignore onBind for now...
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
