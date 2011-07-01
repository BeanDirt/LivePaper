package com.beandirt.livepaper.dashboard;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;

import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;
import com.beandirt.livepaper.dashboard.model.Collection;

public class LivePaperListActivity extends ListActivity {
	
	private static final String TAG = "LivePaperListActivity";
	
	private List<Collection> collections;
	protected LivePaperDbAdapter dbAdapter;
	protected Cursor cursor;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,CollectionDetail.class);
		cursor.moveToPosition(position);
		intent.putExtra("rowid", cursor.getLong(0));
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy(){
		dbAdapter.close();
		super.onDestroy();
	}
	
}
