package com.example.yamba;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {

	static final String TAG = "RefreshService";
	
	public RefreshService() { //Don't actually want/need to use the constructor 
		super(TAG);
	
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Entered OnHandleIntent");
		
		((YambaApplication)getApplication()).PullAndInsert(); //gets the data and inserting it into the database  //problem...
		
	}
}
