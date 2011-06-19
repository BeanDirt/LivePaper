package com.beandirt.livepaper.dashboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlacesDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "livepaper.db";
	private static int DATABASE_VERSION = 1;
	private static final String TAG = "PlacesDatabaseHelper";
	
	public PlacesDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table places (" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"name TEXT, " + 
				"location_short TEXT, " + 
				"location_long TEXT, " + 
				"latitude DOUBLE, " + 
				"longitude DOUBLE, " +
				"enabled BOOLEAN);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ".");
		if(newVersion > oldVersion){
			db.beginTransaction();
			try{
				switch(newVersion){
				case 2: // upgrade to version 2 here
				}
			}
			finally{
				db.endTransaction(); 
			}
		}
		
	}

}
