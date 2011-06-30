package com.beandirt.livepaper.dashboard.database;

import static com.beandirt.livepaper.dashboard.database.CollectionsTable.COLLECTIONS_TABLE;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_COLLECTION_ID;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_ENABLED;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_ID;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_PRICE;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_PURCHASED;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_TITLE;
import static com.beandirt.livepaper.dashboard.database.CollectionsTable.Collections.FIELD_TRIAL;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.beandirt.livepaper.dashboard.model.Collection;

public class CollectionsDbAdapter {

	private final Context context;
	private static CollectionsDbAdapter instance;
	private SQLiteDatabase db;
    private CollectionsDatabaseHelper dbHelper;
	
	private CollectionsDbAdapter(Context context){
		this.context = context;
	}
	
	public static CollectionsDbAdapter getInstanceOf(Context context){
		if(instance == null) {
	          instance = new CollectionsDbAdapter(context);
		}
	    instance.open();
	    return instance;
	}
	
	public CollectionsDbAdapter open() throws SQLException {
       	if(db == null || !db.isOpen()){
       		dbHelper = new CollectionsDatabaseHelper(context);
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
	
	public Cursor fetchCollections(){
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
	
	public Cursor fetchCollection(long rowid){
		return db.query(COLLECTIONS_TABLE, new String[] {
				FIELD_ID,
	            FIELD_COLLECTION_ID,
	            FIELD_TITLE,
	            FIELD_PRICE,
	            FIELD_TRIAL,
	            FIELD_ENABLED,
	            FIELD_PURCHASED
	        },  FIELD_ID + "=" + rowid, null, null, null, null);
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
	
	
	public int updateCollection(Collection collection){
		ContentValues updateValues = createContentValues(collection);
		return db.update(COLLECTIONS_TABLE, updateValues, FIELD_COLLECTION_ID + "=" + collection.getId(), null);
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
