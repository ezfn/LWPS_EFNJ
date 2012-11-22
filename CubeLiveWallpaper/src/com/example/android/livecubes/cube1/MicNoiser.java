package com.example.android.livecubes.cube1;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;


//TODO: setAudioSamplingRate, setAudioEncodingBitRate
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class MicNoiser extends MediaRecorder {
	
    private static double mEMA = 0.0;
    private static final double EMA_FILTER = 0.6;
    private boolean is_started = false;//TODO: do we need this flag?
    public static boolean has_instance = false;
    
    public void re_init(){
    	this.setAudioSource(MediaRecorder.AudioSource.MIC);
		this.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		this.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		this.setOutputFile("/dev/null");
    }
    public MicNoiser(){
    	re_init();
    }
    
	public interface micFooListener{
		public void Foo_notify();
	}

	public void open_ears(){ 
		if (is_started)
			return;/*ears already open!*/
		try
		{           
			super.prepare();
		}catch (java.io.IOException ioe) {
			android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

		}catch (java.lang.SecurityException e) {
			android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
		}
		try
		{           
			super.start();
		}catch (java.lang.SecurityException e) {
			android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
		}
		Log.i("bla", "starting");
		is_started = true;
		//mEMA = 0.0;
	}

	/*TODO: listen to incoming/outgoing calls to close mic*/ 
	public void close_ears() {
		if (!is_started)
			return;
		Log.i("bla", "stopping");
		super.stop();
		is_started = false;
	}
	
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
        return  (this.getMaxAmplitude());
    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
    
    public boolean is_started(){
    	return is_started();
    }

}
