package com.beandirt.livepaper.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beandirt.livepaper.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beandirt.livepaper.dashboard.flickr.FlickrWebService;
import com.beandirt.livepaper.dashboard.flickr.FlickrWebService.PostMethod;
import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.dashboard.model.Photoset;

public class MyCollections extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "Collections"; 
	
	List<Collection> collections;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosets);
		
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
						
						//collections.add(new Collection(collectionId, collectionName, collectionSets));
					}
					
					populateList();
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,Downloader.class);
		intent.putExtra("id", collections.get(position).getId());
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	private void populateList(){
		setListAdapter(new ArrayAdapter<Collection>(this, R.layout.list_photoset_item, collections){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				View v = convertView;
				if(v == null){
					LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.list_collection_item, null);
				}
				
				Collection collection = collections.get(position);
				TextView label = (TextView) v.findViewById(R.id.collection_name);
				label.setText(collection.getTitle());
				return v;
			}
		});
	}
}
