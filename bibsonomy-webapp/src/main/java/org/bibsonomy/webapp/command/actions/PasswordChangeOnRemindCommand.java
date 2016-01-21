/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author daill
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
