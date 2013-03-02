package com.medialab.moodring.client;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class EmailSenderActivity extends Activity {
	
	private ArrayList<Uri> uriList = new ArrayList<Uri>();
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"yaeli.j.cohen@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Moodring daily logs");
		i.putExtra(Intent.EXTRA_TEXT   , "This is an automated message, sending moodring logs for today");
		fillUriList();
		
		i.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uriList);
	   this.startActivity(Intent.createChooser(i, "Send mail..."));
	};
	
	private void fillUriList(){
		uriList.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FileNames.GPSFILENAME)));
		uriList.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FileNames.MSGFILENAME)));
		uriList.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FileNames.CLGFILENAME)));
		uriList.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FileNames.SMSFILENAME)));
		uriList.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), FileNames.CLDFILENAME)));
	}
	

}
