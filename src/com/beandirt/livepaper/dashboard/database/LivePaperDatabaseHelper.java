package com.beandirt.livepaper.dashboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LivePaperDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "livepaper.db";
	private static int DATABASE_VERSION = 1;
	private static final String TAG = "CollectionsDatabaseHelper";
	
	public LivePaperDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table collections (" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"collection_id TEXT, " + 
				"title TEXT, " + 
				"price TEXT, " + 
				"trial BOOLEAN, " + 
				"enabled BOOLEAN, " + 
				"purchased BOOLEAN);");
		
		db.execSQL("create table photosets (" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"photoset_id STRING, " + 
				"title TEXT, " + 
				"width INTEGER, " + 
				"height INTEGER, " + 
				"collection TEXT NOT NULL, " +
				"active BOOLEAN, " + 
				"FOREIGN KEY(collection) REFERENCES collections(collection_id));");
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
