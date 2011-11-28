package com.beandirt.livepaper.dashboard.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beandirt.livepaper.dashboard.model.Collection;

public class RestService implements IRestService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Collection> enabledCollections() {
		String url = "http://10.0.2.2:9001/collections";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			HttpResponse response = client.execute(post);
			String entityString = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(entityString);
			List<Collection> collections = new ArrayList<Collection>();
			for(int i=0; i< jsonArray.length(); i++){
				JSONObject jsonCollection = jsonArray.getJSONObject(i);
				
				String cid = jsonCollection.getString("cid");
				String title = jsonCollection.getString("title");
				String description = jsonCollection.getString("description");
				Boolean trial = jsonCollection.getBoolean("trial");
				Boolean enabled = jsonCollection.getBoolean("enabled");
				String price = jsonCollection.getString("price");
				
				Collection c = new Collection(cid,title,description,price,trial,enabled,true);
				collections.add(c);
			}
			
			return collections;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Collection> purchasedCollections(String userEmail) {
		String url = "http://10.0.2.2:9001/purchasedCollections/" + userEmail;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			HttpResponse response = client.execute(post);
			String entityString = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(entityString);
			List<Collection> collections = new ArrayList<Collection>();
			for(int i=0; i< jsonArray.length(); i++){
				String cid = jsonArray.getJSONObject(i).getString("cid");
				String title = jsonArray.getJSONObject(i).getString("title");
				String description = jsonArray.getJSONObject(i).getString("description");
				Boolean trial = jsonArray.getJSONObject(i).getBoolean("trial");
				Boolean enabled = jsonArray.getJSONObject(i).getBoolean("enabled");
				String price = jsonArray.getJSONObject(i).getString("price");
				Collection c = new Collection(cid,title,description,price,trial,enabled,true);
				collections.add(c);
			}
			return collections;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean purchaseCollection(String cid, String userEmail) {
		String url = "http://10.0.2.2:9001/collections/" + cid + "/purchase/" + userEmail;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
		HttpResponse response;
		try {
			response = client.execute(post);
			String entityString = EntityUtils.toString(response.getEntity());
			return (entityString == "success");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean createUser(String firstName, String lastName, String email) {
		String url = "http://10.0.2.2:9001/users/create/";
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("user.firstName", firstName));
		postParameters.add(new BasicNameValuePair("user.lastName", lastName));
		postParameters.add(new BasicNameValuePair("user.email", email));
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		HttpResponse response;
		
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			response = client.execute(post);
			String entityString = EntityUtils.toString(response.getEntity());
			System.out.println(entityString);
			return (entityString == "success");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
