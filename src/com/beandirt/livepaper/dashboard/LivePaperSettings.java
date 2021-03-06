package com.beandirt.livepaper.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class LivePaperSettings extends PreferenceActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "LivePaperSettings";
	private LivePaperDbAdapter dbAdapter; 
	private Cursor cursor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.livepaper_settings);
	}
	
	private void init(){
		populateCollections();
	}
	
	private void populateCollections(){
		
		ListPreference activeCollection = (ListPreference) getPreferenceManager().findPreference("collectionId");
		cursor = dbAdapter.fetchPurchasedCollections(true);
		startManagingCursor(cursor);
		CharSequence[] entries = new String[cursor.getCount()];
		CharSequence[] entryValues = new String[cursor.getCount()];
		
		int i = 0;
		while(cursor.moveToNext()){
			entries[i] = cursor.getString(2);
			entryValues[i] = cursor.getString(1);
			i++;
		}

		activeCollection.setEntries(entries);
		activeCollection.setEntryValues(entryValues);
	}
	
	protected void onResume(){
		super.onResume();
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		init();
	}
}
