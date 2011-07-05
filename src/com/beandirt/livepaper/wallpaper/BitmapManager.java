package com.beandirt.livepaper.wallpaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.beandirt.livepaper.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

public class BitmapManager extends BitmapFactory{
	private Bitmap bitmap;
	private final Resources resources;
	private final TimeCalculator timeCalculator;
	
	private int counter;
	private File[] day_resources;
	private File[] night_resources;
	
	private static final String TAG = "BitmapManager";
	
	public BitmapManager(TimeCalculator timeCalculator, Resources resources, Context context){
		this.resources = resources;
		this.timeCalculator = timeCalculator;
		
		populateBitmaps(context);
	}
	
	private void populateBitmaps(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String collectionId = sp.getString("collectionId", null);
		String photosetId = sp.getString("photosetId", null);
		
		day_resources = new File[14];
		night_resources = new File[10];
		
		File rootDir = context.getDir(photosetId, Activity.MODE_PRIVATE);
		if(rootDir.isDirectory()){
			String[] totalFiles = rootDir.list();
			for(int i = 0; i < totalFiles.length; i++){
				int indexOfCollection = totalFiles[i].indexOf(collectionId) + 1;
				int indexOfDot = totalFiles[i].indexOf(".");
				int fileIndex = Integer.valueOf(totalFiles[i].substring(indexOfCollection, indexOfDot));
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
		
		Log.d(TAG,String.valueOf(timeCalculator.timePassedSinceSolarEvent()));
		Log.d(TAG,String.valueOf(getCurrentInterval()));
		
		Log.d(TAG,String.valueOf(tempCounter));
		Log.d(TAG,String.valueOf(counter));
		
		if(tempCounter != counter || bitmap == null){
			counter = tempCounter;
			File file = image_resources[counter];
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
}
