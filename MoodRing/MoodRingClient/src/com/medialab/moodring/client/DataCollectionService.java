package com.medialab.moodring.client;




import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import com.medialab.moodring.client.GPSTracker.locationChangedListener;


public class DataCollectionService extends Service {
	

	private static Timer timer = new Timer();
	private GPSTracker gpsTracker;
	Context context;
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
		context = this;
		gpsTracker = new GPSTracker(this, locationListener);
		LogMethods.prepareGpsFile(FileNames.GPSFILENAME);
		LogMethods.prepareMsgFile(FileNames.MSGFILENAME);
	}
	
	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//timer.scheduleAtFixedRate(new updateTask(), 10000, 86400000);//once a day
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	

    private class updateTask extends TimerTask
    { 
        public void run() 
        {
        	ContentResolver cr =getContentResolver();
    		LogMethods.dumpCalls(cr, FileNames.CLGFILENAME);
    		LogMethods.dumpSms(cr, FileNames.SMSFILENAME);
    		LogMethods.dumpCalender(cr, FileNames.CLDFILENAME);
    		sendEmail();
        }
    } 
    

    private void sendEmail(){
    	Intent intent = new Intent(context, EmailSenderActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    	
    	//} catch (android.content.ActivityNotFoundException ex) {
    	    //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	//}
    }
	

}
