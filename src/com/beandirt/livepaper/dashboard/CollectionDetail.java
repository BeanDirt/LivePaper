package com.beandirt.livepaper.dashboard;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;
import com.beandirt.livepaper.database.LivePaperDbAdapter;
public class CollectionDetail extends Activity {

	private Cursor cursor;
	
	@SuppressWarnings("unused")
	private static final String TAG = "CollectionDetail";
	private long collectionRowId;
	
	protected LivePaperDbAdapter dbAdapter;
	
	RelativeLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_detail);
		
		collectionRowId = getIntent().getExtras().getLong("rowid");
		
        setFonts();
        layout = (RelativeLayout) findViewById(R.id.collection_layout);
	}
	
	private void init(){
		String photosetId = getPhotosetId(collectionRowId);
		new GetPreviewImageURLAsync().execute(photosetId);
		populate();
	}
	
	private void populate(){
		
	}
	
	private void setFonts(){
		Typeface myriad_pro = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
		TextView title = (TextView) findViewById(R.id.placeTitle);
		title.setTypeface(myriad_pro);
	}
	
	public void goToDownloader(View v){
		Intent intent = new Intent(this, Downloader.class);
		intent.putExtra("rowid", collectionRowId);
		startActivity(intent);
	}
	
	private String getPhotosetId(long collectionRowId) throws CursorIndexOutOfBoundsException, NullPointerException{
		cursor = dbAdapter.fetchCollection(collectionRowId);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		String collectionId = cursor.getString(1);
		Display display = getWindowManager().getDefaultDisplay(); 
		
		String width = String.valueOf(display.getWidth());
		String height = String.valueOf(display.getHeight());
		
		cursor = dbAdapter.fetchPhotoset(collectionId, width, height);
		cursor.moveToFirst();
		String photosetId = cursor.getString(1);
		return photosetId;
	}
	
	private class GetPreviewImageURLAsync extends AsyncTask<String, Integer, JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			FlickrWebService service = new FlickrWebService();
			return service.getPhotoList(params[0]);
		}
		
		protected void onPostExecute(JSONObject response){
			try {
				JSONArray responsePhotos = response.getJSONObject("photoset").getJSONArray("photo");
				allPhotos:for(int i = 0; i < responsePhotos.length(); i++){
					String tags = responsePhotos.getJSONObject(i).getString("tags");
					String[] tagsArray = tags.split(" ");
					for(int j = 0; j < tagsArray.length; j++){
						if("preview".equals(tagsArray[j])){
							String farm = responsePhotos.getJSONObject(i).getString("farm");
							String id = responsePhotos.getJSONObject(i).getString("id");
							String secret = responsePhotos.getJSONObject(i).getString("originalsecret");
							String format = responsePhotos.getJSONObject(i).getString("originalformat");
							String server = responsePhotos.getJSONObject(i).getString("server");
							String previewURL = buildPhotoURL(farm, id, secret, server, format);
							downloadPreview(previewURL);
							break allPhotos;
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private class DownloadPreviewAsync extends AsyncTask<String, Integer, BitmapDrawable>{

		@Override
		protected BitmapDrawable doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();
	            conn.setDoInput(true);
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            BitmapDrawable bitmap = new BitmapDrawable(getResources(),is);
	            return bitmap;
	          } catch (Exception e) {
	        	  e.printStackTrace();
	        	  return null;
	          }
		}
		
		protected void onPostExecute(BitmapDrawable result){
			if(result != null){
				layout.setBackgroundDrawable(result);
			}
		}
	}
	
	private void downloadPreview(String photoURL){
    	new DownloadPreviewAsync().execute(photoURL);
	}
	
	private String buildPhotoURL(String farm, String id, String secret, String server, String format){
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_o." + format;
	}
	
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "onResume()");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		init();
	}
}
