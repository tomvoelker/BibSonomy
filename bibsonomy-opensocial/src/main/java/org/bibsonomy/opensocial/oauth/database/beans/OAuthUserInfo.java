/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.opensocial.oauth.database.beans;

import java.util.Date;

/**
 * @author nilsraabe
 *
 * Data structure representing the OAuth applications of a user
 */
public class OAuthUserInfo {
	
	/**
	 * These constants are used to generate the variables expirationTime and isExpired for every OAuth-consumer.
	 * There are two types of OAuth-consumers:
	 * 	1. The OAuth consumer which lifetime is up to 5 minutes
	 * 	2. The OAuth consumer which lifetime is up to one year
	 */
	public static final long ONE_YEAR = 365 * 24 * 60 * 60 * 1000L;
	public static final long FIVE_MINUTES = 5 * 60 * 1000L;
	
	private String appId;
	private String appTitle;
	private String appIconUrl;
	private String userId;
	private boolean authorized;
	private String consumerKey;
	private int type;
	private String domain;
	private String oauthVersion;
	private boolean isExpired;
	private Date issueTime;
	private Date expirationTime;
	private String accessToken;
	
	/**
	 * Calculate and set the variables expirationTime and isExpired
	 */
	public void calculateExpirationTime() {
		/*
		 * Calculate expiration time and set it
		 * According to the type of the OAuth-consumer, add the matching constant (Year/5 minutes) to the creation time.
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

		/*
		 * check if the token has expired
		 */
		final Date currentDate = new Date();
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
	 * 
	 * @return the accessToken
	 */
	public String getAccessToken(){
		return this.accessToken;
	}
	/**
	 * 
	 * @param accessToken The accessToken to set
	 */
	public void setAccessToken(String accessToken){
		this.accessToken=accessToken;
	}
}
