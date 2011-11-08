package org.bibsonomy.webapp.command.actions;

import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.User;


/**
 * command class for accessing OAuth services
 * 
 * @author fei
 * @version $Id$
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
}
