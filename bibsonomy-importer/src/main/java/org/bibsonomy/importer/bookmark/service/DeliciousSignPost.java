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
	
	/**
	 * Key for storing the DeliciousSignPost object into the session
	 */
	public static final String OAUTH_KEY = "org.bibsonomy.webapp.controller.DelicioursPinController.oAuthKey";

        private static final String CONSUMERKEY = "dj0yJmk9SFR2YlduTXU3T0p1JmQ9WVdrOU5scFhNV0pxTldrbWNHbzlNVFV3TlRBeU5ERTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0zZg--";
        private static final String CONSUMERSECRET = "7757c2c4be015ac123d7ba73a76a1dcf99998d1f";
        
        private static final String CALLBACK_URL = "http://www.biblicious.org/import/deliciousV2";
    
	private OAuthConsumer consumer;
	private OAuthProvider provider;
	
	public DeliciousSignPost() {
		consumer = new DefaultOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
		provider = new DefaultOAuthProvider("https://api.login.yahoo.com/oauth/v2/get_request_token",
		"https://api.login.yahoo.com/oauth/v2/get_token", "https://api.login.yahoo.com/oauth/v2/request_auth");
	}

	public String getRequestToken(
        		String importData,
        		boolean overwrite,
        		String ckey) {
		String authUrl;
		String callbackUrl = CALLBACK_URL;
		callbackUrl += "?" + "ckey=" + ckey;
		callbackUrl += "&" + "overwrite=" + overwrite;
		callbackUrl += "&" + "importData=" + importData;
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
