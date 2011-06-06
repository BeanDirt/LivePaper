package com.beandirt.livepaper.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewFlipper;

import com.beandirt.livepaper.R;

public class NewPlaces extends Activity implements View.OnClickListener{
	
	ViewFlipper vf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_places);
		
	}

	@Override
	public void onClick(View v) {
	
	}
}
