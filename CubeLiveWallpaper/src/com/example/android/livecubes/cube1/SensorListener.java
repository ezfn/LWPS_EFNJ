package com.example.android.livecubes.cube1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorListener implements SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	someEventListener listener;
	
	public SensorListener(SensorManager given_sensor_manager, someEventListener requesting_listener){
		mSensorManager = given_sensor_manager;
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		listener = requesting_listener;
	}
	
	public interface someEventListener{
		public void notice();
	}
	
	
	public void start(){
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		
	}
	
	public void stop(){
		mSensorManager.unregisterListener(this, mSensor);
		
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		/* if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			 listener.notice();
		 }*/
	}
}


