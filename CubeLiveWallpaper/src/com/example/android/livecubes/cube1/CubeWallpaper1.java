/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.livecubes.cube1;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/*
 * This animated wallpaper draws a rotating wireframe cube.
 */
public class CubeWallpaper1 extends WallpaperService {

	private final Handler mHandler = new Handler();
	private final static int FPS = 100;
	private final static int speed_effect_frames = FPS/2;
	final static ArrayList<Integer> foo_speed = new ArrayList<Integer>(speed_effect_frames);
	MicNoiser mNoiser = null;
	final ReentrantLock _noiserlock = new ReentrantLock(true);
	private static int live_engines = 0;//TODO: maybe use this to close mic sometimes


	@Override
	public void onCreate() {
		super.onCreate();
		for (int i = 0; i < speed_effect_frames && foo_speed.size() < speed_effect_frames; i++){
			foo_speed.add((int)(speed_effect_frames*2 - i*2));
		}
		_noiserlock.lock();
		mNoiser = new MicNoiser();
		mNoiser.open_ears();
		_noiserlock.unlock();
	}

	@Override
	public void onDestroy() {
		Log.i("bla", "live engines = " + live_engines);
		super.onDestroy();
		_noiserlock.lock();
		if (live_engines == 0){
			mNoiser.close_ears();
			mNoiser.release();
			mNoiser = null;
			Log.i("bla", "closed mic and died");
		}
		_noiserlock.unlock();
	}

	@Override
	public Engine onCreateEngine() {
		return new CubeEngine();
	}

	class CubeEngine extends Engine {

		private final Paint mPaint = new Paint();
		private float mOffset;
		private float mTouchX = -1;
		private float mTouchY = -1;
		private long mStartTime;
		private float mCenterX;
		private float mCenterY;
		SensorListener mSensorListener;
		TeleListener mTeleListener;
		final ArrayBlockingQueue<Integer> speed_q = new  ArrayBlockingQueue<Integer>(speed_effect_frames);
		private SensorManager mSensorManager;
		private TelephonyManager mTelephonyMgr;

		private final Runnable mDrawCube = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible;

		CubeEngine() {
			// Create a Paint to draw the lines for our cube
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);
			mStartTime = SystemClock.elapsedRealtime();
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			//android.os.Debug.waitForDebugger(); 

			while (speed_q.remainingCapacity() > 0){
				speed_q.add(1);//
			}
			// By default we don't get touch events, so enable them.
			setTouchEventsEnabled(true);

			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mSensorListener = new SensorListener(mSensorManager, null);

			mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			mTeleListener = new TeleListener(mTelephonyMgr, null);

			live_engines++;
			Log.i("bla", "live engines = " + live_engines);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			setTouchEventsEnabled(false);
			mHandler.removeCallbacks(mDrawCube);
			mSensorListener.stop();
			live_engines--;
			Log.i("bla", "live engines = " + live_engines);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				mSensorListener.start();
				if (live_engines == 1){
					_noiserlock.lock();
					mNoiser.open_ears();
					_noiserlock.unlock();
				}
				drawFrame();

			} else {
				mHandler.removeCallbacks(mDrawCube);
				mSensorListener.stop();
				if (live_engines == 1){
					_noiserlock.lock();
					mNoiser.close_ears();
					mNoiser.re_init();
					_noiserlock.unlock();
				}
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			// store the center of the surface, so we can draw the cube in the right spot
			mCenterX = width/2.0f;
			mCenterY = height/2.0f;
			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawCube);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xStep, float yStep, int xPixels, int yPixels) {
			mOffset = xOffset;
			drawFrame();
		}

		/*
		 * Store the position of the touch event so we can use it for drawing later
		 */
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				mTouchX = event.getX();
				mTouchY = event.getY();
			} else {
				mTouchX = -1;
				mTouchY = -1;
			}
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					// draw something
					drawCube(c);
					drawTouchPoint(c);
				}
			} finally {
				if (c != null) holder.unlockCanvasAndPost(c);
			}

			double amp = mNoiser.soundDb(30);
			if ( amp > 40){
				speed_q.clear();
				speed_q.addAll(foo_speed);
			}
			else
				if (speed_q.remainingCapacity() > 0)
					speed_q.add(1);


			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawCube);
			if (mVisible) {
				mHandler.postDelayed(mDrawCube, 1000 / speed_q.poll());
			}
		}

		/*
		 * Draw a wireframe cube by drawing 12 3 dimensional lines between
		 * adjacent corners of the cube
		 */
		void drawCube(Canvas c) {
			c.save();
			c.translate(mCenterX, mCenterY);
			c.drawColor(0xff000000);
			drawLine(c, -400, -400, -400,  400, -400, -400);
			drawLine(c,  400, -400, -400,  400,  400, -400);
			drawLine(c,  400,  400, -400, -400,  400, -400);
			drawLine(c, -400,  400, -400, -400, -400, -400);

			drawLine(c, -400, -400,  400,  400, -400,  400);
			drawLine(c,  400, -400,  400,  400,  400,  400);
			drawLine(c,  400,  400,  400, -400,  400,  400);
			drawLine(c, -400,  400,  400, -400, -400,  400);

			drawLine(c, -400, -400,  400, -400, -400, -400);
			drawLine(c,  400, -400,  400,  400, -400, -400);
			drawLine(c,  400,  400,  400,  400,  400, -400);
			drawLine(c, -400,  400,  400, -400,  400, -400);
			c.restore();
		}

		/*
		 * Draw a 3 dimensional line on to the screen
		 */
		void drawLine(Canvas c, int x1, int y1, int z1, int x2, int y2, int z2) {
			long now = SystemClock.elapsedRealtime();
			float xrot = ((float)(now - mStartTime)) / 1000;
			float yrot = (0.5f - mOffset) * 2.0f;
			float zrot = 0;

			// 3D transformations

			// rotation around X-axis
			float newy1 = (float)(Math.sin(xrot) * z1 + Math.cos(xrot) * y1);
			float newy2 = (float)(Math.sin(xrot) * z2 + Math.cos(xrot) * y2);
			float newz1 = (float)(Math.cos(xrot) * z1 - Math.sin(xrot) * y1);
			float newz2 = (float)(Math.cos(xrot) * z2 - Math.sin(xrot) * y2);

			// rotation around Y-axis
			float newx1 = (float)(Math.sin(yrot) * newz1 + Math.cos(yrot) * x1);
			float newx2 = (float)(Math.sin(yrot) * newz2 + Math.cos(yrot) * x2);
			newz1 = (float)(Math.cos(yrot) * newz1 - Math.sin(yrot) * x1);
			newz2 = (float)(Math.cos(yrot) * newz2 - Math.sin(yrot) * x2);

			// 3D-to-2D projection
			float startX = newx1 / (4 - newz1 / 400);
			float startY = newy1 / (4 - newz1 / 400);
			float stopX =  newx2 / (4 - newz2 / 400);
			float stopY =  newy2 / (4 - newz2 / 400);

			c.drawLine(startX, startY, stopX, stopY, mPaint);
		}

		/*
		 * Draw a circle around the current touch point, if any.
		 */
		void drawTouchPoint(Canvas c) {
			if (mTouchX >=0 && mTouchY >= 0) {
				c.drawCircle(mTouchX, mTouchY, 80, mPaint);
			}
		}
	}
}
