package com.medialab.moodring.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	String moodRingProjectNumber = "1089929367341";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		findViewById(R.id.work_layout).setVisibility(View.VISIBLE);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				final String[] users = ServerCalls.getUsers();

				runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
								android.R.id.text1, users);
						ListView listView = (ListView) findViewById(R.id.listview_users);
						listView.setAdapter(adapter);
						final TextView textView = (TextView) findViewById(R.id.textview_selected_to);
						listView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								String selected = users[arg2];
								textView.setText(selected);
							}
						});

						findViewById(R.id.button_send).setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								EditText messageEditText = (EditText) findViewById(R.id.edittext_message);
								final String body = messageEditText.getText().toString();
								if (body == null || body.length() < 1) {
									Toast.makeText(MainActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
									return;
								}

								final String to = textView.getText().toString();
								if (to == null || to.length() < 1) {
									Toast.makeText(MainActivity.this, "No recipient specified", Toast.LENGTH_SHORT).show();
									return;
								}

								new Thread(new Runnable() {
									public void run() {
										String res = ServerCalls.sendMessage(to, getCurrentUserId(), body);
										System.out.println(res);
									}
								}).start();
							}
						});
					}
				});
			}
		};

		new Thread(runnable).start();
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
						GCMRegistrar.register(MainActivity.this, moodRingProjectNumber);
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

	private String getCurrentUserId() {
		return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("user_id", null);
	}

}
