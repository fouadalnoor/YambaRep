package com.example.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements LocationListener {

	static final String PROVIDER = LocationManager.GPS_PROVIDER;
	EditText editStatus;
    LocationManager locationManager;
    Location location;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status); //
		editStatus = (EditText) findViewById(R.id.edit_status);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		location = locationManager.getLastKnownLocation(PROVIDER);

	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		locationManager.requestLocationUpdates(PROVIDER, 30000, 1000, this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		locationManager.removeUpdates(this);
	}


	public void onClick(View v) {
		final String statusText = editStatus.getText().toString();
		new PostToTwitter().execute(statusText);

	}

	class PostToTwitter extends AsyncTask<String, Void, String> {

		// new Thread//
		@Override
		protected String doInBackground(String... params) {

			try {
				((YambaApplication)getApplication()).getTwitter().setStatus(params[0]); //how does setStatus work?
				Log.d("StatusActivity", params[0]);
				return "Status posted Sucessfully!";

			} catch (TwitterException e) {
				Log.d("StatusActivity", "Failed to post status");
				return "Status failed to update";

			}

		}

		// End of New thread//

		//UI Thread//
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT)
					.show();
			super.onPostExecute(result);
		}
		
	}
	
	//Menu Stuff

	//called only once. When the Menu button is clicked. 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true; 
				
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //item value is passed depending on which menu button is pressed
		Intent intentUpdater = new Intent(this, UpdaterService.class); //activity is a subclass of "context" hence the 'this' keyword. //its an explicit intent. 
		Intent intentRefresh = new Intent(this, RefreshService.class); //activity is a subclass of "context" hence the 'this' keyword. //its an explicit intent.
		Intent intentPrefs = new Intent(this, PrefsActivity.class); //activity is a subclass of "context" hence the 'this' keyword. //its an explicit intent.
		Intent intentTimeLine = new Intent(this, TimeLineActivity.class); //activity is a subclass of "context" hence the 'this' keyword. //its an explicit intent.
		
		switch(item.getItemId())
		{
	case R.id.item_start_service: 
		Log.d("StatusActivity", "Entered case: item_start_service");
		startService(intentUpdater);
		
		return true;
	case R.id.item_stop_service:
		Log.d("StatusActivity", "Entered case: item_stop_service");
		stopService(intentUpdater);
		return true;
		
	case R.id.item_prefs:
		Log.d("StatusActivity", "Entered case: item_prefs");
		startActivity(new Intent (this, PrefsActivity.class)); //could do it the normal way, but this shows another way to use the intents 
		//startActivity(intentPrefs);
		return true;
		
	case R.id.item_refresh_service:
		Log.d("StatusActivity", "Entered case: item_refresh_service");
		startService(intentRefresh);
		return true; 
		
	case R.id.item_status_update:
		Log.d("StatusActivity","Enetered case: item_timeline_activity");
		startActivity(intentTimeLine);
	return true;
	default: 
		return false;
		}
		
	}


	//Locationlistener callbacks
	@Override
	public void onLocationChanged(Location newlocation) {
		// TODO Auto-generated method stub
		this.location = newlocation;
		Log.d("StatusActivity","onlocationChanged: "+ location.toString());
	}


	@Override
	public void onProviderDisabled(String arg0) { //GPS disabled 
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String arg0) { //GPS enabled 
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //CGPS status changed. 
		// TODO Auto-generated method stub
		
	}
}
	
	

