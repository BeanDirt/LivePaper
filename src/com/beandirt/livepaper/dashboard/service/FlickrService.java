package com.beandirt.livepaper.dashboard.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class FlickrService implements IFlickrService{

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
	
	public FlickrService(){
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost(URL);
	}
	
	public FlickrService(String frob){
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
		case GET_COLLECTION_LIST: return getCollectionList();
		}
		return null;
	}
	
	private JSONObject getAuthToken(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("frob", FROB));
		postParameters.add(new BasicNameValuePair("method", PostMethod.GET_AUTH_TOKEN.getMethod()));
		
		try {
			postParameters.add(new BasicNameValuePair("api_sig", createSignature(postParameters)));
			UrlEncodedFormEntity formEntity;
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			return new JSONObject(EntityUtils.toString(response.getEntity()).substring(14));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JSONObject();
	}
	
	private JSONObject getFrob(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", PostMethod.GET_FROB.getMethod()));
		
        try {
			UrlEncodedFormEntity formEntity;
			formEntity = new UrlEncodedFormEntity(postParameters);
			postParameters.add(new BasicNameValuePair("api_sig", createSignature(postParameters)));
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			FROB = new JSONObject(EntityUtils.toString(response.getEntity()).substring(14)).getJSONObject("frob").getString("_content");
			
			StringBuffer buffer = new StringBuffer();
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
		}
        catch(Exception e){
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
		postParameters.add(new BasicNameValuePair("method", PostMethod.GET_PHOTOSET_LIST.getMethod()));
		
		try {
			UrlEncodedFormEntity formEntity;
			postParameters.add(new BasicNameValuePair("api_sig", createSignature(postParameters)));
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			String entityString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, entityString);
			returnValue = new JSONObject(entityString.substring(14));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public JSONObject getPhotoList(String photosetId){
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("auth_token", TOKEN));
		postParameters.add(new BasicNameValuePair("extras", "tags, original_format"));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", PostMethod.GET_PHOTO_LIST.getMethod()));
		postParameters.add(new BasicNameValuePair("photoset_id", photosetId));
				
		try {
			UrlEncodedFormEntity formEntity;
			postParameters.add(new BasicNameValuePair("api_sig", createSignature(postParameters)));
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			String entityString = EntityUtils.toString(response.getEntity());
			returnValue = new JSONObject(entityString.substring(14));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}
	
	private String createSignature(ArrayList<NameValuePair> postParameters){
		StringBuffer buffer = new StringBuffer();
		buffer.append(SECRET);
		
		for(NameValuePair param : postParameters){
			buffer.append(param.getName());
			buffer.append(param.getValue());
		}
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			String signature = ByteUtilities.toHexString(md.digest(buffer
					.toString().getBytes("UTF-8")));
			return signature;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public JSONObject getCollectionList(){
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("api_key", KEY));
		postParameters.add(new BasicNameValuePair("auth_token", TOKEN));
		postParameters.add(new BasicNameValuePair("format", "json"));
		postParameters.add(new BasicNameValuePair("method", PostMethod.GET_COLLECTION_LIST.getMethod()));
		
		
		try {
			UrlEncodedFormEntity formEntity;
			postParameters.add(new BasicNameValuePair("api_sig", createSignature(postParameters)));
			formEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
			String entityString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, entityString);
			returnValue = new JSONObject(entityString.substring(14));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//String something = PostMethod.GET_AUTH_TOKEN.getMethod();
		return returnValue;
		
	}
	
	public enum PostMethod{
		GET_FROB("flickr.auth.getFrob"), GET_PHOTOSET_LIST("flickr.photosets.getList"), GET_COLLECTION_LIST("flickr.collections.getTree"), GET_AUTH_TOKEN("flickr.auth.getToken"), GET_PHOTO_LIST("flickr.photosets.getPhotos");
		
		private String m;
		
		private PostMethod(String m){
			this.m = m;
		}
		
		public String getMethod(){
			return this.m;
		}
	}
}
