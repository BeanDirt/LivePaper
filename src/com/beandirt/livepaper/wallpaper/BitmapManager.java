package com.beandirt.livepaper.wallpaper;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beandirt.livepaper.database.LivePaperDbAdapter;

public class BitmapManager extends BitmapFactory implements OnSharedPreferenceChangeListener{
	private Bitmap bitmap;
	private final Resources resources;
	private final TimeCalculator timeCalculator;
	private final Context context;
	
	private int counter;
	private File[] day_resources;
	private File[] night_resources;
	
	private SharedPreferences sp;
	private Boolean settingsChanged;
	private BitmapChangedListener bitmapChangedListener;	
	
	private static final String TAG = "BitmapManager";
	
	public BitmapManager(TimeCalculator timeCalculator, Resources resources, Context context){
		this.resources = resources;
		this.timeCalculator = timeCalculator;
		this.context = context;
		populateBitmaps(context);
		
		
	}
	
	private void populateBitmaps(Context context){
		Log.d(TAG, "POPULATING BITMAPS");
		
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.registerOnSharedPreferenceChangeListener(this);
		
		String collectionId = sp.getString("collectionId", null);
		Log.d(TAG, "collection id: " + collectionId);
		LivePaperDbAdapter dbAdapter = LivePaperDbAdapter.getInstanceOf(context);
		Cursor cursor = dbAdapter.fetchActivePhotoset(collectionId);
		cursor.moveToFirst();
		Log.d(TAG, ""+cursor.getColumnCount());
		Log.d(TAG, ""+cursor.getCount());
		Log.d(TAG, ""+cursor.getPosition());
		String photosetId = cursor.getString(0);
		
		Log.d(TAG, photosetId+"/"+collectionId);
		
		day_resources = new File[14];
		night_resources = new File[10];
		
		File rootDir = context.getDir(photosetId, Activity.MODE_PRIVATE);
		if(rootDir.isDirectory()){
			String[] totalFiles = rootDir.list();
			Log.d(TAG, "totalFiles Length: "+totalFiles.length);
			for(int i = 0; i < totalFiles.length; i++){
				//int indexOfCollection = totalFiles[i].indexOf(collectionId) + 1;
				int indexOfDot = totalFiles[i].indexOf(".");
				int fileIndex = Integer.valueOf(totalFiles[i].substring(0, indexOfDot));
				Log.d(TAG, ""+fileIndex);
				File file = new File(rootDir.toString() + "/" + totalFiles[i]);
				
				if(fileIndex > 13)
					night_resources[fileIndex - 14] = file;
				else
					day_resources[fileIndex] = file;
			}
		}
	}
	
	public int getDayImageCount(){
		return day_resources.length;
	}
	
	public int getNightImageCount(){
		return night_resources.length;
	}
	
	private float getCurrentInterval(){
		if(timeCalculator.isDay()) {
			Log.d(TAG,"using day interval");
			return getDayInterval();
		}
		else{
			Log.d(TAG,"using night interval");
			return getNightInterval();
		}
		
	}
	
	public float getDayInterval(){
		float span = this.timeCalculator.getSpan(true);
		return span / day_resources.length;
	}
	
	public float getNightInterval(){
		float span = this.timeCalculator.getSpan(false);
		return span / night_resources.length;
	}
	
	public Bitmap getBitmap(){
		int tempCounter = (int) Math.floor(timeCalculator.timePassedSinceSolarEvent() / getCurrentInterval());
		File[] image_resources = timeCalculator.isDay() ? day_resources : night_resources;
		
		Log.d(TAG, "There are: "+image_resources.length + " photos for this time period.");
		
		Log.d(TAG,String.valueOf(timeCalculator.timePassedSinceSolarEvent()));
		Log.d(TAG,String.valueOf(getCurrentInterval()));
		
		Log.d(TAG,String.valueOf(tempCounter));
		Log.d(TAG,String.valueOf(counter));
		
		if(tempCounter != counter || bitmap == null || settingsChanged){
			counter = tempCounter;
			settingsChanged = false;
			Log.d(TAG, image_resources[counter].toString());
			File file = image_resources[counter];
			Log.d(TAG, file.getPath());
			bitmap = BitmapManager.decodeFile(file.getPath());
			//bitmap = BitmapManager.decodeResource(this.resources, R.drawable.sf_bg);
		}
		
		return bitmap;
	}
	
	public int getBitmap(float timePassedSinceSolarEvent){
		float currentTestTime = timePassedSinceSolarEvent + timeCalculator.getDawn();
		boolean isDay = (currentTestTime >= timeCalculator.getDawn() && currentTestTime < timeCalculator.getDusk());
		float currentInterval = (isDay) ? getDayInterval() : getNightInterval();

		timePassedSinceSolarEvent = (isDay) ? timePassedSinceSolarEvent : currentTestTime - timeCalculator.getDusk();
		Log.d("Testing","Current Interval: " + TimeCalculator.getPrettyTime( currentInterval ) + " (" + String.valueOf(currentInterval) + ")");
		Log.d("Testing","Time Passed Since Solar Event: " + TimeCalculator.getPrettyTime( timePassedSinceSolarEvent ) + " (" + String.valueOf(timePassedSinceSolarEvent) + ")");
		int tempInterval = (int) Math.floor(timePassedSinceSolarEvent / currentInterval);
		return tempInterval;
	}
	
	public File[] getDayResources(){
		return day_resources;
	}
	
	public File[] getNightResources(){
		return night_resources;
	}
	
	public int getCounter(){
		return counter + 1;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		settingsChanged = true;
		populateBitmaps(this.context);
		bitmapChangedListener.onBitmapChanged();
	}
	
	public void setBitmapChangedListener(BitmapChangedListener listener){
		this.bitmapChangedListener = listener;
	}
}
