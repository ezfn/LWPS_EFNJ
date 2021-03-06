package com.medialab.moodring.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class GetUsersServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		/*DUBUG CODE:
		/*String userId = "Erez";
		Key key = KeyFactory.createKey(Constants.USER, userId);
		Entity newUser = null;
		try {
			newUser = datastore.get(key);
		} catch (EntityNotFoundException e) {
			newUser = null;
		}

		if (newUser == null) {
			newUser = new Entity(Constants.USER, userId);
		} else {
		}

		newUser.setProperty(Constants.GCM_ID, "asfdgfadsgadsfadesfadsfads");
		newUser.setProperty(Constants.DATE_REGISTERED, new Date());*/
		
		//Key retrievedKey = datastore.put(newUser);

		Query q = new Query(Constants.USER);
		PreparedQuery preparedQuery = datastore.prepare(q);

		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (Entity result : preparedQuery.asIterable()) {
			if (!first) {
				builder.append("@");
			}
			first = false;
			builder.append(result.getKey().getName());

		}
		resp.getWriter().append(builder.toString());
	}
}
