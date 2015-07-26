package com.example.yamba;


import winterwell.jtwitter.Twitter.Status;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {

	public static  final String TAG = "StatusProvider";
	public static final String AUTHORITY = "content://com.example.yamba.provider"; 
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_USER = "user_name";
	public static final String C_TEXT = "status_text";
	public static final String TABLE = "status";
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 2;
		
	DbHelper dbHelper; 
	SQLiteDatabase db; 
	

	public static ContentValues statusToValues(Status status) {
		
		ContentValues values  = new ContentValues();
		
		values.put(C_ID, status.id);
		values.put(C_USER, status.user.name);
		values.put(C_TEXT, status.text);
		values.put(C_CREATED_AT, status.createdAt.getTime());
		
		return values;
		
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		Log.d(TAG, "OnCreate done");
		return true; //return true when successfully created. 
		
	
	}
	
	@Override
	public String getType(Uri uri) {
		//making up a MIME type...would work fine if we simply returned null. 
		if(uri.getLastPathSegment() == null){
			return "vnd.android.cursor.item/vnd.example.yamba.status";
		} 
		
		else{
			
			return "vnd.android.cursor.dir/vnd.example.yamba.status";
		}
		
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		long id = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		if(id!=-1){
			uri= Uri.withAppendedPath(uri, Long.toString(id));
		}
		Log.d(TAG, "Uri Insert done");
			return uri;
			
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.query(false, TABLE, projection, selection, selectionArgs, null, null, sortOrder, null, null); //query the data base... 
		Log.d(TAG, "query done");
    	return cursor;
    	
		
	}
	
	
	 class DbHelper extends SQLiteOpenHelper {

			public static final String TAG = "DbHelper";
			public DbHelper(Context context) {
				
				super(context, DB_NAME, null, DB_VERSION); /////////////<---------NEED to fix context video: 30.00. 
				 Log.d(TAG,"Entered DbHelper");
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				Log.d(TAG,"Entered OnCreate of DbHelper");
				//be very careful as any mistake in the sql commands will make the app fail. Then you will need to change the code and re-install the app. 
				String sql = String.format("create table %s "+"(%s int primary key, %s int, %s text, %s text)", TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
				Log.d(TAG,"onCreate with Sql: "+sql);
				db.execSQL(sql);
				Log.d(TAG,"db.execSQL is done");
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				//usually ALTER TABLE statement 
				Log.d(TAG, "OnUpgrade from version: " + oldVersion + "to new version: "+ newVersion);
				db.execSQL("dop if exists" + TABLE); 
				onCreate(db);
			}
		
		}
}
