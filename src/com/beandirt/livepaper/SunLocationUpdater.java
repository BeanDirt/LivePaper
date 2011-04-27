package com.beandirt.livepaper;

import java.util.TimeZone;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.util.GeoLocation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;

public class SunLocationUpdater {
	private static final int LOCATION_UPDATE_MIN_TIME = 30 * 60 * 1000; // 30 mins.
	private static final int LOCATION_UPDATE_MIN_DISTANCE = 150 * 1000; // 150km
	private final LocationUpdatedListener locationUpdatedListener;
	
	private float dawn;
	private float dusk;
	private double longitude;
	private double latitude;
	
	private LocationManager mLocationManager;

	public SunLocationUpdater(Context context, LocationUpdatedListener locationUpdatedListener){
		
		this.locationUpdatedListener = locationUpdatedListener;
		LocationListener locationListener = new SunLocationListener();
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
	}
	
	private final class SunLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			updateLocation();
			locationUpdatedListener.onLocationChanged(dawn, dusk);		
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void updateLocation(){
		Location location =  mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		updateLocation(location);
	}
	
	private void updateLocation(Location location){
		try{
			String tz = Time.getCurrentTimezone();
        	TimeZone timeZone = TimeZone.getTimeZone(tz);
			GeoLocation zLocation = new GeoLocation("MyLocation", location.getLatitude(), location.getLongitude(), location.getAltitude(), timeZone);
			ZmanimCalendar zcal = new ComplexZmanimCalendar(zLocation);

			longitude = location.getLongitude();
			latitude = location.getLatitude();
			
			dawn = TimeCalculator.timeToDayFraction(zcal.getSunrise(), timeZone);
			dusk = TimeCalculator.timeToDayFraction(zcal.getSunset(), timeZone);
		}
		catch(Exception e){
			dawn = 0.3f;
			dusk = 0.75f;
		}
	}
	
	public float getDawn(){
		return dawn;
	}
	
	public float getDusk(){
		return dusk;
	}
	
	public double longitude(){
		return longitude;
	}
	
	public double latitude(){
		return latitude;
	}
}
