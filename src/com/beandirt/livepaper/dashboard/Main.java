package com.beandirt.livepaper.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beandirt.livepaperdownloader.FlickrWebService.PostMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class Main extends Activity {
	
	private static final String TAG = "Main";

	List<Collection> collections;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private void checkForUpdates(){
    	final ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Checking for updates...", true);
        dialog.show();
        
        AsyncTask<Object, Object, JSONObject> getCollections = new AsyncTask<Object, Object, JSONObject>() {

    		@Override
    		protected JSONObject doInBackground(Object... params) {
    			FlickrWebService service = new FlickrWebService();
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
						String collectionName = responseArray.getJSONObject(i).getString("title");
						
						if(responseArray.getJSONObject(i).optJSONArray("set") == null){
							continue;
						}
						
						JSONArray responseSets = responseArray.getJSONObject(i).getJSONArray("set");
						
						for(int j = 0; j < responseSets.length(); j++){
							String setId = responseSets.getJSONObject(j).getString("id");
							String setName = responseSets.getJSONObject(j).getString("title");
							String setDescription = responseSets.getJSONObject(j).getString("description"); 
							collectionSets.add(new Photoset(setId,setName,setDescription));
						}
						
						collections.add(new Collection(collectionId, collectionName, collectionSets));
						dialog.hide();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				catch(Exception e){
					e.printStackTrace();
				}
    		}
    	};
    	getCollections.execute();
    }
    
	@Override
	protected void onResume() {
		checkForUpdates();
		super.onResume();
	}
	
	public void gotoNewCollections(View v){
		Intent intent = new Intent(this, NewCollections.class);
		startActivity(intent);
	}
	
	public void gotoMyCollections(View v){
		Intent intent = new Intent(this, MyCollections.class);
		startActivity(intent);
	}
}