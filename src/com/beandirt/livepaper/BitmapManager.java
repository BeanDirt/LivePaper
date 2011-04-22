package com.beandirt.livepaper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapManager extends BitmapFactory{
	private Bitmap bitmap;
	private final Resources resources;
	private final TimeCalculator timeCalculator;
	
	private int counter;
	private int[] night_resources = {R.drawable.night1,
									R.drawable.night2,
									R.drawable.night3,
									R.drawable.night4,
									R.drawable.night5,
									R.drawable.night6,
									R.drawable.night7,
									R.drawable.night8,
									R.drawable.night9,
									R.drawable.night10};
	
	private int[] day_resources = {R.drawable.day1,
									R.drawable.day2,
									R.drawable.day3,
									R.drawable.day4,
									R.drawable.day5,
									R.drawable.day6,
									R.drawable.day7,
									R.drawable.day8,
									R.drawable.day9,
									R.drawable.day10,
									R.drawable.day11,
									R.drawable.day12,
									R.drawable.day13,
									R.drawable.day14};
	
	public BitmapManager(TimeCalculator timeCalculator, Resources resources){
		this.resources = resources;
		this.timeCalculator = timeCalculator;
	}
	
	public int getDayImageCount(){
		return day_resources.length;
	}
	
	public int getNightImageCount(){
		return night_resources.length;
	}
	
	private float getCurrentInterval(){
		if(timeCalculator.isDay()) return getDayInterval();
		else return getNightInterval();
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
		int[] image_resources = timeCalculator.isDay() ? day_resources : night_resources;
		
		Log.d("LivePaper",String.valueOf(tempCounter));
		Log.d("LivePaper",String.valueOf(counter));
		
		if(tempCounter != counter){
			counter = tempCounter;
			bitmap = BitmapManager.decodeResource(this.resources, image_resources[counter]);
		}
		
		return bitmap;
	}
	
	public int getBitmap(int timePassedSinceSolarEvent){
		int tempInterval = (int) Math.floor(getCurrentInterval() / timePassedSinceSolarEvent);
		return tempInterval;
	}
	
	public int[] getDayResources(){
		return day_resources;
	}
	
	public int[] getNightResources(){
		return night_resources;
	}
}
