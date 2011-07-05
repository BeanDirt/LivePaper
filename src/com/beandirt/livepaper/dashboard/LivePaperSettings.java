package com.beandirt.livepaper.dashboard;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class LivePaperSettings extends PreferenceActivity {

	private static final String TAG = "LivePaperSettings";
	private LivePaperDbAdapter dbAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.livepaper_settings);
	}
	
	private void init(){
		populateCollections();
	}
	
	private void populateCollections(){
		
		ListPreference activeCollection = (ListPreference) getPreferenceManager().findPreference("active_collection");
		Cursor c = dbAdapter.fetchPurchasedCollections(true);
		startManagingCursor(c);
		CharSequence[] entries = new String[c.getCount()];
		CharSequence[] entryValues = new String[c.getCount()];
		
		int i = 0;
		while(c.moveToNext()){
			entries[i] = c.getString(2);
			entryValues[i] = c.getString(0);
			i++;
		}
		Log.d(TAG, ""+entries.length);
		activeCollection.setEntries(entries);
		activeCollection.setEntryValues(entryValues);
	}
	
	@Override
	protected void onStart(){
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		init();
		super.onStart();
	}
	
	@Override
	protected void onStop(){
		dbAdapter.close();
		super.onStop();
	}
}
