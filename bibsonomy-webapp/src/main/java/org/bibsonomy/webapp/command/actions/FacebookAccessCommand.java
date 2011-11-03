package org.bibsonomy.webapp.command.actions;

import java.util.Collection;

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

	/** facebook's error code */
	private FacebookError error;
	/** facebook's error reason */
	private FacebookErrorReason error_reason;
	/** facbook's error description */
	private String error_description;
	/** temporary request token */
	private String code;
	
	/** list of imported friends */
	private Collection<User> friends;
	
	
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
}
