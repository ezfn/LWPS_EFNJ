package com.medialab.moodring.client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		String message = arg1.getStringExtra("message");
		notify(message);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		String currentUserId = getCurrentUserId();
		if (currentUserId != null && currentUserId.length() > 0) {
			String res = ServerCalls.register(currentUserId, arg1);
			System.out.println(res);
			saveGcmId(arg1);
			Intent intent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.putExtra("register", true);

			getApplication().startActivity(intent);
		}
	}

	private String getCurrentUserId() {
		return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("user_id", null);
	}

	private void saveGcmId(String arg1) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString("gcm_id", arg1);
		editor.commit();
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		saveGcmId(null);
	}

	private void notify(String message) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("New message!").setContentText(message);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, GcmOnMessageActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(GcmOnMessageActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}
