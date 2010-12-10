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
	 * the new password entered by the user
	 */
	private String newPassword;
	/**
	 * the copy of the new password entered by the user
	 */
	private String passwordCheck;
	
	/**
	 * the reminder hash sent to the user (containing his encryped password)
	 */
	private String reminderHash;
	
	
	
	// **********************************************************
	// getter / setter
	// **********************************************************	
	
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
	public void setReminderHash(String reminderHash) {
		this.reminderHash = reminderHash;
	}
	public String getReminderHash() {
		return reminderHash;
	}	

	
}
