package com.example.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class RefreshScheduleReceiver extends BroadcastReceiver{

	String TAG = "RefreshScheduleReceiver";
	static PendingIntent lastOp;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Entered onReceive");
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); //read shared prefs
		 
		long interval = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("delay", "900000")); //get the frequency 
		
		PendingIntent operation = PendingIntent.getService(context, -1, new Intent(YambaApplication.ACTION_REFRESH), PendingIntent.FLAG_UPDATE_CURRENT); //wrap the "start refresh" intent around another intent. 
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE); //get the alarm manager 
		alarmManager.cancel(lastOp); //cancel pre-existing operation
		
		if(interval>0) {
			
			
			alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation); //set future interval
			
			//context.startService(new Intent (context, UpdaterService.class));
		}
		
		lastOp = operation; 
		Log.d(TAG,"OnReceive delay: "+interval);
		
		
	}

}
