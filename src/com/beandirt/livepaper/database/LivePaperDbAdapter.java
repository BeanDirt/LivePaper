package com.beandirt.livepaper.database;

import static com.beandirt.livepaper.database.LivePaperTables.COLLECTIONS_TABLE;
import static com.beandirt.livepaper.database.LivePaperTables.PHOTOSETS_TABLE;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_COLLECTION_ID;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_ENABLED;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_ID;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_PRICE;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_PURCHASED;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_TITLE;
import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_TRIAL;
import static com.beandirt.livepaper.database.LivePaperTables.Photosets.FIELD_ACTIVE;
import static com.beandirt.livepaper.database.LivePaperTables.Photosets.FIELD_COLLECTION;
import static com.beandirt.livepaper.database.LivePaperTables.Photosets.FIELD_HEIGHT;
import static com.beandirt.livepaper.database.LivePaperTables.Photosets.FIELD_PHOTOSET_ID;
import static com.beandirt.livepaper.database.LivePaperTables.Photosets.FIELD_WIDTH;
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
		Log.i(TAG, "Database was closed");
		dbHelper.close();
		db.close();
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
	
	public Cursor fetchPurchasedCollections(boolean purchased, String width, String height){
		
		/*String query = "SELECT * FROM " +  + 
						" WHERE p." + FIELD_WIDTH + 
						" =? AND p." + FIELD_HEIGHT +
						" =?";*/
		
		//return db.execSQL(query, new String[] {(purchased?"1":"0"), width, height});
		
		return db.query(COLLECTIONS_TABLE + 
						" c INNER JOIN " + PHOTOSETS_TABLE + 
						" p ON c." + FIELD_COLLECTION_ID +
						" = p." + FIELD_COLLECTION, new String[]{
				"c." + FIELD_ID,
				FIELD_COLLECTION_ID,
	            "c." + FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED}, "c." + FIELD_PURCHASED + " =? AND p." + FIELD_WIDTH + " =? AND p." + FIELD_HEIGHT + " =?", new String[] {(purchased?"1":"0"), width, height}, null, null, null);
		
		//return db.rawQuery(query, new String[] {(purchased?"1":"0"), width, height});
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
	
	public Cursor fetchCollection(String collectionId){
		return db.query(COLLECTIONS_TABLE, new String[] {
				FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        },  FIELD_COLLECTION_ID + "=?", new String[] {collectionId}, null, null, null);
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
	
	public Cursor fetchPhotoset(String photosetId){
		return db.query(PHOTOSETS_TABLE, new String[] {
				FIELD_ID,
				FIELD_PHOTOSET_ID,
	            FIELD_TITLE,
	            FIELD_WIDTH,
	            FIELD_HEIGHT,
	            FIELD_COLLECTION,
	            FIELD_ACTIVE,
	        },  FIELD_PHOTOSET_ID + "=?", new String[] {photosetId}, null, null, null);
	}
	
	public Cursor fetchActivePhotoset(String collectionId){
		return db.query(PHOTOSETS_TABLE, new String[] {
				FIELD_ID
	        },  FIELD_COLLECTION + "=? AND " + FIELD_ACTIVE + "=?", new String[] {collectionId, "1"}, null, null, null);
	}
	
	public int setActivePhotoset(String photosetRowId){
		ContentValues updateValues = new ContentValues();
		updateValues.put(FIELD_ACTIVE, true);
		Log.d(TAG, ""+db.isOpen() + "  " + "photoset update");
		return db.update(PHOTOSETS_TABLE, updateValues, FIELD_ID + "=?", new String[] {photosetRowId});
	}
	
	public int setPurchasedCollection(String collectionId){
		ContentValues updateValues = new ContentValues();
		updateValues.put(FIELD_PURCHASED, true);
		return db.update(COLLECTIONS_TABLE, updateValues, FIELD_COLLECTION_ID + "=?", new String[] {collectionId});
	}
	
	
	public int updateCollection(Collection collection){
		ContentValues updateValues = updateContentValues(collection);
		String collectionId = collection.getId();
		Log.d(TAG, ""+db.isOpen() + "  " + "collection update");
		return db.update(COLLECTIONS_TABLE, updateValues, FIELD_COLLECTION_ID + "=?", new String[] {collectionId});
	}
	
	public int updatePhotoset(Photoset photoset){
		ContentValues updateValues = updateContentValues(photoset);
		String photosetId = photoset.getId();
		String collectionId = photoset.getCollection();
		Log.d(TAG, ""+db.isOpen());
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
	
	private ContentValues updateContentValues(Collection collection){
		ContentValues values = new ContentValues();
		values.put(FIELD_COLLECTION_ID, collection.getId());
		values.put(FIELD_TITLE, collection.getTitle());
        values.put(FIELD_PRICE, collection.getPrice());
        values.put(FIELD_TRIAL, collection.getTrial());
        values.put(FIELD_ENABLED, collection.getEnabled());
        return values;
	}
	
	private ContentValues updateContentValues(Photoset photoset){
		ContentValues values = new ContentValues();
		values.put(FIELD_PHOTOSET_ID, photoset.getId());
		values.put(FIELD_TITLE, photoset.getTitle());
        values.put(FIELD_WIDTH, photoset.getWidth());
        values.put(FIELD_HEIGHT, photoset.getHeight());
        values.put(FIELD_COLLECTION, photoset.getCollection());
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
