package com.beandirt.livepaper.dashboard;

import static com.beandirt.livepaper.database.LivePaperTables.Collections.FIELD_TITLE;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class MyCollections extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "MyCollections"; 
	
	private List<Collection> collections;
	protected LivePaperDbAdapter dbAdapter;
	protected Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collections);
	}

	protected void populateList(){
		cursor = dbAdapter.fetchPurchasedCollections(true);
		startManagingCursor(cursor);
		setListAdapter(new SimpleCursorAdapter(getApplicationContext(), R.layout.list_collection_item, cursor, new String[] {FIELD_TITLE}, new int[] {R.id.collection_title}));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,CollectionDetail.class);
		cursor.moveToPosition(position);
		intent.putExtra("cid", cursor.getString(1));
		startActivity(intent);
	}
	
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "onResume()");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		populateList();
	}
}
