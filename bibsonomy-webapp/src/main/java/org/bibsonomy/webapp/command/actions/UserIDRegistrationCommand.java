package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class UserIDRegistrationCommand extends BaseCommand implements Serializable {
	
	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1371638749968299277L;
	
	/**
	 * Holds the details of the user which wants to register (like name, email, password)
	 */
	private User registerUser;

	/**
	 * Registration step
	 */
	private int step = 1;
	
	private boolean rememberMe;
	
	/**
	 * @return register user
	 */
	public User getRegisterUser() {
		return this.registerUser;
	}
	
	/**
	 * Sets register user 
	 * @param registerUser
	 */
	public void setRegisterUser(User registerUser) {
		this.registerUser = registerUser;
	}
	
	/**
	 * @return registration step
	 */
	public int getStep() {
		return this.step;
	}
	
	/**
	 * Sets registration step
	 * @param step
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @return If the user wants to stay logged in. 
	 */
	public boolean getRememberMe() {
		return this.rememberMe;
	}

	/**
	 * @param rememberMe
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}	
}