package com.beandirt.livepaper.dashboard.flickr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FlickrWebService {

	private static final String TAG = "FlickrWebService";
	private static final String URL = "http://api.flickr.com/services/rest/";
	private static final String KEY = "8f83b57f890d9f2e358d8a111a9b809f";
	private static final String SECRET = "ea4b47fa2b62c7cf";
	private static final String AUTH_URL = "http://flickr.com/services/auth/";
	private static final String TOKEN = "72157626932472389-34398544315672c1";
	private static String FROB;
	
	DefaultHttpClient httpClient;
	HttpResponse response;
	HttpPost httpPost;
	JSONObject returnValue;
	
	public FlickrWebService(){
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost(URL);
	}
	
	public FlickrWebService(String frob){
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost(URL);
		FROB = frob;
	}
	
	public JSONObject execute(PostMethod method){
		switch(method){
		case GET_FROB: return getFrob();
		case GET_PHOTOSET_LIST: return getPhotosetList();
		case GET_AUTH_TOKEN: return getAuthToken();
		case GET_COLLECTION: return getCollection();
		case GET_COLLECTION_LIST: return getCollectionList();
		}
		return null;
	}
	
	private JSONObject getAuthToken(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("frob", FROB));
		postParameters.add(new BasicNameValuePair("method", "flickr.auth.getToken"));
		
		StringBuffer buffer = new StringBuffer();
        buffer.append(SECRET);
        buffer.append("api_key");
        buffer.append(KEY);
        buffer.append("format");
        buffer.append("json");
        buffer.append("frob");
        buffer.append(FROB);
        buffer.append("method");
        buffer.append("flickr.auth.getToken");
        
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String signature = ByteUtilities.toHexString(md.digest(buffer
                    .toString().getBytes("UTF-8")));
			postParameters.add(new BasicNameValuePair("api_sig", signature));
			UrlEncodedFormEntity formEntity;
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			return new JSONObject(EntityUtils.toString(response.getEntity()).substring(14));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JSONObject();
	}
	
	private JSONObject getFrob(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", "flickr.auth.getFrob"));
		
		StringBuffer buffer = new StringBuffer();
        buffer.append(SECRET);
        buffer.append("api_key");
        buffer.append(KEY);
        buffer.append("format");
        buffer.append("json");
        buffer.append("method");
        buffer.append("flickr.auth.getFrob");
        
        try {
			UrlEncodedFormEntity formEntity;
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String signature = ByteUtilities.toHexString(md.digest(buffer
                    .toString().getBytes("UTF-8")));
			postParameters.add(new BasicNameValuePair("api_sig", signature));
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			UrlEncodedFormEntity formEntity;
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			response = httpClient.execute(httpPost);
			FROB = new JSONObject(EntityUtils.toString(response.getEntity()).substring(14)).getJSONObject("frob").getString("_content");
			
			buffer = new StringBuffer();
	        buffer.append(SECRET);
	        buffer.append("api_key");
	        buffer.append(KEY);
	        buffer.append("frob");
	        buffer.append(FROB);
	        buffer.append("perms");
	        buffer.append("read");
			
	        MessageDigest md = MessageDigest.getInstance("MD5");
			String api_sig = ByteUtilities.toHexString(md.digest(buffer
                    .toString().getBytes("UTF-8")));
	        
	        returnValue = new JSONObject();
			returnValue.put("api_key", KEY);
			returnValue.put("perms","read");
			returnValue.put("frob", FROB);
			returnValue.put("api_sig", api_sig);
			returnValue.put("url", AUTH_URL);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnValue;
		
	}
	
	public JSONObject getPhotosetList(){
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("auth_token", TOKEN));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", "flickr.photosets.getList"));
		//postParameters.add(new BasicNameValuePair("user_id", "64497976@N06"));
		
		StringBuffer buffer = new StringBuffer();
        buffer.append(SECRET);
        buffer.append("api_key");
        buffer.append(KEY);
        buffer.append("auth_token");
        buffer.append(TOKEN);
        buffer.append("format");
        buffer.append("json");
        buffer.append("method");
        buffer.append("flickr.photosets.getList");
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String signature = ByteUtilities.toHexString(md.digest(buffer
                    .toString().getBytes("UTF-8")));
			UrlEncodedFormEntity formEntity;
			postParameters.add(new BasicNameValuePair("api_sig", signature));
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			response = httpClient.execute(httpPost);
			String entityString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, entityString);
			returnValue = new JSONObject(entityString.substring(14));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}
	
	private JSONObject getCollection(){
		return new JSONObject();
	}
	
	public JSONObject getCollectionList(){
		
		String method = "flickr.collections.getTree";
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("auth_token", TOKEN));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", method));
		//postParameters.add(new BasicNameValuePair("user_id", "64497976@N06"));
		
		StringBuffer buffer = new StringBuffer();
        buffer.append(SECRET);
        buffer.append("api_key");
        buffer.append(KEY);
        buffer.append("auth_token");
        buffer.append(TOKEN);
        buffer.append("format");
        buffer.append("json");
        buffer.append("method");
        buffer.append(method);
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String signature = ByteUtilities.toHexString(md.digest(buffer
                    .toString().getBytes("UTF-8")));
			UrlEncodedFormEntity formEntity;
			postParameters.add(new BasicNameValuePair("api_sig", signature));
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			response = httpClient.execute(httpPost);
			String entityString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, entityString);
			returnValue = new JSONObject(entityString.substring(14));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public String getPhotoSet(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", "flickr.photosets.getList"));
		return "";
	}
	
	public enum PostMethod{
		GET_FROB, GET_PHOTOSET_LIST, GET_PHOTOSET, GET_COLLECTION_LIST, GET_COLLECTION, GET_AUTH_TOKEN;
	}
}
