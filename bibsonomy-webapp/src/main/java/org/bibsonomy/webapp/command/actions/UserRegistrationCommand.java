package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/** 
 * This command basically encapsulates the user for the registration page. 
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationCommand extends BaseCommand implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051289358685799133L;
	private User registerUser;

	/** 
	 * @return The user which tries to register.
	 */
	public User getRegisterUser() {
		return this.registerUser;
	}

	/**
	 * @param registerUser - the user which tries to register.
	 */
	public void setRegisterUser(User registerUser) {
		this.registerUser = registerUser;
	}

}
