package com.beandirt.livepaper.dashboard.model;

public class Photoset {

	private long rowId;
	private boolean active;

	private final String id;
	private final String title;
	private final int width;
	private final int height;
	private final String collection;
	
	public Photoset(String id, String collection, String title){
		this.id = id;
		this.title = title;
		this.collection = collection;
		String[] dimsArray = title.split("x");
		this.width = Integer.valueOf(dimsArray[0]);
		this.height = Integer.valueOf(dimsArray[1]);
	}

	public String getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean getActive(){
		return this.active;
	}
	
	public String getCollection(){
		return this.collection;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}
	
	public long getRowId() {
		return this.rowId;
	}
}
