package org.bibsonomy.importer.bookmark.service;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class DeliciousSignPost implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -6998612190700927048L;
    
	private OAuthConsumer consumer;
	private OAuthProvider provider;
	
	public DeliciousSignPost(
		String consumerKey,
		String consumerSecret,
		String requestTokenEndpointUrl,
	    	String accessTokenEndpointUrl,
	    	String authorizationWebsiteUrl) {
		consumer = new DefaultOAuthConsumer(
			consumerKey,
			consumerSecret);
		provider = new DefaultOAuthProvider(
			requestTokenEndpointUrl,
			accessTokenEndpointUrl,
			authorizationWebsiteUrl);
	}

	public String getRequestToken(String callbackUrl) {
		String authUrl;
		try {
			authUrl = provider.retrieveRequestToken(consumer, callbackUrl);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
			return null;
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
			return null;
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
			return null;
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
			return null;
		}
	
		return authUrl;
	}

	public void getAccessToken(String pin) {
		try {
			provider.retrieveAccessToken(consumer, pin);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}
//	
//	public void setTokenWithSecret(String arg0, String arg1) {
//	    consumer.setTokenWithSecret(arg0, arg1);
//	}
	
	public HttpURLConnection sign(URL url) {
		HttpURLConnection request;
		try {
			request = (HttpURLConnection) url.openConnection();
			return sign(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public HttpURLConnection sign(HttpURLConnection request) {
		try {
			consumer.sign(request);
			return request;
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
