package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.UserInfoCommand;

/**
 * command class for accessing OAuth services
 * 
 * @author fei
 * @version $Id$
 */
public class OAuthAccessCommand extends UserInfoCommand {
	/**
	 * different states during the OAuth process
	 * @author fei
	 */
	public enum State {
		/**
		 * client obtains a request token 
		 */
		REQUEST, 
		/**
		 * client requests user to authorize the token
		 */
		ACCESS, 
		/**
		 * the user authorized the token
		 */
		AUTHORIZED
	}
	
	/**
	 * possible error types during oauth connections
	 * @author fei
	 */
	public enum ErrorType {
		/**
		 * general oauth exception
		 */
		OAuthException
	}

	/** current step of the OAuth dance */
	private State state;
	/** the authentication token */
	private String accessToken;
	/** consumer key */
	private String consumerKey;
	/** consumer secret */
	private String consumerSecret;
	/** callback url */
	private String callbackUrl;
	/** url for requesting a temporary token */
	private String requestTokenEndpointUrl;
	/** url for authorizing a temporary token */
	private String authorizationUrl;
	/** url for finally creating an access token */
	private String accessTokenEndpointUrl;
	/** in case of an authentication error the oauth error message */
	private String errorMessage;
	/** in case of an authentication error the oauth error type */
	private ErrorType errorType;
	
	/**
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return current OAuth state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param token
	 */
	public void setAccessToken(String token) {
		this.accessToken = token;
	}

	/**
	 * @return the auth token
	 */
	public String getAccessToken() {
		return accessToken;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
	
}
