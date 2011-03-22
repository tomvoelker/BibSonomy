package org.bibsonomy.importer.bookmark.service;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

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

	public String getRequestToken(String callbackUrl) throws Exception {
		String authUrl;
		authUrl = provider.retrieveRequestToken(consumer, callbackUrl);
		return authUrl;
	}

	public void getAccessToken(String pin) throws Exception {
		provider.retrieveAccessToken(consumer, pin);
	}
//	
//	public void setTokenWithSecret(String arg0, String arg1) {
//	    consumer.setTokenWithSecret(arg0, arg1);
//	}
	
	public HttpURLConnection sign(URL url) throws Exception {
		HttpURLConnection request;
		request = (HttpURLConnection) url.openConnection();
		return sign(request);
	}
	
	public HttpURLConnection sign(HttpURLConnection request) throws Exception {
		consumer.sign(request);
		return request;
	}
}
