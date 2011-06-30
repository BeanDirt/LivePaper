package com.beandirt.livepaper.dashboard;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_NAME;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.Place;
import com.beandirt.livepaper.dashboard.database.PlacesDbAdapter;

public class MyPlaces extends ListActivity {

	private static final String TAG = "MyPlaces";
	private PlacesDbAdapter dbAdapter;
	private SimpleCursorAdapter adapter;
	private Cursor places;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_places);
	}
	
	private void initList(){
		dbAdapter = PlacesDbAdapter.getInstanceOf(getApplicationContext());
		createFakePlace();
		
		places = dbAdapter.fetchPlaces();
		startManagingCursor(places);
		
		adapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.my_places_list_item,places,new String[] {FIELD_NAME}, new int[] {R.id.myPlacesName});
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getApplicationContext(), PlaceDetail.class);
		places.moveToPosition(position);
		intent.putExtra("rowid", places.getLong(0));
		startActivity(intent);
	}

	private void createFakePlace(){
		Place place = new Place("Golden Gate Bridge", "Baker Beach, San Francisco, CA", "this would be a longer description of the location", 37.8034211407655, -122.47789263725281, true);
		place = dbAdapter.createPlace(place);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initList();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		dbAdapter.close();
	}
	
}
