package com.beandirt.livepaper.database;

import android.provider.BaseColumns;

public final class LivePaperTables {

	public LivePaperTables(){}
	public static final String COLLECTIONS_TABLE = "collections";
	public static final String PHOTOSETS_TABLE = "photosets";
	
	public static final class Collections implements BaseColumns{
		private Collections(){}
		public static final String FIELD_ID = "_id";
		public static final String FIELD_COLLECTION_ID = "collection_id";
		public static final String FIELD_TITLE = "title";
		public static final String FIELD_PRICE = "price";
		public static final String FIELD_TRIAL = "trial";
		public static final String FIELD_ENABLED = "enabled";
		public static final String FIELD_PURCHASED = "purchased";
	}
	
	public static final class Photosets implements BaseColumns{
		private Photosets(){}
		public static final String FIELD_ID = "_id";
		public static final String FIELD_PHOTOSET_ID = "photoset_id";
		public static final String FIELD_TITLE = "title";
		public static final String FIELD_WIDTH = "width";
		public static final String FIELD_HEIGHT = "height";
		public static final String FIELD_COLLECTION = "collection";
		public static final String FIELD_ACTIVE = "active";
	}
}
