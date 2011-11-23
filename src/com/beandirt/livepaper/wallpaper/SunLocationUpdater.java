package com.beandirt.livepaper.wallpaper;

import java.util.TimeZone;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.util.GeoLocation;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

public class SunLocationUpdater {
	private static final int LOCATION_UPDATE_MIN_TIME = 30 * 60 * 1000; // 30 mins.
	private static final int LOCATION_UPDATE_MIN_DISTANCE = 150 * 1000; // 150km
	
	private static final int TWO_MINUTES = 1000 * 60 * 2; // 2 mins.
	
	private static final String TAG = "SunLocationUpdater";
	
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
		
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		
		mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(criteria, true), 
					LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
	}
	
	private final class SunLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			//Location bestLocation = determineMostAccurateLocation(location);
			//updateLocation(bestLocation);
			updateLocation(location);
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
	
	private Location determineMostAccurateLocation(Location location){
		if(isBetterLocation(location,mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))){
			if(isBetterLocation(location,mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))){
				Log.d(TAG, "New location is best");
				return location;
			}
			else{
				Log.d(TAG, "Old GPS has more accurate info");
				return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
		}
		else{
			Log.d(TAG, "Old Network has more accurate info");
			return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
	}
	
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
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
