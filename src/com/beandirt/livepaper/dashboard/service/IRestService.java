package com.beandirt.livepaper.dashboard.service;

import java.util.List;

import com.beandirt.livepaper.dashboard.model.Collection;

public interface IRestService {
	
	/**
	 * /collections
	 * @return  A list of enabled collections
	 */
	public List<Collection> enabledCollections();
	
	/**
	 * /purchasedCollections/{userEmail}
	 * @param userEmail The current user's google account email
	 * @return A list of collections purchased by the user email provided
	 */
	public List<Collection> purchasedCollections(String userEmail);
	
	/**
	 * /collections/{collectionId}/purchase/{userEmail}
	 * @param cid The collection id to purchase
	 * @param userEmail The email of the current user
	 * @return True or false depending on whether the user already owned that collection
	 */
	public Boolean purchaseCollection(String cid, String userEmail);
	
	/**
	 * Creates a user
	 * 
	 * @param firstName The user's first name
	 * @param lastName The user's last name
	 * @param email The user's email
	 * @return True or false depending on whether the user existed already
	 */
	public Boolean createUser(String firstName, String lastName, String email);
}
