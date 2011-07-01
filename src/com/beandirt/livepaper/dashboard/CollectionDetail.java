package com.beandirt.livepaper.dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;
public class CollectionDetail extends LivePaperActivity {

	private static final String TAG = "CollectionDetail";
	private String[] photoURLs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_detail);

        setFonts();
	}
	private void init(){
	}
	
	public void retrievePhotos(View v){
		try{
			String photosetId = getPhotosetId(getIntent().getExtras().getLong("rowid"));
			getPhotoList(photosetId);
		}
		catch(Exception e){
			Log.e(TAG, "Problem Retrieving Photoset ID");
		}
	}
	
	private void getPhotoList(String photosetId){
		final String id = photosetId;
		AsyncTask<Object, Object, JSONObject> getPhotoList = new AsyncTask<Object, Object, JSONObject>() {

    		@Override
    		protected JSONObject doInBackground(Object... params) {
    			FlickrWebService service = new FlickrWebService();
    			return service.getPhotoList(id);
    		}
    		
    		@Override
    		protected void onPostExecute(JSONObject response) {
    			Log.d(TAG, response.toString());
    			try {
					JSONArray responsePhotos = response.getJSONObject("photoset").getJSONArray("photo");
					photoURLs = new String[responsePhotos.length()];
					for(int i = 0; i < responsePhotos.length(); i++){
						String farm = responsePhotos.getJSONObject(i).getString("farm");
						String id = responsePhotos.getJSONObject(i).getString("id");
						String secret = responsePhotos.getJSONObject(i).getString("originalsecret");
						String format = responsePhotos.getJSONObject(i).getString("originalformat");
						String server = responsePhotos.getJSONObject(i).getString("server");
						
						photoURLs[i] = buildPhotoURL(farm, id, secret, server, format);
						Log.d(TAG, photoURLs[i]);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    		}
    	};
    	getPhotoList.execute();
	}
	
	private String buildPhotoURL(String farm, String id, String secret, String server, String format){
		
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_o." + format;
	}
	
	private String getPhotosetId(long collectionRowId) throws CursorIndexOutOfBoundsException, NullPointerException{
		Cursor cursor = dbAdapter.fetchCollection(collectionRowId);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		String collectionId = cursor.getString(1);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		
		String width = String.valueOf(display.getWidth());
		String height = String.valueOf(display.getHeight());
		
		cursor = dbAdapter.fetchPhotoset(collectionId, width, height);
		cursor.moveToFirst();
		
		return cursor.getString(1);
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
		dbAdapter.close();
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
