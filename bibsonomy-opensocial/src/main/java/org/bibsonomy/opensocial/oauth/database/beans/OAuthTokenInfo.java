package org.bibsonomy.opensocial.oauth.database.beans;

/**
 * Information about an access token.
 */
public class OAuthTokenInfo {
	/** the token owner */
	private String viewerId;
	/** the token */
	private String accessToken;
	/** the secret for the token */
	private String tokenSecret;
	/** the session handle (http://oauth.googlecode.com/svn/spec/ext/session/1.0/drafts/1/spec.html) */
	private String sessionHandle;
	/** time (milliseconds since epoch) when the token expires */
	private long tokenExpireMillis;
	
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setSessionHandle(String sessionHandle) {
		this.sessionHandle = sessionHandle;
	}
	public String getSessionHandle() {
		return sessionHandle;
	}
	public void setTokenExpireMillis(long tokenExpireMillis) {
		this.tokenExpireMillis = tokenExpireMillis;
	}
	public long getTokenExpireMillis() {
		return tokenExpireMillis;
	}
	public void setViewerId(String viewerId) {
		this.viewerId = viewerId;
	}
	public String getViewerId() {
		return viewerId;
	}
}