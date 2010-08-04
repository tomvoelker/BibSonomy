package org.bibsonomy.events.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;
import org.springframework.beans.factory.annotation.Required;

public class RestEventManager implements EventManager {

	private String serviceUrl;
	
	@Override
	public Event getEvent(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerUser(final User user, final Event event, final String subEvent, final String address) {
		final NameValuePair[] data = {
			new NameValuePair("event", event.getName()),
			new NameValuePair("name", user.getName()),
			new NameValuePair("subEvent", subEvent),
			new NameValuePair("address", address)
		};
		doRequest("register", data);
	}

	private void doRequest(final String method, final NameValuePair[] data) {
		final PostMethod post = new PostMethod(serviceUrl + "/" + method);
		try {
			final HttpClient client = new HttpClient();
			
	        post.setRequestBody(data);

			final int result = client.executeMethod(post);
			
			if (HttpStatus.SC_OK == result) {
				/*
				 * everything is OK from the protocol side - check server answer
				 */
				final String response = post.getResponseBodyAsString();
			}
			
		} catch (final Exception e) {
			// TODO
		} finally {
			post.releaseConnection();
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
