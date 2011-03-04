package org.bibsonomy.importer.bookmark.service;

public class DeliciousSignPostManager {
    
    private String oAuthKey;
    
    private String consumerKey;
    private String consumerSecret;
    
    private String callbackBaseUrl;
    
    private String requestTokenEndpointUrl;
    private String accessTokenEndpointUrl;
    private String authorizationWebsiteUrl;

    public DeliciousSignPost createDeliciousSignPost() {
	return new DeliciousSignPost(
		consumerKey,
		consumerSecret,
		requestTokenEndpointUrl,
		accessTokenEndpointUrl,
		authorizationWebsiteUrl);
    }

	public void setoAuthKey(String oAuthKey) {
	    this.oAuthKey = oAuthKey;
	}

	public String getoAuthKey() {
	    return oAuthKey;
	}

	public void setConsumerKey(String consumerKey) {
	    this.consumerKey = consumerKey;
	}

	public String getConsumerKey() {
	    return consumerKey;
	}

	public void setConsumerSecret(String consumerSecret) {
	    this.consumerSecret = consumerSecret;
	}

	public String getConsumerSecret() {
	    return consumerSecret;
	}

	public void setCallbackBaseUrl(String callbackBaseUrl) {
	    this.callbackBaseUrl = callbackBaseUrl;
	}

	public String getCallbackBaseUrl() {
	    return callbackBaseUrl;
	}

	public void setRequestTokenEndpointUrl(String requestTokenEndpointUrl) {
	    this.requestTokenEndpointUrl = requestTokenEndpointUrl;
	}

	public String getRequestTokenEndpointUrl() {
	    return requestTokenEndpointUrl;
	}

	public void setAccessTokenEndpointUrl(String accessTokenEndpointUrl) {
	    this.accessTokenEndpointUrl = accessTokenEndpointUrl;
	}

	public String getAccessTokenEndpointUrl() {
	    return accessTokenEndpointUrl;
	}

	public void setAuthorizationWebsiteUrl(String authorizationWebsiteUrl) {
	    this.authorizationWebsiteUrl = authorizationWebsiteUrl;
	}

	public String getAuthorizationWebsiteUrl() {
	    return authorizationWebsiteUrl;
	}
}
