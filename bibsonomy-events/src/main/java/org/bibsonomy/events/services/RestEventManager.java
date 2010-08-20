package org.bibsonomy.events.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;
import org.springframework.beans.factory.annotation.Required;

public class RestEventManager implements EventManager {

	private String serviceUrl;

	@Override
	public Event getEvent(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerUser(final User user, final Event event, final String subEvent, final String address) {
		final NameValuePair[] data = {
				new NameValuePair("event", event.getId()),
				new NameValuePair("name", user.getName()),
				new NameValuePair("subEvent", subEvent),
				new NameValuePair("address", address)
		};
		doRequest("register", data);
	}

	private void doRequest(final String method, final NameValuePair[] data) {
		final PostMethod post = new PostMethod(serviceUrl + "/" + method);
		int result = -1;
		try {
			final HttpClient client = new HttpClient();
			post.setRequestBody(data);
			result = client.executeMethod(post);
		} catch (final Exception e) {
			// re-throw
			throw new RuntimeException(e.getMessage());
		} finally {
			post.releaseConnection();
		}
		/*
		 * check response
		 */
		switch (result) {
		case HttpStatus.SC_CREATED: 
			/*
			 * user has been successfully registered
			 */
			return;
		case HttpStatus.SC_FORBIDDEN:
			/*
			 * user is already registered -> throw appropriate exception
			 */
			throw new InvalidModelException("already registered");
		default:
			throw new RuntimeException("got unknown http status code " + result);
		}
	}

	public String getServiceUrl() {
		return serviceUrl;
	}
	@Required
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
