package org.bibsonomy.opensocial.oauth.database.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nilsraabe
 *
 * Data structure representing the OAuth applications of a user
 */
public class OAuthUserInfo {
	
	  public static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000L;
	  public static final long FIVE_MINUTES = 5 * 60 * 1000L;
	
	  
	  private String 	appId;
	  private String	appTitle;
	  private String	appIconUrl;
	  private String 	userId;
	  private boolean 	authorized;
	  private String 	consumerKey;
	  private int 		type;
	  private String 	domain;
	  private String 	oauthVersion;
	  private boolean 	isExpired;
	  private Date 		issueTime;
	  private String 	issueTimeString;
	  private Date 		expirationTime;
	  private String 	expirationTimeString;
	
	  	
	/**
	 * Create a nice looking String from the Date
	 */
	public String formatDate(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat();
		return formater.format(date);
	}
	
	/**
	 * Calculate and set the variables expirationTime and isExpired
	 */
	public void calculateExpirationTime() {
		
		/**
		 * Calculate expiration time and set it
		 */

		long expTime = issueTime.getTime();
	    
	    switch (type) {
	    case 0:
	      expTime += FIVE_MINUTES;
	      break;
	    case 1:
	      expTime += ONE_YEAR;
	      break;
	    }
	    
	    this.setExpirationTime(new Date(expTime));

	    /**
	     * Define and set the bool isExpired
	     */
	    Date currentDate = new Date();
	    this.setExpired(currentDate.compareTo(this.getExpirationTime()) > 0);
	}

	/**
	 * @return the isExpired
	 */
	public boolean isExpired() {
		return isExpired;
	}

	/**
	 * @param isExpired the isExpired to set
	 */
	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	/**
	 * @return the expiresAt
	 */
	public Date getExpirationTime() {
		return expirationTime;
	}

	/**
	 * @param expiresAt the expiresAt to set
	 */
	public void setExpirationTime(Date expiresAt) {
		this.expirationTime = expiresAt;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the authorized
	 */
	public boolean isAuthorized() {
		return authorized;
	}

	/**
	 * @param authorized the authorized to set
	 */
	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	/**
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}

	/**
	 * @param consumerKey the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the issueTime
	 */
	public Date getIssueTime() {
		return issueTime;
	}

	/**
	 * @param issueTime the issueTime to set
	 */
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the oauthVersion
	 */
	public String getOauthVersion() {
		return oauthVersion;
	}

	/**
	 * @param oauthVersion the oauthVersion to set
	 */
	public void setOauthVersion(String oauthVersion) {
		this.oauthVersion = oauthVersion;
	}

	/**
	 * @return the appTitle
	 */
	public String getAppTitle() {
		return appTitle;
	}

	/**
	 * @param appTitle the appTitle to set
	 */
	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	/**
	 * @return the appIconUrl
	 */
	public String getAppIconUrl() {
		return appIconUrl;
	}

	/**
	 * @param appIconUrl the appIconUrl to set
	 */
	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	/**
	 * @return the issueTimeString
	 */
	public String getIssueTimeString() {
		return issueTimeString;
	}

	/**
	 * @param issueTimeString the issueTimeString to set
	 */
	public void setIssueTimeString(String issueTimeString) {
		this.issueTimeString = issueTimeString;
	}

	/**
	 * @return the expirationTimeString
	 */
	public String getExpirationTimeString() {
		return expirationTimeString;
	}

	/**
	 * @param expirationTimeString the expirationTimeString to set
	 */
	public void setExpirationTimeString(String expirationTimeString) {
		this.expirationTimeString = expirationTimeString;
	}
}
