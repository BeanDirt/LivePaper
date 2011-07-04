package com.beandirt.livepaper.dashboard;

import static com.beandirt.livepaper.dashboard.database.LivePaperTables.Collections.FIELD_TITLE;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;

public class NewCollections extends LivePaperListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "NewCollections"; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_collections);
	}
	
	protected void populateList(){
		cursor = dbAdapter.fetchPurchasedCollections(false);
		startManagingCursor(cursor);
		setListAdapter(new SimpleCursorAdapter(getApplicationContext(), R.layout.list_collection_item, cursor, new String[] {FIELD_TITLE}, new int[] {R.id.collection_title}));
	}
	
	@Override
	protected void onResume(){
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		populateList();
		super.onResume();
	}
}
