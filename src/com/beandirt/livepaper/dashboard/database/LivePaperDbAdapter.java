package com.beandirt.livepaper.dashboard.database;

import static com.beandirt.livepaper.dashboard.database.LivePaperTables.COLLECTIONS_TABLE;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.PHOTOSETS_TABLE;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_COLLECTION_ID;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_ENABLED;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_ID;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_PRICE;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_PURCHASED;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_TITLE;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_TRIAL;
import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Photosets.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.dashboard.model.Photoset;

public class LivePaperDbAdapter {

	private static final String TAG = "LivePaperDbAdapter";
	
	private final Context context;
	private static LivePaperDbAdapter instance;
	private SQLiteDatabase db;
    private LivePaperDatabaseHelper dbHelper;
	
	private LivePaperDbAdapter(Context context){
		this.context = context;
	}
	
	public static LivePaperDbAdapter getInstanceOf(Context context){
		if(instance == null) {
	          instance = new LivePaperDbAdapter(context);
		}
	    instance.open();
	    return instance;
	}
	
	public LivePaperDbAdapter open() throws SQLException {
       	if(db == null || !db.isOpen()){
       		dbHelper = new LivePaperDatabaseHelper(context);
       		db = dbHelper.getWritableDatabase();
       	}
        return this;
    }
	
	public void close(){
		dbHelper.close();
	}
	
	public Collection createCollection(Collection collection){
		ContentValues initialValues = createContentValues(collection);
		collection.setRowId(db.insert(COLLECTIONS_TABLE, null, initialValues));
		return collection;
	}
	
	public Photoset createPhotoset(Photoset photoset){
		ContentValues initialValues = createContentValues(photoset);
		photoset.setRowId(db.insert(PHOTOSETS_TABLE, null, initialValues));
		return photoset;
	}
	
	public Cursor fetchAllCollections(){
		return db.query(COLLECTIONS_TABLE, new String[] {
	            FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        }, null, null, null, null, null);
	}
	
	public Cursor fetchPurchasedCollections(boolean purchased){
		return db.query(COLLECTIONS_TABLE, new String[] {
	            FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        }, FIELD_PURCHASED + "=?", new String[] {(purchased?"1":"0")}, null, null, null);
	}
	
	public Cursor fetchCollection(long rowid){
		return db.query(COLLECTIONS_TABLE, new String[] {
				FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        },  FIELD_ID + "=?", new String[] {String.valueOf(rowid)}, null, null, null);
	}
	
	public Cursor fetchCollection(String collectionid){
		return db.query(COLLECTIONS_TABLE, new String[] {
				FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        },  FIELD_ID + "=" + collectionid, null, null, null, null);
	}
	
	public Cursor fetchPhotoset(String collectionId, String width, String height){
		return db.query(PHOTOSETS_TABLE, new String[] {
				FIELD_ID,
				FIELD_PHOTOSET_ID,
	            FIELD_TITLE,
	            FIELD_WIDTH,
	            FIELD_HEIGHT,
	            FIELD_COLLECTION,
	            FIELD_ACTIVE,
	        },  FIELD_COLLECTION + "=? AND " + FIELD_WIDTH + "=? AND " + FIELD_HEIGHT + "=?", new String[] {collectionId,width,height}, null, null, null);
	}
	
	
	public int updateCollection(Collection collection){
		ContentValues updateValues = createContentValues(collection);
		String collectionId = collection.getId();
		return db.update(COLLECTIONS_TABLE, updateValues, FIELD_COLLECTION_ID + "=?", new String[] {collectionId});
	}
	
	public int updatePhotoset(Photoset photoset){
		ContentValues updateValues = createContentValues(photoset);
		String photosetId = photoset.getId();
		String collectionId = photoset.getCollection();
		return db.update(PHOTOSETS_TABLE, updateValues, FIELD_PHOTOSET_ID + "=? AND " + FIELD_COLLECTION + "=?", new String[] {photosetId,collectionId});
	}
	
	private ContentValues createContentValues(Photoset photoset){
		ContentValues values = new ContentValues();
		values.put(FIELD_PHOTOSET_ID, photoset.getId());
		values.put(FIELD_TITLE, photoset.getTitle());
        values.put(FIELD_WIDTH, photoset.getWidth());
        values.put(FIELD_HEIGHT, photoset.getHeight());
        values.put(FIELD_COLLECTION, photoset.getCollection());
        values.put(FIELD_ACTIVE, photoset.getActive());
        return values;
	}
	
	private ContentValues createContentValues(Collection collection){
		ContentValues values = new ContentValues();
		values.put(FIELD_COLLECTION_ID, collection.getId());
		values.put(FIELD_TITLE, collection.getTitle());
        values.put(FIELD_PRICE, collection.getPrice());
        values.put(FIELD_TRIAL, collection.getTrial());
        values.put(FIELD_ENABLED, collection.getEnabled());
        values.put(FIELD_PURCHASED, collection.getPurchased());
        return values;
	}
}
