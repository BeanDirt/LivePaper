package com.beandirt.livepaper.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.dashboard.model.Photoset;
import com.beandirt.livepaper.dashboard.service.FlickrService;
import com.beandirt.livepaper.dashboard.service.IFlickrService;
import com.beandirt.livepaper.dashboard.service.IRestService;
import com.beandirt.livepaper.dashboard.service.RestService;
import com.beandirt.livepaper.dashboard.service.FlickrService.PostMethod;

public class NewCollections extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "NewCollections"; 
	
	private List<Collection> allCollections;
	private List<Collection> filteredCollections;
	private List<Photoset> photosets;
	private EnabledCollectionsAsync enabledCollectionsAsync;
	private PhotosetsListAsync photosetsAsync;
	private CollectionAdapter collectionAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_collections);
	}
	
	protected void getEnabledCollections(){
		enabledCollectionsAsync = new EnabledCollectionsAsync();
		enabledCollectionsAsync.execute();
	}
	
	protected void retrievePhotosets(){
		photosetsAsync = new PhotosetsListAsync();
		photosetsAsync.execute();
	}
	
	protected void filterByDisplayDimensions(List<Collection> collections, List<Photoset> photosets){
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		filteredCollections = new ArrayList<Collection>();
		
		for(Collection c : collections){
			for(Photoset ps : photosets){
				if(ps.getCollection().equals(c.getId()) && width == ps.getWidth() && height == ps.getHeight()){
					filteredCollections.add(c);
				}
			}
		}
		
		populateList();
	}
	
	protected void populateList(){
		collectionAdapter = new CollectionAdapter(getApplicationContext(),R.layout.list_collection_item,filteredCollections);
		setListAdapter(collectionAdapter);
	}
	
	private class CollectionAdapter extends ArrayAdapter<Collection>{

		private List<Collection> items;
		
		public CollectionAdapter(Context context, int textViewResourceId, List<Collection> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_collection_item, null);
            }
            Collection c = items.get(position);
            TextView title = (TextView) v.findViewById(R.id.collection_title);
            title.setText(c.getTitle());
            
            return v;
		}
	}
	
	private class EnabledCollectionsAsync extends AsyncTask<Object, Object, List<Collection>>{

		@Override
		protected List<Collection> doInBackground(Object... params) {
			IRestService service = new RestService();
			return service.enabledCollections();
		}
		
		@Override
		protected void onPostExecute(List<Collection> c){
			allCollections = c;
			retrievePhotosets();
		}
	}
	
	private class PhotosetsListAsync extends AsyncTask<Object, Object, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			IFlickrService service = new FlickrService();
			return service.execute(PostMethod.GET_COLLECTION_LIST);
		}
		
		@Override
		protected void onPostExecute(JSONObject response) {
			try {
				JSONArray responseArray = response.getJSONObject("collections").getJSONArray("collection");
				List<Photoset> ps = new ArrayList<Photoset>(); 
				for(int i = 0; i < responseArray.length(); i++){
					String collectionId = responseArray.getJSONObject(i).getString("id");
					if(responseArray.getJSONObject(i).optJSONArray("set") == null){
						continue;
					}
					
					JSONArray responseSets = responseArray.getJSONObject(i).getJSONArray("set");
					
					for(int j = 0; j < responseSets.length(); j++){
						String setId = responseSets.getJSONObject(j).getString("id");
						String setTitle = responseSets.getJSONObject(j).getString("title");
						ps.add(new Photoset(setId,collectionId,setTitle));
					}
				}
				photosets = ps;
				filterByDisplayDimensions(allCollections,photosets);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,CollectionDetail.class);
		Collection collection = collectionAdapter.getItem(position);
		intent.putExtra("cid", collection.getId());
		intent.putExtra("collection_title", collection.getTitle());
		intent.putExtra("collection_description", collection.getDescription());
		startActivity(intent);
	}
	
	protected void onResume(){
		super.onResume();
		getEnabledCollections();
	}
}
