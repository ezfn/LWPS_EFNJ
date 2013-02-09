package com.medialab.moodring.client;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;


public class DataCollectionService extends Service {
	
	private String GPSFILENAME = "gpslog0019.csv";
	private String CLGFILENAME = "calllog0019.csv";
	private String SMSFILENAME = "smslog1615.csv";
	private String CLDFILENAME = "calendar0019.csv";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		prepareLogFiles();
		updateLogs();
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	

	
	private void prepareLogFiles(){
		LogMethods.appendLog("\"TIME\",\"latitude\",\"longitude\"",  GPSFILENAME);
	}
	
	private void updateLogs(){
		
		ContentResolver cr =getContentResolver();
		LogMethods.dumpCalls(cr, CLGFILENAME);
		LogMethods.dumpSms(cr, SMSFILENAME);
		LogMethods.dumpCalender(cr, CLDFILENAME);
	}
	

}
