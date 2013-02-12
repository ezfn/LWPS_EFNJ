package com.medialab.moodring.client;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.medialab.moodring.client.GPSTracker.locationChangedListener;


public class DataCollectionService extends Service {
	
	private String GPSFILENAME = "gpslogWWW.csv";
	private String CLGFILENAME = "calllogWWW.csv";
	private String SMSFILENAME = "smslogWWW.csv";
	private String CLDFILENAME = "calendarWWW.csv";
	private GPSTracker gpsTracker;
	private locationChangedListener locationListener = new locationChangedListener() {
		
		public void getNewLocation(Location new_location) {
			LogMethods.gpsLogger(new_location,  GPSFILENAME);	
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		updateLogs();
		LogMethods.prepareGpsFile(GPSFILENAME);
		gpsTracker = new GPSTracker(this, locationListener);
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	
	private void updateLogs(){
		
		ContentResolver cr =getContentResolver();
		LogMethods.dumpCalls(cr, CLGFILENAME);
		LogMethods.dumpSms(cr, SMSFILENAME);
		LogMethods.dumpCalender(cr, CLDFILENAME);
	}
	

}
