package org.bibsonomy.opensocial.oauth.database.beans;
/**
 * 
 * @author sni
 * A combination of fields for deleting a specific OAuthAccess
 *
 */
public class OAuthParam {

	private String userID;
	private String accessToken;
	
	public OAuthParam(String userID, String accessToken){
		this.setUserID(userID);
		this.setAccessToken(accessToken);
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
