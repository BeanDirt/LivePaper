package com.beandirt.livepaper.wallpaper;

import com.beandirt.livepaper.R;
import com.beandirt.livepaper.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LivePaperSettings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}