package com.beandirt.livepaper.dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;

public class Downloader extends LivePaperActivity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "Downloader";
	private long collectionId;
	
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloader);
    }
    
    private void retrievePhotos(){
    	progressDialog = new ProgressDialog(this);
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	progressDialog.setMessage("Loading...");
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
    	try{
			String photosetId = getPhotosetId(collectionId);
			getPhotoList(photosetId);
		}
		catch(Exception e){
			Log.e(TAG, "Problem Retrieving Photoset ID");
		}
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
    
	private void getPhotoList(String photosetId){
		final String id = photosetId;
		AsyncTask<String, Integer, JSONObject> getPhotoList = new AsyncTask<String, Integer, JSONObject>() {

    		@Override
    		protected JSONObject doInBackground(String... urls) {
    			FlickrWebService service = new FlickrWebService();
    			return service.getPhotoList(id);
    		}
    		
    		@Override
    		protected void onProgressUpdate(Integer... progress){
    			//progressDialog.setProgress(progress[0]);
    			//Log.d(TAG,String.valueOf(progress[0]));
    		}
    		
    		@Override
    		protected void onPostExecute(JSONObject response) {
    			Log.d(TAG, response.toString());
    			try {
					JSONArray responsePhotos = response.getJSONObject("photoset").getJSONArray("photo");
					String[] photoURLs = new String[responsePhotos.length()];
					for(int i = 0; i < responsePhotos.length(); i++){
						String farm = responsePhotos.getJSONObject(i).getString("farm");
						String id = responsePhotos.getJSONObject(i).getString("id");
						String secret = responsePhotos.getJSONObject(i).getString("originalsecret");
						String format = responsePhotos.getJSONObject(i).getString("originalformat");
						String server = responsePhotos.getJSONObject(i).getString("server");
						
						photoURLs[i] = buildPhotoURL(farm, id, secret, server, format);
					}
					
					downloadPhotos(photoURLs);
					
				} catch (JSONException e) {
					e.printStackTrace();
				} 
    		}
    	};
    	getPhotoList.execute();
	}
	
	private void downloadPhotos(String[] photoURLs){
		AsyncTask<String, Integer, JSONObject> getPhotoList = new AsyncTask<String, Integer, JSONObject>() {

    		@Override
    		protected JSONObject doInBackground(String... urls) {
    			int count = urls.length;
    			long totalSize = 0;
    			for (int i = 0; i < count; i++) {
    				totalSize += Downloader.downloadFile(urls[i]);
    				publishProgress((int) ((i / (float) count) * 100));
    			}
    	        return totalSize;
    			
    	        FlickrWebService service = new FlickrWebService();
    			return service.getPhotoList(id);
    		}
    		
    		@Override
    		protected void onProgressUpdate(Integer... progress){
    			//progressDialog.setProgress(progress[0]);
    			//Log.d(TAG,String.valueOf(progress[0]));
    		}
    		
    		@Override
    		protected void onPostExecute(JSONObject response) {
    			Log.d(TAG, response.toString());
    			try {
					JSONArray responsePhotos = response.getJSONObject("photoset").getJSONArray("photo");
					String[] photoURLs = new String[responsePhotos.length()];
					for(int i = 0; i < responsePhotos.length(); i++){
						String farm = responsePhotos.getJSONObject(i).getString("farm");
						String id = responsePhotos.getJSONObject(i).getString("id");
						String secret = responsePhotos.getJSONObject(i).getString("originalsecret");
						String format = responsePhotos.getJSONObject(i).getString("originalformat");
						String server = responsePhotos.getJSONObject(i).getString("server");
						
						photoURLs[i] = buildPhotoURL(farm, id, secret, server, format);
					}
					
					downloadPhotos(photoURLs);
					
				} catch (JSONException e) {
					e.printStackTrace();
				} 
    		}
    	};
    	getPhotoList.execute();
	}
	
	private String buildPhotoURL(String farm, String id, String secret, String server, String format){
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_o." + format;
	}
	
	public void confirmPurchase(View v){
		retrievePhotos();
	}
	
	@Override
	protected void onResume(){
		collectionId = getIntent().getExtras().getLong("rowid");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		super.onResume();
	}
}