package com.beandirt.livepaper.dashboard;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class CollectionDetail extends Activity {

	private Cursor cursor;
	
	@SuppressWarnings("unused")
	private static final String TAG = "CollectionDetail";
	private String collectionId;
	private String collectionName;
	protected LivePaperDbAdapter dbAdapter;
	
	RelativeLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_detail);
		
		collectionId = getIntent().getExtras().getString("cid");
		
        setFonts();
        layout = (RelativeLayout) findViewById(R.id.collection_layout);
	}
	
	private void init(){
		String photosetId = getPhotosetId(collectionId);
		new GetPreviewImageURLAsync().execute(photosetId);
		populate(collectionId, photosetId);
	}
	
	private void populate(String collectionId, String photosetId){
		
		collectionName = getCollectionName(collectionId);
		
		final String cid = collectionId;
		final String pid = photosetId;
		
		Button collectionAction = (Button) findViewById(R.id.collection_action_button);
		collectionAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isActivePhotoset(cid, pid)) setActivePhotoset();
				else goToDownloader();
			}
		});
		
	}
	
	private String getCollectionName(String collectionId){
		cursor = dbAdapter.fetchCollection(collectionId);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		return cursor.getString(2);
	}
	
	private Boolean isActivePhotoset(String collectionId, String photosetId){
		cursor = dbAdapter.fetchPhotoset(photosetId);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		return(cursor.getInt(6) == 1);
	}
	
	private void setActivePhotoset(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("collectionId",collectionId);
		editor.commit();
		
		Toast.makeText(getApplicationContext(), String.format(getString(R.string.wallpaper_set),collectionName), Toast.LENGTH_SHORT).show();
	}
	
	private void setFonts(){
		Typeface myriad_pro = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
		TextView title = (TextView) findViewById(R.id.placeTitle);
		title.setTypeface(myriad_pro);
	}
	
	
	
	private void goToDownloader(){
		Intent intent = new Intent(this, Downloader.class);
		intent.putExtra("cid", collectionId);
		startActivity(intent);
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
				ProgressBar pb = (ProgressBar) findViewById(R.id.collection_detail_spinner);
				pb.setVisibility(View.INVISIBLE);
				
				// this doesn't work for some reason
				
				/*Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
				ProgressBar pb = (ProgressBar) findViewById(R.id.collection_detail_spinner);
				pb.startAnimation(fadeOut);*/
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
