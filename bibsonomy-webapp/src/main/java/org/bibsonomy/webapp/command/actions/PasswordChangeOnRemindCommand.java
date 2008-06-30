package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordChangeOnRemindCommand extends BaseCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8544593363734488269L;
	/**
	 * the username with the ative temporary password
	 */
	private String userName;
	/**
	 * the temporary password itself
	 */
	private String tmpPassword;
	/**
	 * the new password entered by the user
	 */
	private String newPassword;
	/**
	 * the copy of the new password entered by the user
	 */
	private String passwordCheck;
	
	/**
	 * @return String
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}
	/**
	 * @return String
	 */
	public String getTmpPassword() {
		return this.tmpPassword;
	}
	/**
	 * @param tmpPassword
	 */
	public void setTmpPassword(final String tmpPassword) {
		this.tmpPassword = tmpPassword;
	}
	/**
	 * @return String
	 */
	public String getNewPassword() {
		return this.newPassword;
	}
	/**
	 * @param newPassword
	 */
	public void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}
	/**
	 * @return String
	 */
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
	/**
	 * @param passwordCheck
	 */
	public void setPasswordCheck(final String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	
}
