package org.bibsonomy.events.services;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.events.model.ParticipantDetails;
import org.bibsonomy.model.User;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author rja
 * @author mat
 * 
 */

public class RestEventManager implements EventManager {

	private String serviceUrl;

	public RestEventManager() {
		super();
	}

	@Override
	public Event getEvent(String id) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerUser(final User user, final Event event, final ParticipantDetails participantDetails) {
		final JSONObject json = new JSONObject();
		json.put("name", user.getName());
		json.put("badgeName", participantDetails.getBadgename());
		json.put("badgeInstitutionName", participantDetails.getBadgeInstitutionName());
		json.put("subEvent", participantDetails.getSubEvent());
		json.put("address", participantDetails.getAddress());
		json.put("hasPoster", participantDetails.getHasPoster());
		json.put("isPresenter", participantDetails.getIsPresenter());
		json.put("icq", participantDetails.getIcq());
		json.put("jabber", participantDetails.getJabber());
		json.put("msn", participantDetails.getMsn());
		json.put("skype", participantDetails.getSkype());
		json.put("facebook", participantDetails.getFacebook());
		json.put("flickr", participantDetails.getFlickr());
		json.put("linkedIn", participantDetails.getLinkedIn());
		json.put("researchGate", participantDetails.getResearchGate());
		json.put("twitter", participantDetails.getTwitter());
		json.put("xing", participantDetails.getXing());
		json.put("isVegetarian", participantDetails.getVegetarian());

		StringWriter out = new StringWriter();
		try {
			json.writeJSONString(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String jsonText = out.toString();
		doRequest("register", jsonText);

	}

	private void doRequest(final String method, String body) {
		final PostMethod post = new PostMethod(serviceUrl + "/" + method);
		int result = -1;
		try {
			final HttpClient client = new HttpClient();
			// FIXME - request entity
			post.setRequestEntity(new StringRequestEntity(body, null, null));
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
