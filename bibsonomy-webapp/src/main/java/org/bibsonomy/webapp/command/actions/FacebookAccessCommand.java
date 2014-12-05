/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.actions;

import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.User;


/**
 * command class for accessing OAuth services
 * 
 * @author fei
 */
public class FacebookAccessCommand extends OAuthAccessCommand {
	/**
	 * capturing different error states
	 * @author fei
	 */
	public enum FacebookError {
		/**
		 * client obtains a request token 
		 */
		access_denied
	}

	/**
	 * capturing different error states
	 * @author fei
	 */
	public enum FacebookErrorReason {
		/**
		 * client obtains a request token 
		 */
		user_denied
	}

	/**
	 * admin actions for the facebook importer
	 * @author fei
	 */
	public enum FacebookAdminAction {
		/**
		 * build the index for resolving user names 
		 */
		BUILD_INDEX
	}

	/**
	 * social actions for the facebook importer
	 * @author fei
	 */
	public enum FacebookSocialAction {
		/**
		 * send invitation to the requested user 
		 */
		SEND_INVITATION
	}
	
	/** facebook's error code */
	private FacebookError error;
	/** facebook's error reason */
	private FacebookErrorReason error_reason;
	/** facbook's error description */
	private String error_description;
	/** temporary request token */
	private String code;
	/** administrative actions like building the resolver index */
	private FacebookAdminAction adminAction;
	/** social interaction, e.g. send invitation */
	private FacebookSocialAction socialAction;
	
	/** list of imported friends */
	private Collection<User> friends;
	
	/** maps facebook user ids to possible BibSonomy users */
	private Map<String, Collection<User>> userMapping;
	
	
	/**
	 * convenience method for facebook
	 * @param token
	 */
	public void setCode(String token) {
		this.code = token;
	}

	/**
	 * convenience method for facebook
	 * @return the auth token
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * @param error
	 */
	public void setError(FacebookError error) {
		this.error = error;
	}

	/**
	 * @return facbook's error code
	 */
	public FacebookError getError() {
		return error;
	}

	/**
	 * @param error_reason
	 */
	public void setError_reason(FacebookErrorReason error_reason) {
		this.error_reason = error_reason;
	}

	/**
	 * @return facbook's error reason
	 */
	public FacebookErrorReason getError_reason() {
		return error_reason;
	}

	/**
	 * @param error_description
	 */
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

	/**
	 * @return facbook's error description
	 */
	public String getError_description() {
		return error_description;
	}

	/**
	 * @param friends list of imported friends
	 */
	public void setFriends(Collection<User> friends) {
		this.friends = friends;
	}

	/**
	 * @return list of imported friends
	 */
	public Collection<User> getFriends() {
		return friends;
	}

	/**
	 * @param userMapping
	 */
	public void setUserMapping(Map<String, Collection<User>> userMapping) {
		this.userMapping = userMapping;
	}

	/**
	 * @return mapping from facebook user ids to possibly matching BibSonomy users
	 */
	public Map<String, Collection<User>> getUserMapping() {
		return userMapping;
	}

	/**
	 * @param adminAction
	 */
	public void setAdminAction(FacebookAdminAction adminAction) {
		this.adminAction = adminAction;
	}

	/**
	 * @return admin action
	 */
	public FacebookAdminAction getAdminAction() {
		return adminAction;
	}

	/**
	 * @param socialAction
	 */
	public void setSocialAction(FacebookSocialAction socialAction) {
		this.socialAction = socialAction;
	}

	/**
	 * @return requested social interaction command 
	 */
	public FacebookSocialAction getSocialAction() {
		return socialAction;
	}
}
