package com.medialab.moodring.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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
				final String[] users = ServerCalls.getUsers();

				runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, users);
						ListView listView = (ListView) findViewById(R.id.listview_users);
						listView.setAdapter(adapter);
						listView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								final String selected = users[arg2];
								OnClickListener voteListener = new OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog, final int which) {
										
										new Thread(new Runnable() {
											public void run() {
												String res = ServerCalls.sendMessage(selected, getCurrentUserId(), Integer.toString(which));
												System.out.println(res);
											}
										}).start();
										
										Toast.makeText(UserListActivity.this, "Thanks!", Toast.LENGTH_SHORT).show();								
									}
								};
								Builder builder = new AlertDialog.Builder(UserListActivity.this);
								AlertDialog dialog = builder.create();	
								dialog.setTitle("Would you say " + selected + " is:");
								dialog.setButton(AlertDialog.BUTTON_NEGATIVE, (CharSequence)"Relaxed", voteListener);
								dialog.setButton(AlertDialog.BUTTON_NEUTRAL, (CharSequence)"Neutral", voteListener);
								dialog.setButton(AlertDialog.BUTTON_POSITIVE, (CharSequence)"Stressed", voteListener);
								dialog.setCancelable(true);
								dialog.show();
							}
						});
					}
				});
			}
		};

		new Thread(runnable).start();
	}


}
