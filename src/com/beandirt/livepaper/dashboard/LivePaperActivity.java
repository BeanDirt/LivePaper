package com.beandirt.livepaper.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.CollectionsDbAdapter;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService.PostMethod;
import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.dashboard.model.Photoset;

public class LivePaperActivity extends Activity {
	
	private static final String TAG = "LivePaperActivity";
	
	private List<Collection> collections;
	private ProgressDialog dialog;
	
	protected CollectionsDbAdapter collectionsAdapter;
	
	protected void checkForUpdates(){
    	dialog = ProgressDialog.show(this, "", 
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
						String collectionTitle = responseArray.getJSONObject(i).getString("title");
						String collectionDescription = responseArray.getJSONObject(i).getString("description");
						
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
					dialog.hide();
				}
    		}
    	};
    	getCollections.execute();
    }
	
	private boolean updateDatabase(){
		boolean updatedFlag = false;
		for(Collection collection : collections){
			int result = collectionsAdapter.updateCollection(collection);
			switch(result){
			case 0: collectionsAdapter.createCollection(collection); // created new collections
				break;
			case 1: updatedFlag = true; // updated collections
				break;
			default: Log.w(TAG, "Found multiple results for one collection ID");
				break;
			}
		}
		return updatedFlag;
	}
}
