package com.medialab.moodring.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
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

public class UserListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		fillFriendsList();
	}

	private String getCurrentUserId() {
		return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("user_id", null);
	}

	protected void fillFriendsList() {


		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				//Debug.waitForDebugger();
				final String[] users = ServerCalls.getUsers();

				runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, users);
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
									Toast.makeText(UserListActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
									return;
								}

								final String to = textView.getText().toString();
								if (to == null || to.length() < 1) {
									Toast.makeText(UserListActivity.this, "No recipient specified", Toast.LENGTH_SHORT).show();
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


}
