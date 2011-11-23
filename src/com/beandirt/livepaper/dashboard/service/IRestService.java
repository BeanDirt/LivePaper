package com.beandirt.livepaper.dashboard.service;

import java.util.List;

import com.beandirt.livepaper.dashboard.model.Collection;

public interface IRestService {
	public List<Collection> enabledCollections();
	public List<Collection> purchasedCollections(String userEmail);
}
