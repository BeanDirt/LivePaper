package com.beandirt.livepaper.dashboard;
import static com.beandirt.livepaper.dashboard.database.PlacesTable.Places.FIELD_NAME;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
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

	private void createFakePlace(){
		Place place = new Place("Baker Beach", "Baker Beach, San Francisco, CA", "this would be a longer description of the location", 37.8034211407655, -122.47789263725281, true);
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
