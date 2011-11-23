package com.beandirt.livepaper.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class LivePaperDashboard extends Activity {
	
	private static final String TAG = "LivePaperDashboard";
	
	protected LivePaperDbAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
	private void init(){
		
	}
	
	
	public void gotoNewCollections(View v){
		Intent intent = new Intent(this, NewCollections.class);
		startActivity(intent);
	}
	
	public void gotoMyCollections(View v){
		Intent intent = new Intent(this, MyCollections.class);
		startActivity(intent);
	}
	
	public void gotoSettings(View v){
		Intent intent = new Intent(this, LivePaperSettings.class);
		startActivity(intent);
	}
	
	/*private boolean updateDatabase(){
		boolean updatedFlag = false;
		for(Collection collection : collections){
			int result = dbAdapter.updateCollection(collection);
			switch(result){
			case 0: dbAdapter.createCollection(collection); // created new collections
				break;
			case 1: updatedFlag = true; // updated collections
				break;
			default: Log.w(TAG, "Found multiple results for one collection ID");
				break;
			}
			
			for(Photoset photoset : collection.getPhotosets()){
				if(dbAdapter.updatePhotoset(photoset) == 0)
					dbAdapter.createPhotoset(photoset);
			}
		}
		return updatedFlag;
	}*/
	
	/*private class CheckForUpdatesAsync extends AsyncTask<Object, Object, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			IFlickrService service = new FlickrService();
			return service.execute(PostMethod.GET_COLLECTION_LIST);
		}
		
		@Override
		protected void onPostExecute(JSONObject response) {
			try {
				JSONArray responseArray = response.getJSONObject("collections").getJSONArray("collection");
				collections = new ArrayList<Collection>();
				for(int i = 0; i < responseArray.length(); i++){
					List<Photoset> collectionSets = new ArrayList<Photoset>(); 
					String collectionId = responseArray.getJSONObject(i).getString("id");
					String collectionTitle = responseArray.getJSONObject(i).getString("title");
					String collectionDescription = responseArray.getJSONObject(i).getString("description");
					
					if(responseArray.getJSONObject(i).optJSONArray("set") == null){
						continue;
					}
					
					JSONArray responseSets = responseArray.getJSONObject(i).getJSONArray("set");
					
					for(int j = 0; j < responseSets.length(); j++){
						String setId = responseSets.getJSONObject(j).getString("id");
						String setTitle = responseSets.getJSONObject(j).getString("title");
						collectionSets.add(new Photoset(setId,collectionId,setTitle));
					}
					
					collections.add(new Collection(collectionId, collectionTitle, collectionDescription, collectionSets, ".99", true, true, false));
				}
				
				if(!updateDatabase()){
					Toast.makeText(getApplicationContext(), getString(R.string.new_collections), Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(), getString(R.string.error_unexpected_response), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			catch(Exception e){
				Toast.makeText(getApplicationContext(), getString(R.string.error_checking_for_updates), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			finally{
				dialog.dismiss();
			}
		}
	}*/
	
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		init();
	}
	
	protected void onPause(){
		super.onPause();
		Log.d(TAG, "onPause()");
		//checkForUpdates.cancel(true);
		try{
			dbAdapter.close();
		}
		catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
	}
	
}
