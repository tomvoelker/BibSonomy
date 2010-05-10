package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author Clemens Baier
 */
public class UserActivationCommand extends BaseCommand implements Serializable {

	private static final long serialVersionUID = 952301302153030500L;
	
	/**
	 * the activation code
	 */
	private String activationCode = "";
	
	/**
	 * @return String
	 */
	public String getActivationCode() {
		return this.activationCode;
	}

	/**
	 * @param activationCode
	 */
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	
	

}
