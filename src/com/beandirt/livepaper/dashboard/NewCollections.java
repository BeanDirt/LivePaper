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

public class NewCollections extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "NewCollections"; 
	
	private List<Collection> collections;
	protected LivePaperDbAdapter dbAdapter;
	protected Cursor cursor;
	
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,CollectionDetail.class);
		cursor.moveToPosition(position);
		intent.putExtra("rowid", cursor.getLong(0));
		startActivity(intent);
	}
	
	protected void onResume(){
		super.onResume();
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		populateList();
	}
}
