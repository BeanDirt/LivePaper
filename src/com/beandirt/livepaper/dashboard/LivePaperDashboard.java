package com.beandirt.livepaper.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.dashboard.database.LivePaperDbAdapter;

public class LivePaperDashboard extends LivePaperActivity {
	
	private static final String TAG = "LivePaperDashboard";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
	@Override
	protected void onResume() {
		dbAdapter = LivePaperDbAdapter.getInstanceOf(getApplicationContext());
		checkForUpdates();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		dbAdapter.close();
		super.onDestroy();
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
