package com.beandirt.livepaper.dashboard;

import com.beandirt.livepaper.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LivePaperDashboard extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
	public void gotoMyPlaces(View v){
		Intent intent = new Intent(v.getContext(), MyPlaces.class);
		startActivityForResult(intent, 0);
	}
	
	public void gotoNewPlaces(View v){
		Intent intent = new Intent(v.getContext(), NewPlaces.class);
		startActivityForResult(intent, 0);
	}
	
	public void gotoSettings(View v){
		
	}
}
