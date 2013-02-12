package com.medialab.moodring.client;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//if (!isDataServiceRunning())
		startService(new Intent(this, DataCollectionService.class));
		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean success = intent.getBooleanExtra("register", false);
		final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);

		if (success || !regId.equals("")) {
			showStandardUi();
		} else {
			showRegistrationUi();
		}
	}



	private void showStandardUi() {
		findViewById(R.id.registration_layout).setVisibility(View.GONE);
		findViewById(R.id.home_layout).setVisibility(View.VISIBLE);

		findViewById(R.id.button_friends).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UserListActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}



	private void showRegistrationUi() {
		findViewById(R.id.button_register).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText username = (EditText) findViewById(R.id.edittext_username);
				CharSequence user = username.getText();
				if (user != null && user.length() > 0 && !user.toString().contains("@")) {
					saveUserId(user);

					GCMRegistrar.checkDevice(MainActivity.this);
					GCMRegistrar.checkManifest(MainActivity.this);
					final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);

					if (regId.equals("")) {
						GCMRegistrar.register(MainActivity.this, Constants.moodRingProjectNumber);
					} else {
						Toast.makeText(MainActivity.this, "Already registered!", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(MainActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
				}
			}

			private void saveUserId(CharSequence username) {
				Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				editor.putString("user_id", username.toString());
				editor.commit();
			}
		});
	}
	
	private boolean isDataServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(MainActivity.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (DataCollectionService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}


}
