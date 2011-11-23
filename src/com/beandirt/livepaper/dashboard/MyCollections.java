package com.beandirt.livepaper.dashboard;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.model.Collection;
import com.beandirt.livepaper.dashboard.service.IRestService;
import com.beandirt.livepaper.dashboard.service.RestService;
import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class MyCollections extends ListActivity {

	private static final String TAG = "MyCollections"; 
	
	private List<Collection> collections;
	protected LivePaperDbAdapter dbAdapter;
	protected Cursor cursor;

	private PurchasedCollectionsAsync purchasedCollectionsAsync;

	private CollectionAdapter collectionAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collections);
	}

	protected void getPurchasedCollections(){
		//Display display = getWindowManager().getDefaultDisplay(); 
		//String width = String.valueOf(display.getWidth());
		//String height = String.valueOf(display.getHeight());
		
		purchasedCollectionsAsync = new PurchasedCollectionsAsync();
		purchasedCollectionsAsync.execute("citizen@tedconn.com");
		
		/*cursor = dbAdapter.fetchPurchasedCollections(true, width, height);
		startManagingCursor(cursor);
		setListAdapter(new SimpleCursorAdapter(getApplicationContext(), R.layout.list_collection_item, cursor, new String[] {FIELD_TITLE}, new int[] {R.id.collection_title}));*/
	}
	
	protected void populateList(){
		collectionAdapter = new CollectionAdapter(getApplicationContext(),R.layout.list_collection_item,collections);
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,CollectionDetail.class);
		Collection collection = collectionAdapter.getItem(position);
		intent.putExtra("cid", collection.getId());
		intent.putExtra("collection_title", collection.getTitle());
		intent.putExtra("collection_description", collection.getDescription());
		startActivity(intent);
	}
	
	private class PurchasedCollectionsAsync extends AsyncTask<String, Object, List<Collection>>{
		
		@Override
		protected List<Collection> doInBackground(String... params){
			IRestService service = new RestService();
			return service.purchasedCollections(params[0]);
		}
		
		@Override
		protected void onPostExecute(List<Collection> c){
			collections = c;
			populateList();
		}
	}
	
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "onResume()");
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		getPurchasedCollections();
	}
}
