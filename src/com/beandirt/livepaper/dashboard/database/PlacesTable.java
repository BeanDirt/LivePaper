package com.beandirt.livepaper.dashboard.database;

import android.provider.BaseColumns;

public final class PlacesTable {

	public PlacesTable(){}
	public static final String PLACES_TABLE = "places";
	
	public static final class Places implements BaseColumns{
		private Places(){}
		public static final String FIELD_ID = "_id";
		public static final String FIELD_NAME = "name";
		public static final String FIELD_LOCATION_SHORT = "location_short";
		public static final String FIELD_LOCATION_LONG = "location_long";
		public static final String FIELD_LATITUDE = "latitude";
		public static final String FIELD_LONGITUDE = "longitude";
		public static final String FIELD_ENABLED = "enabled";
	}
}
