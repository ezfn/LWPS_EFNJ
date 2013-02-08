package com.nitzanj.server_test;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

public class Main {
	public static void main(String[] args) {
		msg();
	}

	private static void delUser() {
		try {

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
				//.setHost("nitzanj.appspot.com")
				.setHost("localhost")
				.setPort(8888)
				.setParameter("id", "Moshe")
				.setPath("/deluser");

			URI uri = builder.build();
			HttpPost httpPost =new HttpPost(uri);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				byte[] buffer = new byte[1024];
				String result = "";
				while (instream.read(buffer) > 0){
					result += new String(buffer);
				}
				try {
					result.trim();
					System.out.println(result);
				} finally {
					instream.close();
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void reg() {
		try {

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
				//.setHost("nitzanj.appspot.com")
				.setHost("localhost")
				.setPort(8888)
				.setParameter("id", "nitzan")
				.setParameter("gcm", "APA91bFu9q-xlvMCMtlSin9ZyNuMUWeBuL7FCOSm0Uh5NDVsnAa-Lukn25n2_zzrr7HANXe958FQnCHgkLVd5S2zg3jyNJocXO36eZlqx75E09dn7NPZbGFoWrlTeW1M4hJtFbWz3N3Y4QwPpCzVHe850jXBta0iN_OpB8CfsMtulA4-nYxKRck")
				.setPath("/register");

			URI uri = builder.build();
			HttpPost httpPost =new HttpPost(uri);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				byte[] buffer = new byte[1024];
				String result = "";
				while (instream.read(buffer) > 0){
					result += new String(buffer);
				}
				try {
					result.trim();
					System.out.println(result);
				} finally {
					instream.close();
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void msg() {
		try {

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
				//.setHost("nitzanj.appspot.com")
				.setHost("localhost")
				.setPort(8888)
				.setParameter("to", "nitzan")
				.setParameter("body", "test message?")
				.setPath("/sendmsg");

			URI uri = builder.build();
			HttpPost httpPost =new HttpPost(uri);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				byte[] buffer = new byte[1024];
				String result = "";
				while (instream.read(buffer) > 0){
					result += new String(buffer);
				}
				try {
					result.trim();
					System.out.println(result);
				} finally {
					instream.close();
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getUsers() {
		try {

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
				//.setHost("nitzanj.appspot.com")
				.setHost("localhost")
				.setPort(8888)
				.setPath("/getusers");

			URI uri = builder.build();
			HttpGet httpGet = new HttpGet(uri);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				byte[] buffer = new byte[256];
				String result = "";
				while (instream.read(buffer) > 0){
					result += new String(buffer);
				}
				try {
					result.trim();
					System.out.println(result);
				} finally {
					instream.close();
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
