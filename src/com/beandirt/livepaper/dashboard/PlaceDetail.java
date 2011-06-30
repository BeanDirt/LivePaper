package com.beandirt.livepaper.dashboard;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.Place;
import com.beandirt.livepaper.dashboard.database.PlacesDbAdapter;
public class PlaceDetail extends Activity {

	private PlacesDbAdapter dbAdapter;
	private Place place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail);

        setFonts();
	}
	private void init(){
		dbAdapter = PlacesDbAdapter.getInstanceOf(getApplicationContext());
		retrievePlace();
		populate();
	}
	
	private void populate(){
		TextView title = (TextView) findViewById(R.id.placeTitle);
		TextView subtitle = (TextView) findViewById(R.id.placeSubTitle);

		title.setText(place.getName());
		subtitle.setText(place.getLocation_short());
	}

	private void retrievePlace(){
		long rowid = getIntent().getExtras().getLong("rowid");
		Cursor placeCursor = dbAdapter.fetchPlace(rowid);
		placeCursor.moveToFirst();
		startManagingCursor(placeCursor);
		
		place = new Place(placeCursor.getString(1),
				placeCursor.getString(2),
				placeCursor.getString(3),
				placeCursor.getDouble(4),
				placeCursor.getDouble(5),
				Boolean.valueOf(placeCursor.getString(6)));
	}
	
	
	private void setFonts(){
		Typeface myriad_pro = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
		TextView title = (TextView) findViewById(R.id.placeTitle);
		title.setTypeface(myriad_pro);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		init();
	}
}
