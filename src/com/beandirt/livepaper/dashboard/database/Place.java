package com.beandirt.livepaper.dashboard.database;

public class Place {

	private long id;
	private final String name;
	private final String location_short;
	private final String location_long;
	private final double latitude;
	private final double longitude;
	private final Boolean enabled;
	
	public Place(String name, String location_short, String location_long, Double latitude, Double longitude, Boolean enabled){
		this.name = name;
		this.location_short = location_short;
		this.location_long = location_long;
		this.latitude = latitude;
		this.longitude = longitude;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public String getLocation_short() {
		return location_short;
	}

	public String getLocation_long() {
		return location_long;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
