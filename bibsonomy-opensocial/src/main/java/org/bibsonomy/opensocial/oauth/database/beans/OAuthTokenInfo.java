/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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