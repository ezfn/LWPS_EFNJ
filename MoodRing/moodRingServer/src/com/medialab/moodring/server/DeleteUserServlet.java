package com.medialab.moodring.server;

import java.io.IOException;

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
public class DeleteUserServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String error = null;

		try {
			String userId = req.getParameter("id");

			if (userId == null || userId.length() < 1) {
				error = "No user specified";
			} else {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Key key = KeyFactory.createKey(Constants.USER, userId);
				Entity user = null;
				try {
					user = datastore.get(key);
				} catch (EntityNotFoundException e) {
					// TODO - not sure about this approach
					user = null;
				}

				if (user != null) {
					datastore.delete(key);
				} else {
					error = "User '" + userId + "' does not exist.";
				}
			}
		} catch (Exception e) {
			error = "Unexpected error while deleting user:: " + e.getMessage();
		}

		if (error != null && error.length() > 0) {
			resp.getWriter().append(error);
		} else
			resp.getWriter().append("User deleted successfully.");
	}
}
