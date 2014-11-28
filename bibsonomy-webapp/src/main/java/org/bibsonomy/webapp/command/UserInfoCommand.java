/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/** 
 * @author dzo
 */
public class UserInfoCommand extends BaseCommand {
	
	private String requestedUser;
	private String format;
	private boolean shareInformation;

	private User user;

	/**
	 * @param requestedUser the requestedUser to set
	 */
	public void setRequestedUser(final String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * @return the requestedUser
	 */
	public String getRequestedUser() {
		return requestedUser;
	}
	
	/**
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(final String format) {
		this.format = format;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the shareInformation
	 */
	public boolean isShareInformation() {
		return this.shareInformation;
	}

	/**
	 * @param shareInformation the shareInformation to set
	 */
	public void setShareInformation(boolean shareInformation) {
		this.shareInformation = shareInformation;
	}
}
