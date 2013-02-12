package com.medialab.moodring.client;

import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class ServerCalls {
	private final static String HOST = "http://ml-moodring.appspot.com";

	public static String register(String userId, String gcmId) {
		try {
			String url = String.format(HOST + "/register?id=%s&gcm=%s", userId, gcmId);
			HttpPost httpPost = new HttpPost(url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String result = "";

			if (entity != null) {
				InputStream instream = null;
				try {
					instream = entity.getContent();
					byte[] buffer = new byte[256];

					while (instream.read(buffer) > 0) {
						result += new String(buffer);
					}

				} finally {
					if (instream != null) {
						instream.close();
					}
				}
			}

			result.trim();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendMessage(String to, String from, String body) {
		
		long time = new Date().getTime();
		try {
			String url = HOST + "/sendmsg?to=" + to + "&from=" + from + "&body=" + body + "&time=" + time;

			HttpPost httpPost = new HttpPost(url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String result = "";

			if (entity != null) {
				InputStream instream = null;
				try {
					instream = entity.getContent();
					byte[] buffer = new byte[1024];

					while (instream.read(buffer) > 0) {
						result += new String(buffer);
					}

				} finally {
					if (instream != null) {
						instream.close();
					}
				}
			}
			
			result.trim();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static String[] getUsers() {
		try {
			String url = HOST + "/getusers";
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String result = "";

			if (entity != null) {
				InputStream instream = null;
				try {
					instream = entity.getContent();
					byte[] buffer = new byte[256];

					while (instream.read(buffer) > 0) {
						result += new String(buffer);
					}

				} finally {
					if (instream != null) {
						instream.close();
					}
				}
			}
			
			result.trim();
			String[] users = result.split("@");
			for (int i = 0; i < users.length; i++) {
				users[i] = users[i].trim();
			}
			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
