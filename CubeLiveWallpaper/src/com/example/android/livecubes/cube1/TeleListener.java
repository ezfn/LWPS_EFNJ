package com.example.android.livecubes.cube1;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TeleListener extends PhoneStateListener {
	
	callListener listener;
	
	public TeleListener (TelephonyManager mTelephonyMgr, callListener requesting_listener){
		mTelephonyMgr.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
		listener = requesting_listener;
	}
	
	public interface callListener{
		public void notice_call();
		public void back_to_idle();
	}

	public void onCallStateChanged(int state, String incomingNumber)
	{
	/*	super.onCallStateChanged(state, incomingNumber);
		if (state == TelephonyManager.CALL_STATE_IDLE){
			listener.back_to_idle();
		}
		else{
			listener.notice_call();
		}*/

	}
	
	

}


