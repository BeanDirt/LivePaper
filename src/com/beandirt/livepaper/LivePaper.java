package com.beandirt.livepaper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LivePaper extends WallpaperService {

	public Boolean engineStarted = false;
	
	private ExecutorService executor;
	private TimeCalculator timeCalculator;
	
	private static int SCREEN_WIDTH;
	private static int SCREEN_HEIGHT;
	
	@Override
	public void onCreate() {
		super.onCreate();
		executor = Executors.newSingleThreadExecutor();
		timeCalculator = new TimeCalculator();
		establishScreenDimensions();
	}
	
	@Override
	public Engine onCreateEngine() {
		return new LivePaperEngine();
	}

	private void establishScreenDimensions(){
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		SCREEN_WIDTH = display.getWidth();
		SCREEN_HEIGHT = display.getHeight();
	}
	
	class LivePaperEngine extends Engine{
		
		private BitmapManager bitmapManager;
		private Bitmap currentBitmap;
		private float xOffset;
		private Boolean toggle;
		
		LivePaperEngine(){
			engineStarted = true;
			toggle = false;
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z,
				Bundle extras, boolean resultRequested) {
			if (action.equals(WallpaperManager.COMMAND_TAP)) {
				toggle = !toggle;
		    	drawBitmap(toggle);
		    }
			return null;
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			// TODO Auto-generated method stub
			super.onCreate(surfaceHolder);
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
		}

		@Override
		public void onOffsetsChanged(final float xOffset, final float yOffset,
				final float xOffsetStep, final float yOffsetStep, final int xPixelOffset,
				final int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			
			Runnable offsetsChangedCommand = new Runnable() {
				public void run() {
					if (xOffsetStep != 0f) {
						setParallax(xOffset);
					}
				};
			};
			executor.execute(offsetsChangedCommand);
			drawBitmap(false);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			
			SunLocationUpdater sunLocation = new SunLocationUpdater(getApplicationContext(), new LocationUpdatedListener() {
				
				@Override
				public void onLocationChanged(float dawn, float dusk) {
					timeCalculator.setDawn(dawn);
					timeCalculator.setDusk(dusk);
				}
			});
			
			bitmapManager = new BitmapManager(timeCalculator, getResources());
			currentBitmap = bitmapManager.getBitmap();
			drawBitmap(false);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if(!visible){
				drawBitmap(false);
			}
		}
		
		private void setParallax(float xOffset) {
			this.xOffset = -(xOffset * (currentBitmap.getWidth() - SCREEN_WIDTH));
		}
		
		private void drawBitmap(Boolean debug) {
			
			currentBitmap = bitmapManager.getBitmap();
			
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = new Canvas();
			c = holder.lockCanvas();
			c.save();
			c.drawBitmap(currentBitmap,this.xOffset,0,null);
			if(debug){
				drawDebug(c);
			}
			c.restore();
			holder.unlockCanvasAndPost(c);
		}
		
		private void drawDebug(Canvas c){
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(16);
			paint.setFakeBoldText(true);
			paint.setAntiAlias(true);
			paint.setShadowLayer(2, 2, 2, Color.DKGRAY);
			
			float dayInterval = bitmapManager.getDayInterval();
			float nightInterval = bitmapManager.getNightInterval();
			
			String dayIntervalString = TimeCalculator.getPrettyTime(dayInterval);
			String nightIntervalString = TimeCalculator.getPrettyTime(nightInterval);
			
			String sunrise = TimeCalculator.getPrettyTime(timeCalculator.getDawn());
			String sunset = TimeCalculator.getPrettyTime(timeCalculator.getDusk());
			
			c.drawText("Today's sunrise: "+sunrise, 15, 70, paint);
			c.drawText("Today's sunset: "+sunset, 15, 100, paint);
			c.drawText("Rotate every " + dayIntervalString + " during the day.", 15, 130, paint);
			c.drawText("Rotate every " + nightIntervalString + " during the night.", 15, 160, paint);
		}
	}
}
