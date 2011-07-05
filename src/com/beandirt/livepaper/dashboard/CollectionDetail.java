package com.beandirt.livepaper.dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;
public class CollectionDetail extends LivePaperActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "CollectionDetail";
	
	private long collectionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_detail);
		
		collectionId = getIntent().getExtras().getLong("rowid");
		
        setFonts();
	}
	
	private void setFonts(){
		Typeface myriad_pro = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
		TextView title = (TextView) findViewById(R.id.placeTitle);
		title.setTypeface(myriad_pro);
	}
	
	public void goToDownloader(View v){
		Intent intent = new Intent(this, Downloader.class);
		intent.putExtra("rowid", collectionId);
		startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	protected void onResume(){
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		super.onResume();
	}
}
