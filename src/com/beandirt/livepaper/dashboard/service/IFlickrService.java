package com.beandirt.livepaper.dashboard.service;

import org.json.JSONObject;

import com.beandirt.livepaper.dashboard.service.FlickrService.PostMethod;

public interface IFlickrService {

	/**
	 * 
	 * @param method
	 * @return A JSON Object representing the result of the service call
	 */
	public JSONObject execute(PostMethod method);
	
	/**
	 * 
	 * @return A JSON Object representing a list of photosets
	 */
	public JSONObject getPhotosetList();
	
	/**
	 * 
	 * @return A JSON Object representing a list of collections
	 */
	public JSONObject getCollectionList();
}
