package com.beandirt.livepaper.dashboard;

import com.beandirt.livepaper.R;

import android.app.Activity;
import android.os.Bundle;

public class Downloader extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "Downloader";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloader); 

        String collectionId = getIntent().getExtras().getString("id");
    }
}