package com.beandirt.livepaper.dashboard.database;

import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_ID;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_ENABLED;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_LATITUDE;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_LOCATION_LONG;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_LOCATION_SHORT;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_LONGITUDE;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_NAME;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.PLACES_TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PlacesDbAdapter {

	private final Context context;
	private static PlacesDbAdapter instance;
	private SQLiteDatabase db;
    private PlacesDatabaseHelper dbHelper;
	
	private PlacesDbAdapter(Context context){
		this.context = context;
	}
	
	public static PlacesDbAdapter getInstanceOf(Context context){
		if(instance == null) {
	          instance = new PlacesDbAdapter(context);
		}
	    instance.open();
	    return instance;
	}
	
	public PlacesDbAdapter open() throws SQLException {
       	if(db == null || !db.isOpen()){
       		dbHelper = new PlacesDatabaseHelper(context);
       		db = dbHelper.getWritableDatabase();
       	}
        return this;
    }
	
	public void close(){
		dbHelper.close();
	}
	
	public Place createPlace(Place place){
		ContentValues initialValues = createContentValues(place);
		place.setId(db.insert(PLACES_TABLE, null, initialValues));
		return place;
	}
	
	public Cursor fetchPlaces(){
		return db.query(PLACES_TABLE, new String[] {
	            FIELD_ID,
	            FIELD_NAME,
	            FIELD_LOCATION_SHORT,
	            FIELD_LOCATION_LONG,
	            FIELD_LATITUDE,
	            FIELD_LONGITUDE,
	            FIELD_ENABLED
	        }, null, null, null, null, null);
	}
	
	public Cursor fetchPlace(long rowid){
		return db.query(PLACES_TABLE, new String[] {
	            FIELD_ID,
	            FIELD_NAME,
	            FIELD_LOCATION_SHORT,
	            FIELD_LOCATION_LONG,
	            FIELD_LATITUDE,
	            FIELD_LONGITUDE,
	            FIELD_ENABLED
	        },  FIELD_ID + "=" + rowid, null, null, null, null);
	}
	
	private ContentValues createContentValues(Place place){
		ContentValues values = new ContentValues();
		values.put(FIELD_NAME, place.getName());
        values.put(FIELD_LOCATION_SHORT, place.getLocation_short());
        values.put(FIELD_LOCATION_LONG, place.getLocation_long());
        values.put(FIELD_LATITUDE, place.getLatitude());
        values.put(FIELD_LONGITUDE, place.getLongitude());
        values.put(FIELD_ENABLED, place.getEnabled());
        return values;
	}
}
