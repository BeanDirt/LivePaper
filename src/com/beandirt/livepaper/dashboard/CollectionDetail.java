package com.beandirt.livepaper.dashboard;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;
public class CollectionDetail extends LivePaperActivity {

	private static final String TAG = "CollectionDetail";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail);

        setFonts();
	}
	private void init(){
		retrievePhoto();
		//populate();
	}
	
	private void retrievePhoto(){
		try{
			long rowid = getIntent().getExtras().getLong("rowid");
			Cursor cursor = dbAdapter.fetchCollection(rowid);
			Log.d(TAG, String.valueOf(cursor.getCount()));
			cursor.moveToFirst();
			String collectionId = cursor.getString(1);
			
			Display display = getWindowManager().getDefaultDisplay(); 
			
			String width = String.valueOf(display.getWidth());
			String height = String.valueOf(display.getHeight());
			
			cursor = dbAdapter.fetchPhotoset(collectionId, width, height);
			cursor.moveToFirst();
			
			String photosetId = cursor.getString(1);
			Log.d(TAG, photosetId);
			startManagingCursor(cursor);
		}
		catch(Exception e){
			Log.e(TAG, "Problem Retrieving Photoset ID");
		}
	}
	
	private void populate(){
		TextView title = (TextView) findViewById(R.id.placeTitle);
		TextView subtitle = (TextView) findViewById(R.id.placeSubTitle);

		//title.setText(place.getName());
		//subtitle.setText(place.getLocation_short());
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
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		init();
		super.onResume();
	}
}
