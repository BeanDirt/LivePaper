package com.beandirt.livepaper.dashboard.model;

import java.util.List;

public class Collection {

	private final String id;
	private final String name;
	private final List<Photoset> photosets;
	
	public Collection(String id, String name, List<Photoset> photosets){
		this.id = id;
		this.name = name;
		this.photosets = photosets;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public List<Photoset> getPhotosets() {
		return photosets;
	}
}