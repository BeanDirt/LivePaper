package com.beandirt.livepaper.dashboard.model;

public class Collection {
	private long rowId;

	private final String id;
	private final String title;
	private final String description;
	private final String price;
	private final Boolean trial;
	private final Boolean enabled;
	private final Boolean purchased;
	
	public Collection(String id, String title, String description, String price, Boolean trial, Boolean enabled, Boolean purchased){
		this.id = id;
		this.title = title;
		this.description = description;
		this.price = price;
		this.trial = trial;
		this.enabled = enabled;
		this.purchased = purchased;
	}

	public String getId() {
		return id;
	}
	
	public void setRowId(long rowId){
		this.rowId = rowId;
	}
	
	public long getRowId(){
		return this.rowId;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getPrice() {
		return this.price;
	}

	public Boolean getTrial() {
		return this.trial;
	}

	public Boolean getEnabled() {
		return this.enabled;
	}
	
	public Boolean getPurchased() {
		return this.purchased;
	}
}