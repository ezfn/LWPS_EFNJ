package com.medialab.moodring.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class SendMessageServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Result result = null;
		String error = null;

		try {
			String from = req.getParameter("from");
			String to = req.getParameter("to");
			String body = req.getParameter("body");

			if (to == null || to.length() < 1) {
				error = "No recipient specified";
			} else if (body == null || body.length() < 1) {
				error = "Message body is empty";
			} else {

				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Key key = KeyFactory.createKey(Constants.USER, to);
				Entity user = null;
				try {
					user = datastore.get(key);
				} catch (EntityNotFoundException e) {
					// TODO - not sure about this approach
					user = null;
				}

				if (user != null) {
					String gcmId = user.getProperty(Constants.GCM_ID).toString();
					Sender sender = new Sender(Constants.API_SERVER_KEY);
					Message message = new Message.Builder().addData("message", body).build();
					result = sender.send(message, gcmId, 5);

					if (result.getMessageId() != null) {
						String canonicalRegId = result.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration ID:
							// update database
						} else {
							error = result.getErrorCodeName();

							if (error != null) {
								if (error.equals(com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED)) {
								}
								// application has been removed from device -
								// unregister user from database
							}
						}
					}
					Entity newMessage = null;
					newMessage = new Entity(Constants.BODY, body);
					newMessage.setProperty(Constants.FROM, from);
					newMessage.setProperty(Constants.TO, to);
					newMessage.setProperty(Constants.DATE_REGISTERED, new Date());
					datastore.put(newMessage);

				} else {
					error = "User not found";
				}
			}
		} catch (Exception e) {
			error = "Unexpected error while sending message: " + e.getMessage();
		}

		if (error != null && error.length() > 0) {
			resp.getWriter().append(error);
		} else if (result != null) {
			resp.getWriter().append("Message sent, msgId: " + result.getMessageId());
		} else {
			resp.getWriter().append("Not sure what happened...");
		}
	}
}
