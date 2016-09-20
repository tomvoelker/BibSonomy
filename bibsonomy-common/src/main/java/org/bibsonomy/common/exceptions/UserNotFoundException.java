package org.bibsonomy.common.exceptions;

/**
 * User not found exception. Takes the username as input.
 *
 * @author niebler
 */
public class UserNotFoundException extends RuntimeException {

	private final String username;

	public UserNotFoundException(final String username) {
		super("The requested user " + username + " was not found.");
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}
}
