package com.beandirt.livepaper.util;

import android.content.Context;

public class FileManager {
	
	public static boolean isDownloaded(Context context, long collectionId, long photosetId){
		return isDownloaded(context, String.valueOf(collectionId), String.valueOf(photosetId));
	}
	
	public static boolean isDownloaded(Context context, String collectionId, String photosetId){
		//File rootDir = context.getDir(photosetId, Activity.MODE_PRIVATE);
		return false;
	}
}
