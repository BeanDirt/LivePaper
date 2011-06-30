package com.beandirt.livepaper.wallpaper;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.Time;
import android.util.Log;

public class TimeCalculator {

	private float dawn;
	private float dusk;
	
	public static int SECONDS_IN_DAY = 86400;
	private final TimeZone TIMEZONE;
	
	public TimeCalculator(){
		this.dawn = 0.3f;
		this.dusk = 0.75f;
		
		String tz = Time.getCurrentTimezone();
    	TIMEZONE = TimeZone.getTimeZone(tz);
	}
	
	public void setDawn(float dawn){
		this.dawn = dawn; 
	}
	
	public void setDusk(float dusk){
		this.dusk = dusk;
	}
	
	public float getDawn(){
		return this.dawn * SECONDS_IN_DAY;
	}
	
	public float getDusk(){
		return this.dusk * SECONDS_IN_DAY;
	}
	
	public float getSpan(Boolean day){
		float dayInterval = this.getDusk() - this.getDawn();
		if(day) return dayInterval;
		else return SECONDS_IN_DAY - dayInterval;
	}
	
	public static String getPrettyTime(float secondsToCalculate){
		double hoursFraction = (secondsToCalculate / 60) / 60;
		int hours = (int) Math.floor(hoursFraction);
		String stringHours = String.valueOf(hours);
		if(stringHours.length() == 1) stringHours = "0" + stringHours;

		double minutesFraction = (hoursFraction - hours) * 60;		
		int minutes = (int) Math.floor(minutesFraction);
		String stringMinutes = String.valueOf(minutes);
		if(stringMinutes.length() == 1) stringMinutes = "0" + stringMinutes;
		
		double secondsFraction = (minutesFraction - minutes) * 60;
		int seconds = (int) Math.floor(secondsFraction);
		String stringSeconds = String.valueOf(seconds);
		if(stringSeconds.length() == 1) stringSeconds = "0" + stringSeconds;
		
		return stringHours + ":" + stringMinutes + ":" + stringSeconds;	
		
	}
	
	public float timePassedSinceSolarEvent(){
		float seconds;
		float currentSeconds = getCurrentSeconds();
		
		if(isDay()){
			seconds = currentSeconds - getDawn();
		}
		else {
			if(currentSeconds < getDawn()) {
				seconds = currentSeconds + (SECONDS_IN_DAY - getDusk());
			}
			else{
				seconds = currentSeconds - getDusk();
			}
		}
				
		
		return seconds;
	}
	
	private int getCurrentSeconds(){
		Calendar now = Calendar.getInstance();

		int currentSeconds = now.get(Calendar.SECOND);
		currentSeconds += now.get(Calendar.HOUR_OF_DAY) * 60 * 60;
		currentSeconds += now.get(Calendar.MINUTE) * 60;
		
		return currentSeconds;
	}
	
	public boolean isDay(){
		int seconds = getCurrentSeconds();
		if(seconds > getDusk() || seconds < getDawn()) return false;
		else return true;
	}
	
	public Boolean isNight(){
		return !isDay();
	}
	
	public static float getTotalSeconds(Calendar cal){
		float seconds = (float) (cal.get(Calendar.HOUR_OF_DAY) * 60.0 * 60.0);
		seconds += cal.get(Calendar.MINUTE) * 60.0;
		seconds += cal.get(Calendar.SECOND);
		return seconds;
	}
	
	public static float timeToDayFraction(Date time, TimeZone timeZone){
		
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(time);
		
		float seconds = getTotalSeconds(cal);
		return seconds / SECONDS_IN_DAY;
	}
	
	public static float timeToDayFraction(int seconds){
		return seconds / SECONDS_IN_DAY;
	}
}
