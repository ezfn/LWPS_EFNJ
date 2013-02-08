package com.medialab.moodring.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String userid = req.getParameter("id");
		String gcmId = req.getParameter("gcm");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key key = KeyFactory.createKey(Constants.USER, userid);
		Entity newUser = null;
		try {
			newUser = datastore.get(key);
		} catch (EntityNotFoundException e) {
			newUser = null;
		}

		String responseMsg = "";
		if (newUser == null) {
			newUser = new Entity(Constants.USER, userid);
			newUser.setProperty(Constants.DATE_REGISTERED, new Date());
			responseMsg = "User created!";
		} else {
			responseMsg = "User already exists.";
		}

		newUser.setProperty(Constants.GCM_ID, gcmId);
		datastore.put(newUser);

		resp.getWriter().append(responseMsg);
	}
}
