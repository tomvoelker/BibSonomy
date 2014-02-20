package org.bibsonomy.opensocial.oauth.database.beans;

/**
 * A combination of fields for deleting a specific OAuthAccess
 * 
 * @author sni
 */
public class OAuthParam {

	private String username;
	private String accessToken;
	
	/**
	 * default constructor
	 * 
	 * @param username
	 * @param accessToken
	 */
	public OAuthParam(String username, String accessToken){
		this.setUsername(username);
		this.setAccessToken(accessToken);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return this.accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
