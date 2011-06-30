package com.beandirt.livepaper.dashboard.database;

import android.provider.BaseColumns;

public final class CollectionsTable {

	public CollectionsTable(){}
	public static final String COLLECTIONS_TABLE = "collections";
	
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
}
