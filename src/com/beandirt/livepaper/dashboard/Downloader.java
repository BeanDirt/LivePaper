package com.beandirt.livepaper.dashboard;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.service.FlickrService;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class Downloader extends Activity {
	
	private static final String TAG = "Downloader";
	private String collectionId;
	private long photosetRowId;
	private Cursor cursor;
	
	private ProgressDialog progressDialog;
	protected LivePaperDbAdapter dbAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloader);
    }
    
    private void retrievePhotos(){
    	progressDialog = new ProgressDialog(this);
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	progressDialog.setMessage("Gathering Photo Information...");
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
    	try{
			String photosetId = getPhotosetId(collectionId);
			photosetRowId = getPhotosetRowId(photosetId);
			
			getPhotoList(photosetId);
		}
		catch(Exception e){
			Log.e(TAG, e.getMessage());
			Log.e(TAG, "Problem Retrieving Photoset ID");
		}
	}
	
    private long getPhotosetRowId(String photosetId){
    	cursor = dbAdapter.fetchPhotoset(photosetId);
    	startManagingCursor(cursor);
    	cursor.moveToFirst();
    	long photosetRowId = cursor.getLong(0);
    	return photosetRowId;
    }
    
    private String getPhotosetId(String collectionId) throws CursorIndexOutOfBoundsException, NullPointerException{
		Display display = getWindowManager().getDefaultDisplay(); 
		
		String width = String.valueOf(display.getWidth());
		String height = String.valueOf(display.getHeight());
		
		cursor = dbAdapter.fetchPhotoset(collectionId, width, height);
		cursor.moveToFirst();
		String photosetId = cursor.getString(1);
		
		return photosetId;
	}
    
	private void getPhotoList(String photosetId){
		final String id = photosetId;
		AsyncTask<String, Integer, JSONObject> getPhotoList = new AsyncTask<String, Integer, JSONObject>() {

    		@Override
    		protected JSONObject doInBackground(String... urls) {
    			FlickrService service = new FlickrService();
    			return service.getPhotoList(id);
    		}
    		
    		@Override
    		protected void onPostExecute(JSONObject response) {
    			Log.d(TAG, response.toString());
    			try {
					JSONArray responsePhotos = response.getJSONObject("photoset").getJSONArray("photo");
					String[] photoURLs = new String[responsePhotos.length() - 1];
					String[] photoFormats = new String[responsePhotos.length() - 1];
					int k = 0;
					for(int i = 0; i < responsePhotos.length(); i++){
						String farm = responsePhotos.getJSONObject(i).getString("farm");
						String id = responsePhotos.getJSONObject(i).getString("id");
						String secret = responsePhotos.getJSONObject(i).getString("originalsecret");
						String format = responsePhotos.getJSONObject(i).getString("originalformat");
						String server = responsePhotos.getJSONObject(i).getString("server");
						String tags = responsePhotos.getJSONObject(i).getString("tags");
						
						String[] tagsArray = tags.split(" ");
						if(hasPreview(tagsArray)) {
							continue;
						}
						
						photoURLs[k] = buildPhotoURL(farm, id, secret, server, format);
						photoFormats[k] = format;
						k++;
					}
					downloadPhotos(photoURLs, photoFormats);
					
				} catch (JSONException e) {
					e.printStackTrace();
				} 
    		}
    	};
    	getPhotoList.execute();
	}
	
	private boolean hasPreview(String[] tags){
		for(int j = 0; j < tags.length; j++){
			if("preview".equals(tags[j])){
				return true;
			}
		}
		return false;
	}
	
	private void downloadPhotos(String[] photoURLs, String[] photoFormats){
    	new DownloadImageAsync().execute(photoURLs, photoFormats);
	}
	
	private String buildPhotoURL(String farm, String id, String secret, String server, String format){
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_o." + format;
	}
	
	public void confirmPurchase(View v){
		retrievePhotos();
	}
	
	private class DownloadImageAsync extends AsyncTask<String[], Integer, PhotoDownloadResult>{

		@Override
		protected PhotoDownloadResult doInBackground(String[]... photoArray) {
			try{
				int j = 0;
				int count;
				long totalSize = 0;
				int downloaded = 0;
				String[] urlArray = photoArray[0];
				for(String urlString : urlArray){
					URL url = new URL(urlString);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setDoOutput(true);
					conn.connect();
					totalSize += conn.getContentLength();
					
					j++;
					publishProgress((int)((j*100)/urlArray.length), null);
				}
				
				if(!checkAvailableSpace(totalSize)) return PhotoDownloadResult.INSUFFICIENT_STORAGE;
				
				int i = 0;
				
				for(String urlString : urlArray){
					URL url = new URL(urlString);
					URLConnection conn = url.openConnection();
					conn.connect();
					InputStream input = new BufferedInputStream(url.openStream(), 50000);
		            OutputStream output = new FileOutputStream(
		            		getDir(String.valueOf(photosetRowId), MODE_PRIVATE).toString() + 
		            		"/" +
		            		i + 
		            		"." + 
		            		photoArray[1][i]);
		            
		            // TODO: Scramble the filename so it is unclear what is what in the file xplorer
		            
					byte data[] = new byte[1024];
					while ((count = input.read(data)) != -1) {
						downloaded += count;
						output.write(data, 0, count);
						publishProgress((int)((downloaded*100)/totalSize), i + 1);
					}
					i++;
				}
			}
			catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			return PhotoDownloadResult.SUCCESS;
		}
		
		protected void onProgressUpdate(Integer... progress){
			progressDialog.setProgress(progress[0]);
			if(progress[1] != null) progressDialog.setMessage("Downloading Photo " + progress[1] + " of 24");
		}
		
		protected void onPostExecute(PhotoDownloadResult result){
			
			switch(result){
				case SUCCESS: 
					setActiveCollection();
					break;
				case INSUFFICIENT_STORAGE: 
					showFailure(getString(R.string.error_insufficient_storage));
					break;
			}
		}
	}
	
	private void showFailure(String error){
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
		progressDialog.dismiss();
	}
	
	private void setActiveCollection(){
		cursor = dbAdapter.fetchPurchasedCollections(true);
		
		// TODO: instead of checking to see if I've only one collection,
		// I should check to see if the collectionId is set
		
		if(cursor.getCount() == 0){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("collectionId", collectionId);
			editor.commit();
			
			enableWallpaper();
		}
		
		dbAdapter.setActivePhotoset(String.valueOf(photosetRowId));
		dbAdapter.setPurchasedCollection(collectionId);
		
		progressDialog.dismiss();
	    Intent intent = new Intent(getApplicationContext(), LivePaperDashboard.class);
	    startActivity(intent);
	}
	
	private void enableWallpaper(){
		ComponentName componentToDisable =
			  new ComponentName("com.beandirt.livepaper",
			  "com.beandirt.livepaper.wallpaper.LivePaper");
		
		getPackageManager().setComponentEnabledSetting(
			  componentToDisable,
			  PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}
	
	private boolean checkAvailableSpace(long bytesNeeded){
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
	    long bytesAvailable = (long)stat.getFreeBlocks() * (long)stat.getBlockSize();;
	    //return false;
	    return (bytesAvailable > bytesNeeded);
	}
	
	public enum PhotoDownloadResult{
		SUCCESS, INSUFFICIENT_STORAGE;
	}
	
	protected void onResume(){
		super.onResume();
		collectionId = getIntent().getExtras().getString("cid");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
	}
}