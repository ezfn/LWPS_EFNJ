package com.medialab.moodring.client;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.medialab.moodring.client.GPSTracker.locationChangedListener;


public class DataCollectionService extends Service {
	

	private GPSTracker gpsTracker;
	private locationChangedListener locationListener = new locationChangedListener() {
		
		public void getNewLocation(Location new_location) {
			LogMethods.logGps(new_location,  FileNames.GPSFILENAME);	
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		updateLogs();
		gpsTracker = new GPSTracker(this, locationListener);
		LogMethods.prepareGpsFile(FileNames.GPSFILENAME);
		LogMethods.prepareMsgFile(FileNames.MSGFILENAME);
	}
	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	
	private void updateLogs(){
		
		ContentResolver cr =getContentResolver();
		LogMethods.dumpCalls(cr, FileNames.CLGFILENAME);
		LogMethods.dumpSms(cr, FileNames.SMSFILENAME);
		LogMethods.dumpCalender(cr, FileNames.CLDFILENAME);
	}
	

}
