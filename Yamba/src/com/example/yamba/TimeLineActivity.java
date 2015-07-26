package com.example.yamba;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimeLineActivity extends ListActivity implements LoaderCallbacks<Cursor> {
	static final String[] FROM = {StatusProvider.C_USER, StatusProvider.C_TEXT, StatusProvider.C_CREATED_AT};
	static final int[] TO = {R.id.text_user,R.id.text_text, R.id.text_created_at};
	static final int STATUS_LOADER = 47;
	
	SimpleCursorAdapter adapter;
	TimeLineReceiver receiver; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//managedQuery(StatusProvider.CONTENT_URI, null, null, null, StatusProvider.C_CREATED_AT+" DESC");
		//cursor = getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null, StatusProvider.C_CREATED_AT+" DESC"); //This is our data (the statuses in the data base); //getting our data
		adapter = new SimpleCursorAdapter(this, R.layout.row, null, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		setTitle(R.string.timeline);
		getLoaderManager().initLoader(-1, null, this);
	    setListAdapter(adapter);
	}
	

	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() !=R.id.text_created_at) {return false;}
			
			long time = cursor.getLong(cursor.getColumnIndex(StatusProvider.C_CREATED_AT));
			CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(time);
			((TextView)view).setText(relativeTime);
			return true;
		}
		
	};
	
	//called only once. When the Menu button is clicked. 
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.menu, menu);
			return true; 
					
		}
		
		

		@Override
		protected void onResume() {
			super.onResume();
			//register receiver programatically
			if(receiver == null)
			{
				receiver = new TimeLineReceiver(); //only instantiate if its not already made
			}
			registerReceiver(receiver, new IntentFilter("com.example.yamba.NEW_STATUS") );//problem...
		}
		
		@Override
		protected void onPause() {
			super.onPause();
			//un-register receiver programatically
			unregisterReceiver(receiver);
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
		
		//class within a class...used to update the timeline..
		class TimeLineReceiver extends BroadcastReceiver {

			private static final String TAG ="TimeLineReceiver";

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG,"TimeLineReceiver onReceive entered with count" +intent.getIntExtra("count: ", 0)); //problems with count...
			  //cursor = context.getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null, StatusProvider.C_CREATED_AT+" DESC"); //This is our data (the statuses in the data base); //getting our data
				getLoaderManager().restartLoader(STATUS_LOADER, null, TimeLineActivity.this);
				
				
			}
		}

		//Loader Manager.LoaderCallbacks. 
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(this, StatusProvider.CONTENT_URI, null, null, null, StatusProvider.C_CREATED_AT+" DESC"); //async way of oading data if we have lots of records 
		}



		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.swapCursor(cursor);
		}



		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
			
		}

}


